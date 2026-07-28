package com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.converters;


import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.UUID;

@ReadingConverter
public class UUIDConverter implements Converter<String, UUID> { //source, target

    @Override
    public UUID convert(String source) {
        return UUID.fromString(source);
    }
}
