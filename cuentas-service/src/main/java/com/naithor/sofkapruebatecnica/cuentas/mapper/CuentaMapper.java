package com.naithor.sofkapruebatecnica.cuentas.mapper;

import com.naithor.sofkapruebatecnica.cuentas.dto.CuentaDTO;
import com.naithor.sofkapruebatecnica.cuentas.entity.Cuenta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CuentaMapper {

    @Mapping(target = "id", ignore = true)
    Cuenta toEntity(CuentaDTO dto);

    CuentaDTO toDTO(Cuenta entity);

    List<CuentaDTO> toDTOList(List<Cuenta> entities);
}
