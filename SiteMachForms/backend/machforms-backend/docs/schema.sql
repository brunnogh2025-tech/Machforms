CREATE DATABASE IF NOT EXISTS machforms_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE machforms_db;

CREATE TABLE organizadores (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nome            VARCHAR(120) NOT NULL,
    data_nascimento DATE         NOT NULL,
    telefone        VARCHAR(20)  NOT NULL,
    email           VARCHAR(150) NULL,
    senha_hash      VARCHAR(100) NOT NULL,
    criado_em       DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_organizador_email UNIQUE (email)
);

CREATE TABLE eventos (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    nome           VARCHAR(150) NOT NULL,
    organizador_id BIGINT       NOT NULL,
    imagem         VARCHAR(500) NULL,
    data_inicio    DATETIME(6)  NULL,
    localizacao    VARCHAR(255) NULL,
    latitude       DOUBLE       NULL,
    longitude      DOUBLE       NULL,
    criado_em      DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_evento_nome UNIQUE (nome),
    CONSTRAINT fk_evento_organizador FOREIGN KEY (organizador_id) REFERENCES organizadores (id)
);

CREATE TABLE participantes (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    protocolo         VARCHAR(20)  NOT NULL,
    evento_id         BIGINT       NOT NULL,
    nome              VARCHAR(120) NOT NULL,
    email             VARCHAR(150) NOT NULL,
    telefone          VARCHAR(20)  NOT NULL,
    data_nascimento   DATE         NOT NULL,
    dormida           VARCHAR(10)  NOT NULL,
    responsavel_chale VARCHAR(120) NULL,
    criado_em         DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_participante_protocolo UNIQUE (protocolo),
    CONSTRAINT uk_participante_evento_email UNIQUE (evento_id, email),
    CONSTRAINT fk_participante_evento FOREIGN KEY (evento_id) REFERENCES eventos (id)
);

CREATE TABLE participante_dias (
    participante_id BIGINT      NOT NULL,
    dia             VARCHAR(10) NOT NULL,
    CONSTRAINT fk_dias_participante FOREIGN KEY (participante_id) REFERENCES participantes (id)
);

CREATE TABLE participante_restricoes (
    participante_id BIGINT      NOT NULL,
    restricao       VARCHAR(30) NOT NULL,
    CONSTRAINT fk_restricoes_participante FOREIGN KEY (participante_id) REFERENCES participantes (id)
);
