package com.smartBankElite.authserver.DTO;

import lombok.Builder;
import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private long expiresIn;
}
