package com.smartBankElite.authserver.ServiceImpl;

import com.smartBankElite.authserver.DTO.LoginUserDto;
import com.smartBankElite.authserver.DTO.RegisterUserDto;
import com.smartBankElite.authserver.Model.User;
import com.smartBankElite.authserver.Repositories.UserRepository;
import com.smartBankElite.authserver.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public User signup(RegisterUserDto input) {
        User user = new User();
        user.setUserName(input.getUserName());

        user.setFullName(input.getFullName());
        user.setEmailId(input.getEmail());
        user.setDeletedFlag(Boolean.FALSE);
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUserName(),
                        input.getPassword()
                )
        );

        return userRepository.findActiveUserByEmailOrUsername(input.getUserName())
                .orElseThrow();
    }
}
