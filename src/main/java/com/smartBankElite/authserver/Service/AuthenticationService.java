package com.smartBankElite.authserver.Service;

import com.smartBankElite.authserver.DTO.LoginUserDto;
import com.smartBankElite.authserver.DTO.RegisterUserDto;
import com.smartBankElite.authserver.Model.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    public User signup(RegisterUserDto input);

    public User authenticate(LoginUserDto input);
}
