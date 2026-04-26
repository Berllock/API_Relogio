package com.dev.watchapi.service;

import com.dev.watchapi.dto.AtualizarRelogioRequest;
import com.dev.watchapi.dto.CriarRelogioRequest;
import com.dev.watchapi.dto.PaginaRelogioDto;
import com.dev.watchapi.dto.RelogioDto;
import com.dev.watchapi.entity.Relogio;
import com.dev.watchapi.entity.enums.MaterialCaixa;
import com.dev.watchapi.entity.enums.TipoMovimento;
import com.dev.watchapi.entity.enums.TipoVidro;
import com.dev.watchapi.exception.NotFindException;
import com.dev.watchapi.mapper.RelogioMapper;
import com.dev.watchapi.repository.RelogioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tools.jackson.databind.cfg.MapperBuilder;

import java.time.Instant;
import java.util.UUID;

import static com.dev.watchapi.service.RelogioSpecs.*;

@Service
@RequiredArgsConstructor
public class RelogioService {

    private final RelogioRepository relogioRepository;
    private final RelogioMapper relogioMapper;


    public PaginaRelogioDto listar(
            int pagina,
            int porPagina,
            String busca,
            String marca,
            String tipoMovimento,
            String materialCaixa,
            String tipoVidro,
            Integer resistenciaMin,
            Integer resistenciaMax,
            Long precoMin,
            Long precoMax,
            Integer diametroMin,
            Integer diametroMax,
            String ordenar
    ) {
        int paginaSegura = Math.max(1, pagina);
        int porPaginaSegura = Math.min(60, Math.max(1, porPagina));

        TipoMovimento movimento = TipoMovimento.fromApi(tipoMovimento);
        MaterialCaixa material = MaterialCaixa.fromApi(materialCaixa);
        TipoVidro vidro = TipoVidro.fromApi(tipoVidro);

        OrdenacaoRelogios ordenacao = OrdenacaoRelogios.fromApi(ordenar);

        Sort sort = switch (ordenacao) {
            case MAIS_RECENTES -> Sort.by("criadoEm").descending();
            case PRECO_CRESC -> Sort.by("precoEmCentavos").ascending();
            case PRECO_DESC -> Sort.by("precoEmCentavos").descending();
            case DIAMETRO_CRESC -> Sort.by("diametroMm").ascending();
            case RESISTENCIA_DESC -> Sort.by("resistenciaAguaM").descending();
        };

        Pageable pageable = PageRequest.of(paginaSegura - 1, porPaginaSegura, sort);

        Specification<Relogio> spec = Specification.where(busca(busca))
                .and(marcaIgual(marca))
                .and(tipoMovimentoIgual(movimento))
                .and(materialCaixaIgual(material))
                .and(tipoVidroIgual(vidro))
                .and(resistenciaAguaEntre(resistenciaMin, resistenciaMax))
                .and(precoEntre(precoMin, precoMax))
                .and(diametroEntre(diametroMin, diametroMax));

        Page<Relogio> resultado = relogioRepository.findAll(spec, pageable);

        return new PaginaRelogioDto(
                resultado.getContent().stream()
                        .map(relogioMapper::toDto)
                        .toList(),
                resultado.getTotalPages()
        );
    }

    public RelogioDto buscarPorId (UUID id) {
        Relogio r = relogioRepository.findById(id)
                .orElseThrow(() -> new NotFindException("Relógio não encontrado: " + id));
        return relogioMapper.toDto(r);
    }

    public RelogioDto criar(CriarRelogioRequest req) {
        Relogio r = Relogio.builder()
                .id(UUID.randomUUID())
                .marca(req.marca())
                .modelo(req.modelo())
                .referencia(req.referencia())
                .tipoMovimento(TipoMovimento.fromApi(req.tipoMovimento()))
                .materialCaixa(MaterialCaixa.fromApi(req.materialCaixa()))
                .tipoVidro(TipoVidro.fromApi(req.tipoVidro()))
                .resistenciaAguaM(req.resistenciaAguaM())
                .diametroMm(req.diametroMm())
                .lugtoLugMm(req.lugToLugMm())
                .espessuraMm(req.espessuraMm())
                .larguraMm(req.larguraMm())
                .precoEmCentavos(req.precoEmCentavos())
                .urlImagem(req.urlImagem())
                .criadoEm(Instant.now())
                .build();
        return relogioMapper.toDto(relogioRepository.save(r));
    }

    public RelogioDto atualizar (UUID id, AtualizarRelogioRequest req) {
        Relogio r = relogioRepository.findById(id)
                .orElseThrow(() -> new NotFindException("Relógio não encontrado: " + id));

        r.setMarca(req.marca());
        r.setModelo(req.modelo());
        r.setReferencia(req.referencia());
        r.setTipoMovimento(TipoMovimento.fromApi(req.tipoMovimento()));
        r.setMaterialCaixa(MaterialCaixa.fromApi(req.materialCaixa()));
        r.setTipoVidro(TipoVidro.fromApi(req.tipoVidro()));
        r.setResistenciaAguaM(req.resistenciaAguaM());
        r.setDiametroMm(req.diametroMm());
        r.setLugtoLugMm(req.lugToLugMm());
        r.setEspessuraMm(req.espessuraMm());
        r.setLarguraMm(req.larguraMm());
        r.setPrecoEmCentavos(req.precoEmCentavos());
        r.setUrlImagem(req.urlImagem());
        return relogioMapper.toDto(relogioRepository.save(r));
    }

    public void remover (UUID id) {
        if (!relogioRepository.existsById(id)) {
            throw new NotFindException("Relogio não encontrado: " + id);
        }
        relogioRepository.deleteById(id);
    }
}
