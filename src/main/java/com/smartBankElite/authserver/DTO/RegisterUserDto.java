package com.smartBankElite.authserver.DTO;

import lombok.Data;

@Data
public class RegisterUserDto {

    private Integer id;
    private String email;
    private String password;
    private String fullName;
    private String userName;
}
