package com.machforms.service.impl;

import com.machforms.domain.Organizador;
import com.machforms.exception.ConflictException;
import com.machforms.repository.OrganizadorRepository;
import com.machforms.service.DadosOrganizador;
import com.machforms.service.OrganizadorService;
import com.machforms.util.Normalizadores;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizadorServiceImpl implements OrganizadorService {

    private final OrganizadorRepository organizadorRepository;
    private final PasswordEncoder passwordEncoder;

    public OrganizadorServiceImpl(OrganizadorRepository organizadorRepository, PasswordEncoder passwordEncoder) {
        this.organizadorRepository = organizadorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Organizador cadastrar(DadosOrganizador dados) {
        String email = Normalizadores.emailOuNulo(dados.email());
        if (email != null) {
            garantirEmailDisponivel(email);
        }

        Organizador organizador = new Organizador(
                Normalizadores.texto(dados.nome()),
                dados.dataNascimento(),
                Normalizadores.telefone(dados.telefone()),
                email,
                passwordEncoder.encode(dados.senha()));
        return organizadorRepository.save(organizador);
    }

    private void garantirEmailDisponivel(String email) {
        if (organizadorRepository.existsByEmail(email)) {
            throw new ConflictException("Já existe um organizador cadastrado com este e-mail");
        }
    }
}
