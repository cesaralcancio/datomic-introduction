(ns cursodois.aula5
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

; nested maps
(def camiseta
  {:produto/nome      "Camiseta"
   :produto/slug      "/camiseta"
   :produto/preco     30M
   :produto/id        (model/uuid)
   :produto/categoria {:categoria/nome "Roupas"
                       :categoria/id   (model/uuid)}})

; lookup ref
(def categoria-id (-> camiseta :produto/categoria :categoria/id))
(def bermuda
  {:produto/nome      "Bermuda"
   :produto/slug      "/bermuda"
   :produto/preco     30M
   :produto/id        (model/uuid)
   :produto/categoria [:categoria/id categoria-id]})

(def cueca
  {:produto/nome      "Cueca"
   :produto/slug      "/cueca"
   :produto/preco     10M
   :produto/id        (model/uuid)
   :produto/categoria [:categoria/id categoria-id]})

(def jaqueta
  {:produto/nome      "Jaqueta"
   :produto/slug      "/jaqueta"
   :produto/preco     200M
   :produto/id        (model/uuid)
   :produto/categoria [:categoria/id categoria-id]})

(def blusa
  {:produto/nome      "Blusa"
   :produto/slug      "/blusa"
   :produto/preco     250M
   :produto/id        (model/uuid)
   :produto/categoria {:categoria/nome "Roupas e Blusas"
                       :categoria/id   categoria-id}})

(pprint @(db/adiciona-produtos! conn [blusa]))
(pprint @(db/adiciona-produtos! conn [camiseta bermuda cueca jaqueta]))

(db/todos-os-produtos-top-top (dt/db conn))
(db/resumo-dos-produtos (dt/db conn))
(db/resumo-dos-produtos-por-categoria (dt/db conn))