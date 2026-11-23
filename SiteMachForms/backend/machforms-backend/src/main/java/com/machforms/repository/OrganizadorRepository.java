package com.machforms.repository;

import com.machforms.domain.Organizador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizadorRepository extends JpaRepository<Organizador, Long> {

    Optional<Organizador> findByEmail(String email);

    boolean existsByEmail(String email);
}
