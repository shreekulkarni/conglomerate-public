package com.conglomerate.dev.models.domain;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class GoogleTokenDomain {
    String idToken;
    String refreshToken;

    public GoogleTokenDomain() {
        idToken = null;
        refreshToken = null;
    }
}
