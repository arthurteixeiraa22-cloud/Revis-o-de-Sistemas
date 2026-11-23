ARENAXP - GESTAO DE EQUIPAMENTOS ESPORTS
SAEP 2026 - Projeto 01 (Java 17+ / Spring Boot 3 / MySQL 8)
=========================================================

1. PRE-REQUISITOS
   - JDK 17 ou 21
   - Maven 3.8+
   - MySQL 8+ rodando em localhost:3306

2. BANCO DE DADOS
   a) Abra o MySQL Workbench (ou DBeaver / terminal) e execute o arquivo:
        02_Banco_de_Dados/arenaxp_db.sql
      Pelo terminal:
        mysql -u root -p < 02_Banco_de_Dados/arenaxp_db.sql
   b) O script cria o schema "arenaxp_db", as 3 tabelas e a carga inicial
      (3 usuarios, 5 equipamentos, 4 movimentacoes). Pode ser reexecutado
      quantas vezes quiser (ele recria as tabelas).

3. CONFIGURAR A CONEXAO
   Arquivo: 03_Codigo_Fonte/arenaxp-app/src/main/resources/application.properties
   Padrao: usuario "root", senha "root". Para mudar, edite o arquivo OU
   defina variaveis de ambiente antes de rodar:
        Linux/Mac:  export DB_USER=meuusuario DB_PASSWORD=minhasenha
        Windows:    set DB_USER=meuusuario  &  set DB_PASSWORD=minhasenha

4. EXECUTAR A APLICACAO
        cd 03_Codigo_Fonte/arenaxp-app
        mvn spring-boot:run
   Acesse: http://localhost:8080  (sera redirecionado para /login)

5. TESTES UNITARIOS (nao precisam de banco)
        mvn test

6. CREDENCIAIS DE TESTE
   E-mail                 Senha        Perfil
   admin@arenaxp.com      admin123     ADMIN
   carlos@arenaxp.com     tecnico123   TECNICO
   marina@arenaxp.com     tecnico123   COORDENADOR
   (senhas armazenadas com hash BCrypt)

7. TELAS / ROTAS
   /login          Login (publico)
   /               Inicio: resumo e itens criticos
   /equipamentos   CRUD + busca por nome/patrimonio + filtro por categoria
   /estoque        Lista A-Z + registro de Entrada/Saida + alerta de minimo
   /movimentacoes  Historico (mais recentes primeiro)

8. ROTEIRO RAPIDO DE TESTE
   - Login com senha errada -> mensagem de erro.
   - Acessar /equipamentos sem login -> redireciona para /login.
   - /estoque: Headset HyperX Cloud III tem saldo 4 e minimo 3.
     * SAIDA de 5 -> bloqueada (saldo insuficiente).
     * SAIDA de 1 -> saldo 3 e aparece o alerta
       "ESTOQUE CRITICO / ABAIXO DO MINIMO".
   - /equipamentos: excluir equipamento com movimentacao -> mensagem
     amigavel (sem erro 500).

9. ESTRUTURA TECNICA
   model (entidades JPA) | repository (Spring Data) | service (regra de saldo)
   controller (rotas) | security (Spring Security + BCrypt) | templates (Thymeleaf)
