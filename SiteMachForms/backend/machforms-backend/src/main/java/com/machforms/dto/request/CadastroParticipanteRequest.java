package com.machforms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.machforms.domain.enums.DiaParticipacao;
import com.machforms.domain.enums.LocalDormida;
import com.machforms.domain.enums.RestricaoAlimentar;
import com.machforms.util.FormatoData;
import com.machforms.validation.ChaleExigeResponsavel;
import com.machforms.validation.EventoInformado;
import com.machforms.validation.Telefone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * Telas "Cadastro do participante", "Continuação" (local para dormir) e "Continuação" (restrição alimentar).
 * Os nomes dos campos são os mesmos do objeto enviado pelo frontend. O campo "protocolo", se vier,
 * é ignorado: quem gera o protocolo é o servidor.
 */
@ChaleExigeResponsavel
@EventoInformado
public record CadastroParticipanteRequest(
        /* Evento escolhido na busca: o frontend envia o id e o nome. Vale o id; sem id, vale o nome. */
        Long eventoId,

        String evento,

        @NotBlank(message = "Informe o nome completo")
        @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres")
        String nome,

        @NotBlank(message = "Informe o e-mail")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
        String email,

        @NotBlank(message = "Informe o telefone")
        @Telefone
        String telefone,

        @NotEmpty(message = "Selecione ao menos um dia")
        Set<@NotNull(message = "Dia inválido") DiaParticipacao> dias,

        @NotNull(message = "Escolha onde vai dormir (Barraca ou Chalé)")
        LocalDormida dormida,

        @Size(max = 120, message = "O nome do responsável deve ter no máximo 120 caracteres")
        String responsavelChale,

        @NotNull(message = "Informe a data de nascimento (dd/mm/aaaa)")
        @Past(message = "A data de nascimento deve estar no passado")
        @JsonFormat(pattern = FormatoData.PADRAO, lenient = OptBoolean.FALSE)
        LocalDate dataNasc,

        Set<@NotNull(message = "Restrição alimentar inválida") RestricaoAlimentar> restricoes
) {
}
