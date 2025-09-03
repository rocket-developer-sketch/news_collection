package com.ddi.assessment.news.domain.user.service;

import com.ddi.assessment.news.domain.user.entity.JpaUser;
import com.ddi.assessment.news.domain.user.vo.LoginUser;
import com.ddi.assessment.news.domain.user.vo.LoginUserQuery;
import com.ddi.assessment.news.domain.user.vo.RegisterUserCommand;
import com.ddi.assessment.news.domain.user.vo.TokenOwner;

public interface UserService {
    LoginUser login(LoginUserQuery query);
    void register(RegisterUserCommand command);
    JpaUser findUser(Long userId);
    TokenOwner findTokenOwner(String loginId);
}
