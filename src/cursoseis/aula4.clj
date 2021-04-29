(ns cursoseis.aula4
  (:use clojure.pprint)
  (:require [cursoseis.db.config :as db.config]
            [cursoseis.db.produto :as db.produto]
            [datomic.api :as dt]
            [datomic.api :as d]
            [schema.core :as s]
            [schema-generators.generators :as g]
            [clojure.test.check.generators :as clojure.g]
            [cursoseis.model :as model]
            [cursoseis.generators :as gen]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))
(db.config/cria-schema! conn)
(db.config/cria-dados-de-exemplo! conn)

(println "Aula 4")
