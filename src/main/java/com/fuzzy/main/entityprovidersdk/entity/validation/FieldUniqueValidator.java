package com.fuzzy.main.entityprovidersdk.entity.validation;

import com.fuzzy.main.entityprovidersdk.entity.schema.SchemaField;
import com.fuzzy.main.entityprovidersdk.exception.runtime.SchemeValidationException;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldUniqueValidator implements Validator<Collection<SchemaField>> {
    @Override
    public boolean validate(Collection<SchemaField> source) {

        final Map<String, Long> duplicates = source.stream()
                .map(fieldSchema -> fieldSchema.getContainer().getName() + "." + fieldSchema.getName())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entryValue -> entryValue.getValue() > 1)
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        if (!duplicates.isEmpty()) {
            final String errorMessage = duplicates.entrySet().stream()
                    .filter(entryValue -> entryValue.getValue() > 1)
                    .map(entry -> "field '%s' contain more than one, times: %s  "
                            .formatted(entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(","));
            throw new SchemeValidationException(errorMessage);
        }
        return true;
    }
}
