package com.machforms.service;

import com.machforms.dto.request.LoginRequest;
import com.machforms.dto.response.LoginResponse;

public interface AuthService {

    /**
     * @throws org.springframework.security.core.AuthenticationException se as credenciais forem inválidas
     */
    LoginResponse autenticar(LoginRequest request);
}
