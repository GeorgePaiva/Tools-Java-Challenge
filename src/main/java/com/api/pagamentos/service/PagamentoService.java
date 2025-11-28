package com.api.pagamentos.service;

import com.api.pagamentos.dto.*;
import com.api.pagamentos.entity.DescricaoEmbeddable;
import com.api.pagamentos.entity.FormaPagamentoEmbeddable;
import com.api.pagamentos.entity.PagamentoEntity;
import com.api.pagamentos.entity.enums.StatusTransacao;
import com.api.pagamentos.entity.enums.TipoPagamento;
import com.api.pagamentos.exception.ResourceNotFoundException;
import com.api.pagamentos.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    public static final String CARD_NUMBER_REGEX = "\\d{12,19}";

    @Transactional
    public PagamentoResponse createPayment(PagamentoRequest request) {

        this.validateRequest(request);

        var maskedCard = maskCard(request.getTransacao().getCartao());
        var status = determineStatus(request.getTransacao().getCartao());

        var descricao = buildDescricao(request, status);
        var formaPagamento = buildFormaPagamento(request.getTransacao().getFormaPagamento());

        var entity = new PagamentoEntity();
        entity.setCartao(maskedCard);
        entity.setDescricao(descricao);
        entity.setFormaPagamento(formaPagamento);

        pagamentoRepository.save(entity);
        return toResponse(entity);
    }

    private DescricaoEmbeddable buildDescricao(PagamentoRequest request, String status) {
        var descricao = new DescricaoEmbeddable();

        descricao.setValor(request.getTransacao().getDescricao().getValor());
        descricao.setDataHora(request.getTransacao().getDescricao().getDataHora());
        descricao.setEstabelecimento(request.getTransacao().getDescricao().getEstabelecimento());
        descricao.setNsu(generateNsu());
        descricao.setCodigoAutorizacao(generateCodigoAutorizacao());
        descricao.setStatus(status);
        return descricao;
    }

    private FormaPagamentoEmbeddable buildFormaPagamento(FormaPagamentoEmbeddable formaPagamentoDTO) {
        var pagamento = new FormaPagamentoEmbeddable();
        pagamento.setTipo(formaPagamentoDTO.getTipo());
        pagamento.setParcelas(formaPagamentoDTO.getParcelas());
        return pagamento;
    }

    @Transactional
    public PagamentoResponse refund(Long id) {
        var entity = pagamentoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Transação não encontrada: " + id));

        var statusAtual = entity.getDescricao().getStatus();

        if (StatusTransacao.CANCELADO.getValue().equalsIgnoreCase(statusAtual)) {
            return toResponse(entity);
        }

        entity.getDescricao().setStatus(StatusTransacao.CANCELADO.getValue());
        pagamentoRepository.save(entity);

        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public PagamentoResponse findById(Long id) {
        PagamentoEntity entity = pagamentoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Transação não encontrada: " + id));
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponse> findAll() {
        return pagamentoRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void validateRequest(PagamentoRequest req) {
        var forma = getFormaPagamentoDTO(req);

        var tipo = forma.getTipo();
        var isTipoInvalido = !(TipoPagamento.AVISTA.getValue().equalsIgnoreCase(tipo) || TipoPagamento.PARCELADO_LOJA
                .getValue().equalsIgnoreCase(tipo) || TipoPagamento.PARCELADO_EMISSOR.getValue().equalsIgnoreCase(tipo));

        if (isTipoInvalido) {
            throw new IllegalArgumentException("tipo de pagamento inválido");
        }
    }

    private static FormaPagamentoEmbeddable getFormaPagamentoDTO(PagamentoRequest req) {
        var valor = req.getTransacao().getDescricao().getValor();
        var cartao = req.getTransacao().getCartao();
        var forma = req.getTransacao().getFormaPagamento();

        if (valor == null || valor.compareTo(String.valueOf(BigDecimal.ZERO)) <= 0) {
            throw new IllegalArgumentException("valor deve ser maior que zero");
        }

        if (cartao == null || !cartao.matches(CARD_NUMBER_REGEX)) {
            throw new IllegalArgumentException("cartao inválido");
        }

        if (forma == null) {
            throw new IllegalArgumentException("formaPagamento é obrigatório");
        }
        return forma;
    }

    private String maskCard(String card) {
        if (card == null || card.length() <= 8) {
            return card;
        }

        var length = card.length();
        var first4 = card.substring(0, 4);
        var last4 = card.substring(length - 4);

        return first4 + "********" + last4;
    }

    private String determineStatus(String card) {
        if (card == null || card.isBlank()) {
            return StatusTransacao.NEGADO.getValue();
        }

        var lastChar = card.charAt(card.length() - 1);

        if (!Character.isDigit(lastChar)) {
            return StatusTransacao.NEGADO.getValue();
        }

        var lastDigit = Character.getNumericValue(lastChar);
        var isEven = lastDigit % 2 == 0;

        return isEven ? StatusTransacao.AUTORIZADO.getValue() : StatusTransacao.NEGADO.getValue();
    }

    private String generateNsu() {
        var randomBits = Math.abs(UUID.randomUUID().getMostSignificantBits());
        var digits = Long.toString(randomBits).replace("-", "");

        return digits.length() >= 10 ? digits.substring(0, 10) : String.format("%010d", randomBits).substring(0, 10);
    }

    private String generateCodigoAutorizacao() {
        var number = ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000);
        return Integer.toString(number);
    }

    private PagamentoResponse toResponse(PagamentoEntity entity) {

        var descricaoSrc = entity.getDescricao();
        var formaSrc = entity.getFormaPagamento();

        var transacao = getTransacaoDTO(entity, descricaoSrc);

        var forma = new FormaPagamentoDTO();
        forma.setTipo(formaSrc.getTipo());
        forma.setParcelas(formaSrc.getParcelas());

        var response = new PagamentoResponse();
        response.setTransacao(transacao);

        return response;
    }

    private static TransacaoDTO getTransacaoDTO(PagamentoEntity entity, DescricaoEmbeddable descricaoSrc) {
        var descricao = new DescricaoDTO();
        descricao.setValor(descricaoSrc.getValor());
        descricao.setDataHora(descricaoSrc.getDataHora());
        descricao.setEstabelecimento(descricaoSrc.getEstabelecimento());
        descricao.setNsu(descricaoSrc.getNsu());
        descricao.setCodigoAutorizacao(descricaoSrc.getCodigoAutorizacao());
        descricao.setStatus(descricaoSrc.getStatus());

        var transacao = new TransacaoDTO();
        transacao.setId(entity.getId());
        transacao.setCartao(entity.getCartao());
        transacao.setDescricao(descricao);
        transacao.setFormaPagamento(entity.getFormaPagamento());
        return transacao;
    }
}
