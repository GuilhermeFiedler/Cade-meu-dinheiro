import { useEffect, useState } from "react";

/** Executa `buscar()` quando `deps` mudam e informa carregando, dados e erro. `recarregar()` busca de novo. */
export default function useCarregar(buscar, deps) {
  const [estado, setEstado] = useState({ carregando: true, dados: null, erro: null });
  const [recarga, setRecarga] = useState(0);

  useEffect(() => {
    let vivo = true;
    setEstado({ carregando: true, dados: null, erro: null });
    buscar()
      .then((dados) => vivo && setEstado({ carregando: false, dados, erro: null }))
      .catch((e) => vivo && setEstado({ carregando: false, dados: null, erro: e.message }));
    return () => { vivo = false; };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, recarga]);

  return { ...estado, recarregar: () => setRecarga((n) => n + 1) };
}
