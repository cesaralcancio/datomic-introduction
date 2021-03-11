(ns cursoquatro_bindings.aula1
  (:use clojure.pprint)
  (:require [cursotres-schemas.db :as db]
            [cursotres-schemas.model :as model]
            [datomic.api :as dt]
            [schema.core :as s]
            [datomic.api :as d]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db/apaga-banco!)
(def conn (db/abre-conexao!))
(db/cria-schema! conn)
(db/cria-dados-de-exemplo! conn)

(def produtos (db/todos-os-produtos (dt/db conn)))