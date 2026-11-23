package com.machforms.security;

import com.machforms.repository.OrganizadorRepository;
import com.machforms.util.Normalizadores;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Ensina o Spring Security a buscar o organizador (login = e-mail) no banco.
 */
@Service
public class OrganizadorUserDetailsService implements UserDetailsService {

    private static final String PAPEL_ORGANIZADOR = "ORGANIZADOR";

    private final OrganizadorRepository organizadorRepository;

    public OrganizadorUserDetailsService(OrganizadorRepository organizadorRepository) {
        this.organizadorRepository = organizadorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return organizadorRepository.findByEmail(Normalizadores.email(email))
                .map(organizador -> User.withUsername(organizador.getEmail())
                        .password(organizador.getSenhaHash())
                        .roles(PAPEL_ORGANIZADOR)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Organizador não encontrado"));
    }
}
