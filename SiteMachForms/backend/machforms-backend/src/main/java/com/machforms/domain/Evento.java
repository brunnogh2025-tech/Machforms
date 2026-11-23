package com.machforms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Retiro ou acampamento que recebe inscrições de participantes.
 * Imagem, data de início, localização e coordenadas alimentam a busca de eventos do frontend.
 * As colunas novas aceitam nulo no banco para que bases criadas antes delas continuem funcionando;
 * a obrigatoriedade (data de início e localização) é validada na entrada da API.
 */
@Entity
@Table(name = "eventos",
        uniqueConstraints = @UniqueConstraint(name = "uk_evento_nome", columnNames = "nome"))
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizador_id", nullable = false)
    private Organizador organizador;

    @Column(length = 500)
    private String imagem;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(length = 255)
    private String localizacao;

    private Double latitude;

    private Double longitude;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Evento() {
        // exigido pelo JPA
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Organizador getOrganizador() {
        return organizador;
    }

    public String getImagem() {
        return imagem;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public static final class Builder {

        private String nome;
        private Organizador organizador;
        private String imagem;
        private LocalDateTime dataInicio;
        private String localizacao;
        private Double latitude;
        private Double longitude;

        private Builder() {
        }

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder organizador(Organizador organizador) {
            this.organizador = organizador;
            return this;
        }

        public Builder imagem(String imagem) {
            this.imagem = imagem;
            return this;
        }

        public Builder dataInicio(LocalDateTime dataInicio) {
            this.dataInicio = dataInicio;
            return this;
        }

        public Builder localizacao(String localizacao) {
            this.localizacao = localizacao;
            return this;
        }

        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Evento build() {
            Evento evento = new Evento();
            evento.nome = nome;
            evento.organizador = organizador;
            evento.imagem = imagem;
            evento.dataInicio = dataInicio;
            evento.localizacao = localizacao;
            evento.latitude = latitude;
            evento.longitude = longitude;
            evento.criadoEm = Instant.now();
            return evento;
        }
    }
}
