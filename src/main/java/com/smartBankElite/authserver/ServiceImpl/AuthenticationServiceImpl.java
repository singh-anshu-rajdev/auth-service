package com.smartBankElite.authserver.ServiceImpl;

import com.smartBankElite.authserver.DTO.LoginResponseDTO;
import com.smartBankElite.authserver.DTO.LoginUserDto;
import com.smartBankElite.authserver.DTO.RegisterUserDto;
import com.smartBankElite.authserver.Model.User;
import com.smartBankElite.authserver.Repositories.UserRepository;
import com.smartBankElite.authserver.Service.AuthenticationService;
import com.smartBankElite.authserver.Service.JwtService;
import com.smartBankElite.authserver.Utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public RegisterUserDto signup(RegisterUserDto input) {
        try{
            User user = new User();
            user.setUserName(input.getUserName());

            user.setFullName(input.getFullName());
            user.setEmailId(input.getEmail());
            user.setDeletedFlag(Boolean.FALSE);
            user.setPassword(passwordEncoder.encode(input.getPassword()));

            User registeredUser =  userRepository.save(user);
            input.setId(registeredUser.getId());
            emailService.sendUserCreationEmail(input);
            return input;
        }catch (Exception e){
            throw new RuntimeException("Error occurred during user registration: " + e.getMessage(), e);
        }
    }

    @Override
    public LoginResponseDTO authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUserName(),
                        input.getPassword()
                )
        );

        User authenticatedUser =  userRepository.findActiveUserByEmailOrUsername(input.getUserName())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        return loginResponse;
    }
}
