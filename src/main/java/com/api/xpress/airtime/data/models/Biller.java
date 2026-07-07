package com.api.xpress.airtime.data.models;

import com.api.xpress.xceptions.XpressException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public enum Biller {

    MTN("0803", "0806", "0703", "0706", "0813", "0816", "0810", "0814"),
    GLO("0805", "0807", "0705", "0815", "0811"),
    AIRTEL("0802", "0808", "0708", "0812"),
    ETISALAT("0809", "0818", "0817", "0909");

    private static final Map<String, Biller> PREFIX_LOOKUP =
            Arrays.stream(values())
                    .flatMap(provider -> provider.prefixes.stream()
                            .map(prefix -> Map.entry(prefix, provider)))
                    .collect(Collectors.toUnmodifiableMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (existing, duplicate) -> {
                                throw new IllegalStateException(
                                        "Duplicate prefix: %s vs %s".formatted(existing.name(), duplicate.name())
                                );
                            }
                    ));

    private String uniqueCode;
    private final Set<String> prefixes;

    Biller(String... prefixes) {
        this.prefixes = Set.of(prefixes);
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getUniqueCode() {
        if (uniqueCode == null) {
            throw new IllegalStateException("Unique code not initialized for: %s".formatted(this.name()));
        }
        return uniqueCode;
    }

    public static Biller fromPrefix(String prefix) {
        Objects.requireNonNull(prefix, "Phone prefix cannot be null");
        return Optional.ofNullable(PREFIX_LOOKUP.get(prefix))
                .orElseThrow(() -> new XpressException("Unsupported phone number prefix: %s".formatted(prefix)));
    }
}