package com.smartBankElite.authserver.Service;

import com.smartBankElite.authserver.DTO.LoginResponseDTO;
import com.smartBankElite.authserver.DTO.LoginUserDto;
import com.smartBankElite.authserver.DTO.RegisterUserDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    public RegisterUserDto signup(RegisterUserDto input);

    public LoginResponseDTO authenticate(LoginUserDto input);
}
