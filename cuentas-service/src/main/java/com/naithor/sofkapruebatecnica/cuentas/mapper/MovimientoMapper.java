package com.naithor.sofkapruebatecnica.cuentas.mapper;

import com.naithor.sofkapruebatecnica.cuentas.dto.MovimientoDTO;
import com.naithor.sofkapruebatecnica.cuentas.entity.Movimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovimientoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cuenta", ignore = true)
    Movimiento toEntity(MovimientoDTO dto);

    MovimientoDTO toDTO(Movimiento entity);

    List<MovimientoDTO> toDTOList(List<Movimiento> entities);
}
