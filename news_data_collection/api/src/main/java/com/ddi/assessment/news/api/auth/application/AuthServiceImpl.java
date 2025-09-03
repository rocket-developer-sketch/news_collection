package com.ddi.assessment.news.api.auth.application;

import com.ddi.assessment.news.api.auth.dto.LoginUserRequest;
import com.ddi.assessment.news.api.auth.dto.LoginUserResponse;
import com.ddi.assessment.news.api.auth.dto.RegisterUserRequest;
import com.ddi.assessment.news.domain.user.exception.UserLoginException;
import com.ddi.assessment.news.domain.user.service.UserService;
import com.ddi.assessment.news.domain.user.vo.LoginUser;
import com.ddi.assessment.news.domain.user.vo.LoginUserQuery;
import com.ddi.assessment.news.domain.user.vo.RegisterUserCommand;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginUserResponse login(LoginUserRequest request) {
        LoginUser loginUser = userService.login(new LoginUserQuery(request.userId()));

        if(!passwordEncoder.matches(request.rawPassword(), loginUser.passwordHash())) {
            throw new UserLoginException("Invalid Password");
        }

        return new LoginUserResponse(loginUser.id(), loginUser.userId());
    }

    @Override
    public void register(RegisterUserRequest request) {
        userService.register(new RegisterUserCommand(request.userId(), request.email(), passwordEncoder.encode(request.rawPassword())));
    }
}
