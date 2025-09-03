package com.ddi.assessment.news.domain.user.service;

import com.ddi.assessment.news.domain.user.entity.JpaUser;
import com.ddi.assessment.news.domain.user.exception.UserLoginException;
import com.ddi.assessment.news.domain.user.exception.UserRegisterException;
import com.ddi.assessment.news.domain.user.repository.JpaUserRepository;
import com.ddi.assessment.news.domain.user.vo.LoginUser;
import com.ddi.assessment.news.domain.user.vo.LoginUserQuery;
import com.ddi.assessment.news.domain.user.vo.RegisterUserCommand;
import com.ddi.assessment.news.domain.user.vo.TokenOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    JpaUserRepository jpaUserRepository;

    @Override
    public LoginUser login(LoginUserQuery query) {

        Optional<JpaUser> userOrEmpty = jpaUserRepository.findByUserId(query.userId());

        if(userOrEmpty.isEmpty()) {
            throw new UserLoginException("존재 하지 않는 사용자");
        }

        JpaUser user = userOrEmpty.get();

        return new LoginUser(user.getId(), user.getUserId(), user.getPasswordHash());

    }

    @Override
    public void register(RegisterUserCommand command) {

        Optional<JpaUser> userOrEmpty = jpaUserRepository.findByUserId(command.userId());

        if(userOrEmpty.isPresent()) {
            throw new UserRegisterException("아이디 중복");
        }


        Optional<JpaUser> emailOrEmpty = jpaUserRepository.findByEmail(command.email());
        if(emailOrEmpty.isPresent()) {
            throw new UserRegisterException("이메일 중복");
        }

        jpaUserRepository.save(new JpaUser(command.userId(), command.email(), command.passwordHash()));

    }

    @Override
    public JpaUser findUser(Long userId) {

        Optional<JpaUser> userOrEmpty = jpaUserRepository.findById(userId);

        if(userOrEmpty.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자");
        }

        return userOrEmpty.get();

    }

    @Override
    public TokenOwner findTokenOwner(String loginId) {
        Optional<JpaUser> userOrEmpty = jpaUserRepository.findByUserId(loginId);

        if(userOrEmpty.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자");
        }

        return new TokenOwner(userOrEmpty.get().getId());

    }
}
