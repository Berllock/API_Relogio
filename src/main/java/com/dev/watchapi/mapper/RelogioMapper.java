package com.dev.watchapi.mapper;

import com.dev.watchapi.dto.RelogioDto;
import com.dev.watchapi.entity.Relogio;
import com.dev.watchapi.entity.enums.MaterialCaixa;
import com.dev.watchapi.entity.enums.TipoMovimento;
import com.dev.watchapi.entity.enums.TipoVidro;
import org.springframework.stereotype.Component;

@Component
public class RelogioMapper {

    public RelogioDto toDto(Relogio r) {
        return RelogioDto.builder()
                .id(r.getId())
                .marca(r.getMarca())
                .referencia(r.getReferencia())
                .tipoMovimento(r.getTipoMovimento().toApi())
                .materialCaixa(r.getMaterialCaixa().toApi())
                .tipoVidro(r.getTipoVidro().toApi())
                .resistenciaAguaM(r.getResistenciaAguaM())
                .diametroMm(r.getDiametroMm())
                .lugToLugMm(r.getLugtoLugMm())
                .espessuraMm(r.getEspessuraMm())
                .larguraMm(r.getLarguraLugMm())
                .precoEmCentavos(r.getPrecoEmCentavos())
                .urlImagem(r.getUrlImagem())
                .etiquetaResistenciaAgua(etiquetaResistencia(r.getResistenciaAguaM()))
                .pontuacaoColecionador(pontuacaoColecionador(r))
                .build();
    }

    private String etiquetaResistencia(int resistenciaM) {
        if (resistenciaM < 50) return "respingos";
        if (resistenciaM < 100) return "uso_diario";
        if (resistenciaM < 200) return "natacao";
        return "mergulho";
    }

    private int pontuacaoColecionador(Relogio r) {
        int pontos = 0;

        if (r.getTipoVidro() == TipoVidro.SAFIRA) pontos += 25;

        if (r.getResistenciaAguaM() >= 100) pontos += 15;
        if (r.getResistenciaAguaM() >= 200) pontos += 10;

        if (r.getTipoMovimento() == TipoMovimento.AUTOMATICO) pontos += 20;

        if (r.getMaterialCaixa() == MaterialCaixa.CERAMICA) pontos += 12;
        if (r.getMaterialCaixa() == MaterialCaixa.TITANIO) pontos += 12;

        if (r.getDiametroMm() >= 38 &&  r.getDiametroMm() <= 42) pontos += 8;

        return pontos;
    }
}
