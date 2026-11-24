package com.caminha.javadailyexpenses.persistence;

import java.util.List;

public record TopicDetails(
        String name,
        List<String> supportedTypes
) {
}
