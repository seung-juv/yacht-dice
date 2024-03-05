package com.yacht.app.auth.service;

import com.yacht.app.user.domain.Account;
import com.yacht.app.user.domain.AccountRepository;
import com.yacht.app.user.domain.User;
import com.yacht.app.user.domain.UserRepository;
import com.yacht.app.user.dto.UserDto;
import com.yacht.app.user.dto.UserDtoMapper;
import com.yacht.app.auth.domain.CustomUserDetails;
import com.yacht.app.auth.dto.AuthDto;
import com.yacht.app.auth.dto.AuthDtoMapper;
import com.yacht.app.jwt.dto.TokenDto;
import com.yacht.app.jwt.service.JwtService;
import com.yacht.exception.BadRequestException;
import com.yacht.exception.ForbiddenException;
import com.yacht.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthDto.LoginResponse login(AuthDto.Login request) {
        Account account = accountRepository.findByUsername(request.getUsername()).orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new ForbiddenException("비밀번호가 일치하지 않습니다.");
        }

        TokenDto.Response token = jwtService.generateToken(account.getId());

        AuthDto.LoginResponse response = new AuthDto.LoginResponse(token);
        UserDto.Response userResponse = UserDtoMapper.INSTANCE.toResponse(account);
        userResponse.setName(account.getUser().getName());
        response.setUser(userResponse);
        return response;
    }

    @Transactional
    public AuthDto.LoginResponse join(AuthDto.Join join) {
        accountRepository.findByUsername(join.getUsername())
                .ifPresent(x -> {
                    throw new ForbiddenException("이미 존재하는 계정입니다.");
                });

        User user = userRepository.save(
                User.builder()
                        .name(join.getName())
                        .isAdmin(false)
                        .build()
        );

        Account account = AuthDtoMapper.INSTANCE.toEntity(join);
        account.setType(Account.Type.EMAIL);
        account.setPassword(passwordEncoder.encode(join.getPassword()));
        account.setUser(user);
        accountRepository.save(account);

        return this.login(
                AuthDto.Login.builder()
                        .username(join.getUsername())
                        .password(join.getPassword())
                        .build()
        );
    }

    public TokenDto.Response oauthToken(AuthDto.OauthToken request) {
        switch (request.getGrantType()) {
            case "refreshToken":
                return jwtService.renewalToken(request.getJti(), request.getRefreshToken());
            default:
                throw new BadRequestException("잘못된 요청입니다");
        }
    }

    public Boolean logout(AuthDto.Logout request, CustomUserDetails user) {
        switch (request.getGrantType()) {
            case "refreshToken" -> {
                return jwtService.expireToken(request.getJti(), request.getRefreshToken(), user);
            }
            default -> {
                throw new BadRequestException("잘못된 요청입니다");
            }
        }
    }

    @Transactional(readOnly = true)
    public UserDto.Response me(Account account) {
        UserDto.Response response = UserDtoMapper.INSTANCE.toResponse(account);
        response.setName(account.getUser().getName());
        return response;
    }
}
