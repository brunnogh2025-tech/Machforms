package com.machforms.repository;

import com.machforms.domain.Participante;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    boolean existsByProtocolo(String protocolo);

    boolean existsByEventoIdAndEmail(Long eventoId, String email);

    /**
     * Inscrições dos eventos de um organizador, da mais recente para a mais antiga.
     * O EntityGraph carrega tudo em uma consulta só, evitando o problema N+1.
     */
    @EntityGraph(attributePaths = {"evento", "dias", "restricoes"})
    List<Participante> findByEventoOrganizadorEmailOrderByCriadoEmDesc(String emailOrganizador);
}
