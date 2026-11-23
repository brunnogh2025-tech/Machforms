package com.machforms.repository;

import com.machforms.domain.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    Optional<Evento> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);

    List<Evento> findAllByOrderByNomeAsc();

    List<Evento> findByOrganizadorEmailOrderByNomeAsc(String emailOrganizador);

    Optional<Evento> findFirstByOrganizadorIdOrderByCriadoEmAsc(Long organizadorId);
}
