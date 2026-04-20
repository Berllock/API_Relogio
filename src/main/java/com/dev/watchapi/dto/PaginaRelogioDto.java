package com.dev.watchapi.dto;

import java.util.List;

public record PaginaRelogioDto(
        List<RelogioDto> itens,
        long total
) {
}
