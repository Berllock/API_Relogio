package com.dev.watchapi.service;

import com.dev.watchapi.dto.PaginaRelogioDto;
import com.dev.watchapi.entity.Relogio;
import com.dev.watchapi.entity.enums.MaterialCaixa;
import com.dev.watchapi.entity.enums.TipoMovimento;
import com.dev.watchapi.entity.enums.TipoVidro;
import com.dev.watchapi.mapper.RelogioMapper;
import com.dev.watchapi.repository.RelogioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
        MaterialCaixa material = MaterialCaixa.fromApi(marca);
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

}
