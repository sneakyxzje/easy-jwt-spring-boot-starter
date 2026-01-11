package com.sneakyzxje.libs.security.dto;

import java.util.Date;

public record RefreshTokenDTO(
    String refreshToken,
    Date expiryDate
) {
    
}
