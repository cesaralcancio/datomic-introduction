(ns cursocinco_bancofiltrado.aula5
  (:use clojure.pprint)
  (:require [cursocinco-bancofiltrado.db.config :as db.config]
            [cursocinco-bancofiltrado.db.produto :as db.produto]
            [datomic.api :as dt]
            [datomic.api :as d]
            [schema.core :as s]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))
(db.config/cria-schema! conn)
(db.config/cria-dados-de-exemplo! conn)

(def produtos (db.produto/todos-os-produtos (dt/db conn)))
(do produtos)
(println produtos)
