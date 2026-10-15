// Guarda o token e o usuário logado no navegador.
export const sessao = {
  token: () => localStorage.getItem("t"),
  usuario: () => JSON.parse(localStorage.getItem("u") || "null"),
  salvar: (token, usuario) => {
    localStorage.setItem("t", token);
    localStorage.setItem("u", JSON.stringify(usuario));
  },
  limpar: () => {
    localStorage.removeItem("t");
    localStorage.removeItem("u");
  },
};
