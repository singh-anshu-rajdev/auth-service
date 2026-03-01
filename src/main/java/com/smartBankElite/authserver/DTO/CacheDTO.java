package com.smartBankElite.authserver.DTO;

import lombok.Data;

@Data
public class CacheDTO {

    private Integer userId;
    private String name;
    private String userName;
    private String emailId;
    private Long createdAt;
}
