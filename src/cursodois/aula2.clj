(ns cursodois.aula2
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as dt]))

(db/apaga-banco!)

(def conn (db/abre-conexao!))

(db/cria-schema! conn)

(def eletronicos (model/nova-categoria (model/uuid) "Eletronicos"))
(def esporte (model/nova-categoria (model/uuid) "Esporte"))

(db/adiciona-categorias! conn [eletronicos esporte])

(db/todas-categorias (dt/db conn))
(db/todas-categorias-pull (dt/db conn))

(def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.00M))
(def celular (model/novo-produto (model/uuid) "Celular Caro", "/celular", 15000.99M))
(def calculadora {:produto/nome "Calculadora com 4 operações"})
(def celular-barato (model/novo-produto (model/uuid) "Celular Barato", "/celular-barato", 500.00M))
(def celular-barato-2 (model/novo-produto (:produto/id celular-barato) "Celular Barato Dois", "/celular-barato-2", 100.00M))
(def celular-barato-3 {:produto/id (:produto/id celular-barato) :produto/nome "Celular Barato Tres"})
(def tabuleiro-de-xadrez (model/novo-produto (model/uuid) "Tabuleiro de Xadrez", "/tabuleiro-xadrez", 30M))

(db/adiciona-produtos! conn [computador celular calculadora celular-barato, tabuleiro-de-xadrez])
(db/adiciona-produtos! conn [celular-barato-2])
(db/adiciona-produtos! conn [celular-barato-3])

(db/todos-os-produtos-top-top (dt/db conn))

; relacionar produto com categoria
(db/atribui-categorias! conn [computador celular celular-barato, tabuleiro-de-xadrez celular-barato-2] eletronicos)
(db/atribui-categorias! conn [tabuleiro-de-xadrez] esporte)

; por db.id
(db/um-produto (dt/db conn) 17592186045421)
(db/um-produto-melhor (dt/db conn) 17592186045421)

; por produto.uuid
(db/um-produto-por-produto-uuid (dt/db conn) (:produto/id computador))