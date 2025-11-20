package com.integratez.platform.modules.auth.dto;


import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String email;
    private String otp;
    private String newPassword;
}
