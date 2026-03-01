package com.smartBankElite.authserver.Utils;

import lombok.Getter;

@Getter
public enum SmartBankEliteConstants {
    USER_ID("userId"),
    NAME("Name"),
    USERNAME("username"),
    EMAIL_ID("emailId"),
    CREATED_AT("created At"),
    AUTHORIZATION("authorization");

    private final String value;

    SmartBankEliteConstants(String value) {
        this.value = value;
    }
}
