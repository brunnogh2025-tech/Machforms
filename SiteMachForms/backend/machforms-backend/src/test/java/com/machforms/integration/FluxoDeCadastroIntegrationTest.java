package com.machforms.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Percorre a API de ponta a ponta (HTTP -> segurança -> serviços -> banco H2 em memória),
 * reproduzindo as chamadas que o frontend faz: /api/eventos, /api/login, /api/inscricoes e /api/chat.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FluxoDeCadastroIntegrationTest {

    private static final String SENHA = "segredo123";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void organizadorCriaEventoFazLoginEVeAsInscricoes() throws Exception {
        String nomeEvento = nomeUnico("Trilha Verde");
        String emailOrganizador = emailUnico("org");

        cadastrarEvento(nomeEvento, emailOrganizador)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nomeEvento").value(nomeEvento))
                .andExpect(jsonPath("$.imagem").value("https://exemplo.com/capa.jpg"))
                .andExpect(jsonPath("$.dataInicio").value("2027-03-15T09:00:00"))
                .andExpect(jsonPath("$.localizacao").exists())
                .andExpect(jsonPath("$.latitude").value(-22.7396))
                .andExpect(jsonPath("$.longitude").value(-45.5911))
                .andExpect(jsonPath("$.criadoEm").exists());

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(emailOrganizador, SENHA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.evento.nomeEvento").value(nomeEvento));

        String token = login(emailOrganizador, SENHA);

        inscrever(null, nomeEvento, emailUnico("part"), "Barraca", "null")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.protocolo").value(matchesPattern("MF\\d{6}")))
                .andExpect(jsonPath("$.evento").value(nomeEvento))
                .andExpect(jsonPath("$.dias[0]").value("Dia 1"))
                .andExpect(jsonPath("$.dias[1]").value("Dia 2"))
                .andExpect(jsonPath("$.dormida").value("Barraca"))
                .andExpect(jsonPath("$.dataNasc").value("20/05/2005"))
                .andExpect(jsonPath("$.telefone").value("11912345678"));

        mockMvc.perform(get("/api/inscricoes"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/inscricoes").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].evento").value(nomeEvento));
    }

    @Test
    void inscricaoPeloIdDoEventoEscolhidoNaBusca() throws Exception {
        String nomeEvento = nomeUnico("Retiro da Busca");
        long eventoId = cadastrarEventoEObterId(nomeEvento, emailUnico("org"));

        inscrever(eventoId, nomeEvento, emailUnico("part"), "Barraca", "null")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.evento").value(nomeEvento))
                .andExpect(jsonPath("$.protocolo").value(matchesPattern("MF\\d{6}")));
    }

    @Test
    void inscricaoSemIdNemNomeDoEventoRetorna400() throws Exception {
        inscrever(null, "", emailUnico("part"), "Barraca", "null")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.evento").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void inscricaoEmEventoInexistenteRetorna404() throws Exception {
        inscrever(999_999_999L, "", emailUnico("part"), "Barraca", "null")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
        inscrever(null, "Evento que não existe " + UUID.randomUUID(), emailUnico("part"), "Barraca", "null")
                .andExpect(status().isNotFound());
    }

    @Test
    void organizadorNaoVeInscricoesDeEventoDeOutroOrganizador() throws Exception {
        String eventoA = nomeUnico("Evento A");
        String eventoB = nomeUnico("Evento B");
        String emailB = emailUnico("orgb");
        cadastrarEvento(eventoA, emailUnico("orga")).andExpect(status().isCreated());
        cadastrarEvento(eventoB, emailB).andExpect(status().isCreated());
        inscrever(null, eventoA, emailUnico("part"), "Barraca", "null").andExpect(status().isCreated());

        String tokenB = login(emailB, SENHA);

        mockMvc.perform(get("/api/inscricoes").header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void chaleExigeNomeDoResponsavel() throws Exception {
        String nomeEvento = nomeUnico("Retiro Chale");
        cadastrarEvento(nomeEvento, emailUnico("org")).andExpect(status().isCreated());

        inscrever(null, nomeEvento, emailUnico("part"), "Chalé", "null")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.responsavelChale").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());

        inscrever(null, nomeEvento, emailUnico("part"), "Chalé", "\"Carlos Souza\"")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responsavelChale").value("Carlos Souza"));
    }

    @Test
    void naoPermiteDuasInscricoesComOMesmoEmailNoMesmoEvento() throws Exception {
        String nomeEvento = nomeUnico("Acampamento");
        String emailParticipante = emailUnico("part");
        cadastrarEvento(nomeEvento, emailUnico("org")).andExpect(status().isCreated());

        inscrever(null, nomeEvento, emailParticipante, "Barraca", "null").andExpect(status().isCreated());
        inscrever(null, nomeEvento, emailParticipante, "Barraca", "null")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void naoPermiteDoisEventosComOMesmoNome() throws Exception {
        String nomeEvento = nomeUnico("Evento Repetido");
        cadastrarEvento(nomeEvento, emailUnico("org1")).andExpect(status().isCreated());
        cadastrarEvento(nomeEvento, emailUnico("org2"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void loginComSenhaErradaRetorna401ComMessage() throws Exception {
        String emailOrganizador = emailUnico("org");
        cadastrarEvento(nomeUnico("Evento"), emailOrganizador).andExpect(status().isCreated());

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(emailOrganizador, "senha-errada")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void eventoSemEmailECadastradoComoOFormularioAtualEnvia() throws Exception {
        cadastrarEvento(nomeUnico("Evento Sem Email"), null)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeEvento").exists());
    }

    @Test
    void dataDeNascimentoInexistenteNoCalendarioRetorna400() throws Exception {
        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventoJson(nomeUnico("Evento"), emailUnico("org"))
                                .replace("15/03/1990", "31/02/1990")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void imagemQueNaoEUmaUrlHttpRetorna400() throws Exception {
        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventoJson(nomeUnico("Evento"), emailUnico("org"))
                                .replace("https://exemplo.com/capa.jpg", "javascript:alert(1)")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.imagem").exists());
    }

    @Test
    void imagemVaziaELatitudeNulaSaoAceitas() throws Exception {
        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventoJson(nomeUnico("Evento Simples"), emailUnico("org"))
                                .replace("https://exemplo.com/capa.jpg", "")
                                .replace("-22.7396", "null")
                                .replace("-45.5911", "null")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imagem").doesNotExist())
                .andExpect(jsonPath("$.latitude").doesNotExist());
    }

    @Test
    void latitudeForaDaFaixaRetorna400() throws Exception {
        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventoJson(nomeUnico("Evento"), emailUnico("org"))
                                .replace("-22.7396", "95.5")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.latitude").exists());
    }

    @Test
    void listaPublicaDeEventosNaoExigeLoginEDevolveUmArrayComOsCamposDaBusca() throws Exception {
        String nomeEvento = nomeUnico("Evento Publico");
        cadastrarEvento(nomeEvento, emailUnico("org")).andExpect(status().isCreated());

        mockMvc.perform(get("/api/eventos").param("lat", "-23.55").param("lon", "-46.63"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nomeEvento == '%s')]".formatted(nomeEvento)).exists())
                .andExpect(jsonPath("$[?(@.nomeEvento == '%s')].localizacao".formatted(nomeEvento)).exists())
                .andExpect(jsonPath("$[?(@.nomeEvento == '%s')].imagem".formatted(nomeEvento)).exists());
    }

    @Test
    void chatExigeLoginEResponde() throws Exception {
        String emailOrganizador = emailUnico("org");
        cadastrarEvento(nomeUnico("Evento Chat"), emailOrganizador).andExpect(status().isCreated());
        String token = login(emailOrganizador, SENHA);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Olá\"}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/chat")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Olá\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Recebi sua mensagem!"));

        mockMvc.perform(post("/api/chat")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    // ---------- auxiliares ----------

    private ResultActions cadastrarEvento(String nomeEvento, String email) throws Exception {
        return mockMvc.perform(post("/api/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventoJson(nomeEvento, email)));
    }

    private long cadastrarEventoEObterId(String nomeEvento, String email) throws Exception {
        String corpo = cadastrarEvento(nomeEvento, email)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(corpo, "$.id")).longValue();
    }

    /** Mesmo formato do corpo enviado pela tela "Cadastro do evento" (sem e-mail quando {@code email} é nulo). */
    private String eventoJson(String nomeEvento, String email) {
        String campoEmail = email == null ? "" : "\"email\":\"%s\",".formatted(email);
        return """
                {"nomeCriador":"Ana Souza","dataNasc":"15/03/1990","nomeEvento":"%s",
                 "imagem":"https://exemplo.com/capa.jpg","dataInicio":"2027-03-15T09:00",
                 "localizacao":"Campos do Jordão, SP","latitude":-22.7396,"longitude":-45.5911,
                 %s"telefone":"(11) 91234-5678","senha":"%s","criadoEm":"2026-11-23T12:00:00.000Z"}
                """.formatted(nomeEvento, campoEmail, SENHA);
    }

    private String loginJson(String email, String senha) {
        return """
                {"email":"%s","senha":"%s"}
                """.formatted(email, senha);
    }

    private String login(String email, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(email, senha)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(corpo, "$.token");
    }

    /**
     * @param eventoId        id do evento escolhido na busca, ou {@code null}
     * @param responsavelJson valor JSON do responsável pelo chalé: {@code null} ou um texto entre aspas
     */
    private ResultActions inscrever(Long eventoId, String nomeEvento, String email, String dormida,
                                    String responsavelJson) throws Exception {
        return mockMvc.perform(post("/api/inscricoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"eventoId":%s,"evento":"%s","nome":"Ana Beatriz Souza","email":"%s",
                         "telefone":"(11) 91234-5678","dias":["Dia 2","Dia 1"],"dormida":"%s",
                         "responsavelChale":%s,"dataNasc":"20/05/2005","restricoes":["Vegano"]}
                        """.formatted(eventoId, nomeEvento, email, dormida, responsavelJson)));
    }

    private String nomeUnico(String prefixo) {
        return prefixo + " " + UUID.randomUUID().toString().substring(0, 8);
    }

    private String emailUnico(String prefixo) {
        return prefixo + "-" + UUID.randomUUID().toString().substring(0, 8) + "@email.com";
    }
}
