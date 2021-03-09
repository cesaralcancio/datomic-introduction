(ns cursotres-schemas.aula1
  (:use clojure.pprint)
  (:require [cursotres-schemas.db :as db]
            [cursotres-schemas.model :as model]
            [datomic.api :as dt]))

(db/apaga-banco!)
(def conn (db/abre-conexao!))
(db/cria-schema! conn)
(db/cria-dados-de-exemplo! conn)

(def db (dt/db conn))

; trazer dois
(pprint (count (db/todos-os-produtos-por-preco db 1000)))

; trazer um
(pprint (count (db/todos-os-produtos-por-preco db 5000)))

; testando
(db/todos-os-produtos-top-top (dt/db conn))
(db/todas-categorias-pull (dt/db conn))
