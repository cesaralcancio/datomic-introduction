(ns cursoquatro_bindings.aula3
  (:use clojure.pprint)
  (:require [cursoquatro_bindings.db :as db]
            [cursoquatro_bindings.model :as model]
            [datomic.api :as dt]
            [schema.core :as s]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db/apaga-banco!)
(def conn (db/abre-conexao!))
(db/cria-schema! conn)
(db/cria-dados-de-exemplo! conn)


(def produtos (db/todos-os-produtos (dt/db conn)))
(def primeiro (first produtos))
(do primeiro)