(ns cursodois.aula6
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

@(db/adiciona-produtos! conn [computador celular celular-barato, tabuleiro-de-xadrez] "192.168.0.1")
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
(def esporte-id (-> esporte :categoria/id))
(def dama
  {:produto/nome      "Dama"
   :produto/slug      "/dama"
   :produto/preco     15M
   :produto/id        (model/uuid)
   :produto/categoria [:categoria/id esporte-id]})


(pprint @(db/adiciona-produtos! conn [camiseta dama] "192.168.0.2"))
(db/todos-os-produtos-top-top (dt/db conn))

(db/todos-os-produtos-maior-preco (dt/db conn))
(db/todos-os-produtos-mais-caros (dt/db conn))


; teste pra entender
(def ip "192.168.0.1")
(def produtos [computador celular celular-barato, tabuleiro-de-xadrez])
(def db-add-ip [:db/add "datomic.tx" :tx-data/ip ip])
(conj produtos db-add-ip)
; fim pra entender

(db/todos-os-produtos-do-ip (dt/db conn) "192.168.0.1")
(db/todos-os-produtos-do-ip (dt/db conn) "192.168.0.2")
(db/todos-os-produtos-do-ip (dt/db conn) "192.168.0.3")
