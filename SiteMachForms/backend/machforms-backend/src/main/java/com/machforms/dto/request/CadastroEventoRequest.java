package com.machforms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.machforms.util.FormatoData;
import com.machforms.validation.Telefone;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tela "Cadastro do evento". Os nomes dos campos são os enviados pelo frontend:
 * nomeCriador, dataNasc, nomeEvento, imagem, dataInicio, localizacao, latitude, longitude, telefone, senha.
 * Campos opcionais: imagem, latitude, longitude e email. O "criadoEm" enviado pelo navegador é ignorado
 * (a data de criação é a do servidor). O e-mail é o login do organizador: sem ele o organizador não consegue
 * entrar.
 */
public record CadastroEventoRequest(
        @NotBlank(message = "Informe o nome completo")
        @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres")
        String nomeCriador,

        @NotNull(message = "Informe a data de nascimento (dd/mm/aaaa)")
        @Past(message = "A data de nascimento deve estar no passado")
        @JsonFormat(pattern = FormatoData.PADRAO, lenient = OptBoolean.FALSE)
        LocalDate dataNasc,

        @NotBlank(message = "Informe o nome do evento")
        @Size(min = 3, max = 150, message = "O nome do evento deve ter entre 3 e 150 caracteres")
        String nomeEvento,

        @Size(max = 500, message = "A URL da imagem deve ter no máximo 500 caracteres")
        @Pattern(regexp = "^(https?://\\S+)?$", message = "Informe uma URL de imagem válida (http:// ou https://)")
        String imagem,

        @NotNull(message = "Informe a data de início")
        LocalDateTime dataInicio,

        @NotBlank(message = "Informe a localização do evento")
        @Size(min = 2, max = 255, message = "A localização deve ter entre 2 e 255 caracteres")
        String localizacao,

        @DecimalMin(value = "-90.0", message = "A latitude deve estar entre -90 e 90")
        @DecimalMax(value = "90.0", message = "A latitude deve estar entre -90 e 90")
        Double latitude,

        @DecimalMin(value = "-180.0", message = "A longitude deve estar entre -180 e 180")
        @DecimalMax(value = "180.0", message = "A longitude deve estar entre -180 e 180")
        Double longitude,

        @NotBlank(message = "Informe o telefone")
        @Telefone
        String telefone,

        @Email(message = "Informe um e-mail válido")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
        String email,

        @NotBlank(message = "Informe a senha")
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres")
        String senha
) {
}
