(ns cursocinco_bancofiltrado.aula5
  (:use clojure.pprint)
  (:require [cursocinco_bancofiltrado.db :as db]
            [cursocinco_bancofiltrado.model :as model]
            [datomic.api :as dt]
            [datomic.api :as d]
            [schema.core :as s]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db/apaga-banco!)
(def conn (db/abre-conexao!))
(db/cria-schema! conn)
(db/cria-dados-de-exemplo! conn)

(def produtos (db/todos-os-produtos (dt/db conn)))
(do produtos)
(println produtos)