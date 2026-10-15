import { useCallback, useRef, useState } from "react";

/** Mensagem temporária de sucesso ou erro (toast). */
export default function useAviso() {
  const [aviso, setAviso] = useState(null);
  const timer = useRef(null);
  const avisar = useCallback((msg, erro = false) => {
    setAviso({ msg, erro });
    clearTimeout(timer.current);
    timer.current = setTimeout(() => setAviso(null), 2800);
  }, []);
  return { aviso, avisar };
}
