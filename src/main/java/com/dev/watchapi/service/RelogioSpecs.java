package com.dev.watchapi.service;

import com.dev.watchapi.entity.Relogio;
import com.dev.watchapi.entity.enums.MaterialCaixa;
import com.dev.watchapi.entity.enums.TipoMovimento;
import com.dev.watchapi.entity.enums.TipoVidro;
import org.springframework.data.jpa.domain.Specification;

public class RelogioSpecs {

    private RelogioSpecs() {

    }
    
    public static Specification<Relogio> tudo() {
        return (root, query, cb) -> cb.conjunction();
    }

    public static boolean blank (String str) {
        return str == null || str.isBlank();
    }

    public static Specification<Relogio> busca (String termo) {
        if (blank(termo)) return tudo();

        String like = "%" + termo.toLowerCase() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("marca")), like),
                cb.like(cb.lower(root.get("modelo")), like),
                cb.like(cb.lower(root.get("referencia")), like)
        );
    }


    public static Specification<Relogio> marcaIgual(String marca) {
        if (blank(marca)) return tudo();
        return (root, q, cb) ->  cb.equal(root.get("marca"), marca);
    }

    public static Specification<Relogio> referenciaIgual(String referencia) {
        if (blank(referencia)) return tudo();
        return (root, q, cb) ->  cb.equal(root.get("referencia"), referencia);
    }

    public static Specification<Relogio> tipoMovimentoIgual(TipoMovimento tipoMovimento) {
        if (tipoMovimento == null) return tudo();
        return(root, q, cb) ->  cb.equal(root.get("tipoMovimento"), tipoMovimento);
    }

    public static Specification<Relogio> tipoVidroIgual(TipoVidro tipoVidro) {
        if (tipoVidro == null) return tudo();
        return (root, q, cb) ->  cb.equal(root.get("tipoVidro"), tipoVidro);
    }

    public static Specification<Relogio> materialCaixaIgual(MaterialCaixa materialCaixa) {
        if (materialCaixa == null) return tudo();
        return (root, q, cb) ->  cb.equal(root.get("materialCaixa"), materialCaixa);
    }

    public static Specification<Relogio> resistenciaAguaEntre(Integer min, Integer max) {
        if (min == null && max == null) return tudo();
        return (root, q, cb) -> {
            if (min != null && max != null) return cb.between(root.get("resistenciaAguaM"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("resistenciaAguaM"), min);
            return cb.lessThanOrEqualTo(root.get("resistenciaAguaM"), max);
        };
    }

    public static Specification<Relogio> precoEntre (Long min, Long max) {
        if (min == null && max == null) return tudo();
        return (root, q, cb) -> {
            if (min != null && max != null) return cb.between(root.get("precoEntre"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("precoEntre"), min);
            return cb.lessThanOrEqualTo(root.get("precoEntre"), max);
        };
    }

    public static Specification<Relogio> diametroEntre (Integer min, Integer max) {
        if (min == null && max == null) return tudo();
        return (root, q, cb) -> {
            if (min != null && max != null) return cb.between(root.get("diametroMm"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("diametroMm"), min);
            return cb.lessThanOrEqualTo(root.get("diametroMm"), max);
        };
    }

}
