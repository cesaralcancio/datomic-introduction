(ns cursodois.aula4
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as dt]))

(db/apaga-banco!)

(def conn (db/abre-conexao!))

(db/cria-schema! conn)

(def eletronicos (model/nova-categoria (model/uuid) "Eletronicos"))
(def esporte (model/nova-categoria (model/uuid) "Esporte"))
@(db/adiciona-categorias! conn [eletronicos esporte])
(db/todas-categorias-pull (dt/db conn))

(def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.00M))
(def celular (model/novo-produto (model/uuid) "Celular Caro", "/celular", 15000.99M))
(def celular-barato (model/novo-produto (model/uuid) "Celular Barato", "/celular-barato", 500.00M))
(def tabuleiro-de-xadrez (model/novo-produto (model/uuid) "Tabuleiro de Xadrez", "/tabuleiro-xadrez", 30M))

@(db/adiciona-produtos! conn [computador celular celular-barato, tabuleiro-de-xadrez])
(db/todos-os-produtos-top-top (dt/db conn))

; relacionar produto com categoria
(db/atribui-categorias! conn [computador celular celular-barato, tabuleiro-de-xadrez] eletronicos)
(db/atribui-categorias! conn [tabuleiro-de-xadrez] esporte)

; por produto.uuid
(db/um-produto-por-produto-uuid (dt/db conn) (:produto/id computador))

; todos os nomes de produtos + categorias
(db/todos-nomes-produtos-categorias (dt/db conn))
(db/todos-produtos-por-categoria-forward (dt/db conn) "Eletronicos")
(db/todos-produtos-por-categoria-forward (dt/db conn) "Esporte")

(db/todos-produtos-por-categoria-backward (dt/db conn) "Eletronicos")
(db/todos-produtos-por-categoria-backward (dt/db conn) "Esporte")