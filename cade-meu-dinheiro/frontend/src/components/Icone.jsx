const CAMINHOS = {
  novo: <><circle cx="12" cy="12" r="9" /><path d="M12 8v8M8 12h8" /></>,
  hist: <><path d="M9 6h11M9 12h11M9 18h11" /><circle cx="4.5" cy="6" r="1" /><circle cx="4.5" cy="12" r="1" /><circle cx="4.5" cy="18" r="1" /></>,
  resumo: <><path d="M21 12A9 9 0 1 1 12 3v9z" /><path d="M15.5 3.6A9 9 0 0 1 20.4 8.5H15.5z" /></>,
  sair: <><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" /><path d="M16 17l5-5-5-5M21 12H9" /></>,
  editar: <><path d="M12 20h9" /><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4z" /></>,
  excluir: <path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6" />,
  esquerda: <path d="M15 18l-6-6 6-6" />,
  direita: <path d="M9 18l6-6-6-6" />,
  carteira: <><path d="M3 7a2 2 0 0 1 2-2h12v4" /><path d="M3 7v10a2 2 0 0 0 2 2h14a1 1 0 0 0 1-1v-8a1 1 0 0 0-1-1H5a2 2 0 0 1-2-2z" /><circle cx="16" cy="14" r="1" /></>,
  ok: <path d="M5 13l4 4L19 7" />,
  alerta: <><circle cx="12" cy="12" r="9" /><path d="M12 8v5M12 16h.01" /></>,
};

/** Ícone SVG simples (decorativo: o texto ao lado é o que leitores de tela leem). */
export default function Icone({ nome, tamanho = 20 }) {
  return (
    <svg width={tamanho} height={tamanho} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
         strokeLinecap="round" strokeLinejoin="round" aria-hidden="true" focusable="false">
      {CAMINHOS[nome]}
    </svg>
  );
}
