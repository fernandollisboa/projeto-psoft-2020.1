-- Criando o Administrador
-- /*
--  {
--   "cpfLogin": "00000000000",
--   "senhaLogin": "admin",
--   "tipoLogin": "Administrador"
-- }
--  */
INSERT INTO cartao_vacina(cartao_sus, situacao) VALUES ('000000000000000','VACINACAOFINALIZADA');
INSERT INTO cidadao (email,cpf,data_nascimento,nome,senha,cartao_vacina_cartao_sus) VALUES ('admin@admin','00000000000',date'1900-05-28','Administrador','admin','000000000000000');
