package com.machforms.domain;

import com.machforms.domain.enums.DiaParticipacao;
import com.machforms.domain.enums.LocalDormida;
import com.machforms.domain.enums.RestricaoAlimentar;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Inscrição de uma pessoa em um evento. O mesmo e-mail só pode se inscrever uma vez por evento.
 */
@Entity
@Table(name = "participantes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_participante_protocolo", columnNames = "protocolo"),
                @UniqueConstraint(name = "uk_participante_evento_email", columnNames = {"evento_id", "email"})
        })
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String protocolo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LocalDormida dormida;

    @Column(name = "responsavel_chale", length = 120)
    private String responsavelChale;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "participante_dias", joinColumns = @JoinColumn(name = "participante_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia", nullable = false, length = 10)
    private Set<DiaParticipacao> dias = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "participante_restricoes", joinColumns = @JoinColumn(name = "participante_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "restricao", nullable = false, length = 30)
    private Set<RestricaoAlimentar> restricoes = new LinkedHashSet<>();

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Participante() {
        // exigido pelo JPA
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public Evento getEvento() {
        return evento;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public LocalDormida getDormida() {
        return dormida;
    }

    public String getResponsavelChale() {
        return responsavelChale;
    }

    public Set<DiaParticipacao> getDias() {
        return Collections.unmodifiableSet(dias);
    }

    public Set<RestricaoAlimentar> getRestricoes() {
        return Collections.unmodifiableSet(restricoes);
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    /**
     * Builder que também garante a regra de domínio: o responsável só existe para quem dorme no chalé.
     */
    public static final class Builder {

        private String protocolo;
        private Evento evento;
        private String nome;
        private String email;
        private String telefone;
        private LocalDate dataNascimento;
        private LocalDormida dormida;
        private String responsavelChale;
        private Set<DiaParticipacao> dias = Set.of();
        private Set<RestricaoAlimentar> restricoes = Set.of();

        private Builder() {
        }

        public Builder protocolo(String protocolo) {
            this.protocolo = protocolo;
            return this;
        }

        public Builder evento(Evento evento) {
            this.evento = evento;
            return this;
        }

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder telefone(String telefone) {
            this.telefone = telefone;
            return this;
        }

        public Builder dataNascimento(LocalDate dataNascimento) {
            this.dataNascimento = dataNascimento;
            return this;
        }

        public Builder dormida(LocalDormida dormida) {
            this.dormida = dormida;
            return this;
        }

        public Builder responsavelChale(String responsavelChale) {
            this.responsavelChale = responsavelChale;
            return this;
        }

        public Builder dias(Set<DiaParticipacao> dias) {
            this.dias = dias == null ? Set.of() : dias;
            return this;
        }

        public Builder restricoes(Set<RestricaoAlimentar> restricoes) {
            this.restricoes = restricoes == null ? Set.of() : restricoes;
            return this;
        }

        public Participante build() {
            Participante participante = new Participante();
            participante.protocolo = protocolo;
            participante.evento = evento;
            participante.nome = nome;
            participante.email = email;
            participante.telefone = telefone;
            participante.dataNascimento = dataNascimento;
            participante.dormida = dormida;
            participante.responsavelChale = dormida == LocalDormida.CHALE ? responsavelChale : null;
            participante.dias.addAll(dias);
            participante.restricoes.addAll(restricoes);
            participante.criadoEm = Instant.now();
            return participante;
        }
    }
}
