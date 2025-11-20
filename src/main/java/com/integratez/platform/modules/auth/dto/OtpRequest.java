package com.integratez.platform.modules.auth.dto;


import lombok.Data;

@Data
public class OtpRequest {
    private String email;
    private String otp;
}
