(ns cursotres-schemas.aula1
  (:use clojure.pprint)
  (:require [cursotres-schemas.db :as db]
            [cursotres-schemas.model :as model]
            [datomic.api :as dt]
            [schema.core :as s]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db/apaga-banco!)
(def conn (db/abre-conexao!))
(db/cria-schema! conn)

(defn testa-schema []
  (def eletronicos (model/nova-categoria (model/uuid) "Eletronicos"))
  (def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.00M))

  (s/validate model/Categoria eletronicos)
  (s/validate model/Produto computador)
  (s/validate model/Produto (assoc computador :produto/categoria eletronicos)))
(testa-schema)

; testando
(db/cria-dados-de-exemplo! conn)
(db/todos-os-produtos (dt/db conn))
(db/todas-categorias (dt/db conn))