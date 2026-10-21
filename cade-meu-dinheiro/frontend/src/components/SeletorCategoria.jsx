export default function SeletorCategoria({ categorias, valor, onMudar }) {
  return (
    <div className="chips" role="radiogroup" aria-label="Categoria">
      {categorias.map((c) => (
        <label className="chip" key={c.id}>
          <input type="radio" name="cat" checked={valor === c.id} onChange={() => onMudar(c.id)} />
          <span><i className="dot" style={{ background: c.cor }} />{c.nome}</span>
        </label>
      ))}
    </div>
  );
}
