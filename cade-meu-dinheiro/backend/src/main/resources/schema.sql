-- Cadê meu dinheiro? – DER: USUARIO 1:N GASTO, CATEGORIA 1:N GASTO (compatível com H2 e PostgreSQL)
CREATE TABLE IF NOT EXISTS usuario (
  id         UUID         PRIMARY KEY,
  nome       VARCHAR(80)  NOT NULL,
  email      VARCHAR(254) NOT NULL UNIQUE,                              -- RN06
  senha_hash VARCHAR(255) NOT NULL,
  criado_em  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categoria (
  id    SMALLINT    PRIMARY KEY,
  nome  VARCHAR(30) NOT NULL UNIQUE,
  cor   CHAR(7)     NOT NULL,
  icone VARCHAR(30),
  ordem SMALLINT    NOT NULL
);

CREATE TABLE IF NOT EXISTS gasto (
  id            UUID          PRIMARY KEY,
  usuario_id    UUID          NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,  -- RN01
  categoria_id  SMALLINT      NOT NULL REFERENCES categoria(id),                  -- RN03
  valor         NUMERIC(12,2) NOT NULL CHECK (valor > 0 AND valor <= 9999999.99), -- RN02
  data          DATE          NOT NULL,                                           -- RN04 (não futura: validada na API)
  descricao     VARCHAR(100),                                                     -- RN05
  criado_em     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  atualizado_em TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_gasto_usuario_data ON gasto (usuario_id, data);  -- RNF07
