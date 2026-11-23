-- =====================================================================
-- ArenaXP - Gestão de Equipamentos ESports
-- SAEP 2026 - Projeto 01 | MySQL 8+
-- Script DDL + DML (carga inicial). Pode ser executado várias vezes.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS arenaxp_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE arenaxp_db;

-- Limpeza para permitir reexecução em banco zerado/sujo (ordem inversa das FKs)
DROP TABLE IF EXISTS movimentacoes;
DROP TABLE IF EXISTS equipamentos;
DROP TABLE IF EXISTS usuarios;

-- ---------------------------------------------------------------------
-- DDL
-- ---------------------------------------------------------------------

CREATE TABLE usuarios (
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    nome    VARCHAR(100) NOT NULL,
    email   VARCHAR(120) NOT NULL,
    senha   VARCHAR(255) NOT NULL,   -- hash BCrypt
    perfil  VARCHAR(30)  NOT NULL,   -- ADMIN, TECNICO, COORDENADOR
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE equipamentos (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    codigo_patrimonio     VARCHAR(30)  NOT NULL,
    nome                  VARCHAR(120) NOT NULL,
    categoria             VARCHAR(50)  NOT NULL,
    especificacao_tecnica VARCHAR(255) NOT NULL,
    quantidade_atual      INT          NOT NULL,
    estoque_minimo        INT          NOT NULL,
    CONSTRAINT pk_equipamentos PRIMARY KEY (id),
    CONSTRAINT uk_equipamentos_codigo UNIQUE (codigo_patrimonio),
    CONSTRAINT ck_equip_quantidade CHECK (quantidade_atual >= 0),
    CONSTRAINT ck_equip_minimo CHECK (estoque_minimo >= 0)
) ENGINE=InnoDB;

CREATE TABLE movimentacoes (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    tipo_movimentacao VARCHAR(15)  NOT NULL,
    quantidade        INT          NOT NULL,
    data_hora         DATETIME     NOT NULL,
    observacao_evento VARCHAR(255) NULL,
    equipamento_id    BIGINT       NOT NULL,
    usuario_id        BIGINT       NOT NULL,
    CONSTRAINT pk_movimentacoes PRIMARY KEY (id),
    CONSTRAINT ck_mov_tipo CHECK (tipo_movimentacao IN ('ENTRADA', 'SAIDA')),
    CONSTRAINT ck_mov_quantidade CHECK (quantidade > 0),
    CONSTRAINT fk_mov_equipamento FOREIGN KEY (equipamento_id) REFERENCES equipamentos (id),
    CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB;

CREATE INDEX idx_mov_data_hora ON movimentacoes (data_hora);
CREATE INDEX idx_mov_equipamento ON movimentacoes (equipamento_id);

-- ---------------------------------------------------------------------
-- DML - carga inicial
-- Senhas (BCrypt):  admin123  -> admin@arenaxp.com
--                   tecnico123 -> demais usuários
-- ---------------------------------------------------------------------

INSERT INTO usuarios (nome, email, senha, perfil) VALUES
('Administrador ArenaXP', 'admin@arenaxp.com',   '$2a$10$st0HCGsLLNHknBrvjLL.9uBSDYtdE1cojXzkGSiwvlnzFo.DWMxFu', 'ADMIN'),
('Carlos Eduardo Lima',   'carlos@arenaxp.com',  '$2a$10$DO54pd0Rl5cKt41jzon08uF9byBK4o9B8fM6/DSjrTbcn4x4GG8fy', 'TECNICO'),
('Marina Souza Alves',    'marina@arenaxp.com',  '$2a$10$DO54pd0Rl5cKt41jzon08uF9byBK4o9B8fM6/DSjrTbcn4x4GG8fy', 'COORDENADOR');

INSERT INTO equipamentos (codigo_patrimonio, nome, categoria, especificacao_tecnica, quantidade_atual, estoque_minimo) VALUES
('AXP-MSE-001', 'Mouse Logitech G Pro X Superlight 2', 'Mouse',   '32000 DPI, 2.4GHz Wireless, 60g',                    12, 5),
('AXP-TEC-001', 'Teclado Mecânico Wooting 60HE',       'Teclado', 'Hall Effect, 60%, Rapid Trigger',                      8, 4),
('AXP-HDS-001', 'Headset HyperX Cloud III',            'Headset', 'Drivers 53mm, microfone destacável, USB/P2',           4, 3),  -- próximo do mínimo
('AXP-MON-001', 'Monitor ASUS ROG Swift 360Hz',        'Monitor', '24.5" Full HD, 360Hz, IPS, 1ms',                       6, 2),
('AXP-CAP-001', 'Placa de Captura Elgato 4K X',        'Captura', '4K144 HDMI 2.1, USB 3.2, passthrough VRR',             2, 2);  -- já no mínimo (crítico)

INSERT INTO movimentacoes (tipo_movimentacao, quantidade, data_hora, observacao_evento, equipamento_id, usuario_id) VALUES
('ENTRADA', 12, '2026-11-02 09:00:00', 'Compra inicial - lote de mouses para a temporada',        1, 1),
('SAIDA',    2, '2026-11-07 17:30:00', 'Copa Universitária FPS - Palco A',                        3, 2),
('SAIDA',    3, '2026-11-08 10:15:00', 'Copa Universitária FPS - Palco B (streamers)',            5, 2),
('ENTRADA',  1, '2026-11-10 14:45:00', 'Devolução pós-torneio - Copa Universitária FPS',          3, 3);
