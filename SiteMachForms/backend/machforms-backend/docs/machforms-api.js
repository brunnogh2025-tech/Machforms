/**
 * Cliente da API MachForms para o frontend.
 * Cole no <script> do HTML (ou salve como arquivo .js) e use no lugar de window.storage.
 *
 * Erros: a função lança um Error com
 *   - err.status  -> status HTTP
 *   - err.campos  -> { nomeDoCampo: "mensagem" } (HTTP 400), útil para showErr(...)
 */
const API_URL = 'http://localhost:8080/api';

async function apiRequest(path, { method = 'GET', body, token } = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(API_URL + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });
  const data = await res.json().catch(() => null);

  if (!res.ok) {
    const err = new Error((data && data.mensagem) || 'Erro inesperado');
    err.status = res.status;
    err.campos = (data && data.campos) || {};
    throw err;
  }
  return data;
}

const api = {
  // Tela "Login" -> { token, tipo, expiraEmSegundos, nomeCriador, email }
  login: (email, senha) =>
    apiRequest('/auth/login', { method: 'POST', body: { email, senha } }),

  // Tela "Cadastro do evento" (state.evento + e-mail) -> { id, nome }
  cadastrarEvento: (evento) =>
    apiRequest('/eventos', { method: 'POST', body: evento }),

  // Eventos disponíveis (público)
  listarEventos: () => apiRequest('/eventos'),

  // Eventos do organizador logado
  listarMeusEventos: (token) => apiRequest('/eventos/meus', { token }),

  // Fim do fluxo de inscrição: state.participante + nome do evento -> { protocolo, ... }
  cadastrarParticipante: (participante, nomeEvento) =>
    apiRequest('/participantes', {
      method: 'POST',
      body: { ...participante, evento: nomeEvento }
    }),

  // Painel do organizador (exige token)
  listarParticipantes: (token) => apiRequest('/participantes', { token })
};

/* ------------------------------------------------------------------
 * Exemplos de uso dentro do frontend atual
 * ------------------------------------------------------------------
 *
 * // submitLogin()
 * try {
 *   const r = await api.login(email, senha);
 *   state.token = r.token;
 *   go('chat');
 * } catch (e) {
 *   showErr('loginSenha', e.message);
 * }
 *
 * // submitEvento()  (acrescente o campo evEmail no formulário)
 * try {
 *   await api.cadastrarEvento({
 *     nomeCriador, dataNasc, nomeEvento, telefone, email, senha
 *   });
 * } catch (e) {
 *   Object.entries(e.campos).forEach(([campo, msg]) => showErr(mapa[campo], msg));
 * }
 *
 * // submitRestricao()
 * const r = await api.cadastrarParticipante(state.participante, state.evento.nomeEvento);
 * state.protocolo = r.protocolo;       // o protocolo agora vem do servidor
 * go('confirmacao', false);
 *
 * // loadPainel()
 * const rows = await api.listarParticipantes(state.token);
 */
