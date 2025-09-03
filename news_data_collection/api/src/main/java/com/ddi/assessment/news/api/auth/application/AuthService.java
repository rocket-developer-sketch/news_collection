package com.ddi.assessment.news.api.auth.application;

import com.ddi.assessment.news.api.auth.dto.LoginUserRequest;
import com.ddi.assessment.news.api.auth.dto.LoginUserResponse;
import com.ddi.assessment.news.api.auth.dto.RegisterUserRequest;

public interface AuthService {
    LoginUserResponse login(LoginUserRequest request);
    void register(RegisterUserRequest request);
}
