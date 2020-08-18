package com.conglomerate.dev.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class NewPasswordDomain {
    private String newPassword;
    private String resetPin;

    public NewPasswordDomain() {
        this.newPassword = null;
        this.resetPin = null;
    }
}
