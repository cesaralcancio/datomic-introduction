(ns cursoseis.aula1
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

(def produtos (db.produto/todos-os-produtos (dt/db conn)))
(def primeiro (first produtos))
(pprint primeiro)

(pprint (g/sample 10 model/Categoria))
(pprint (g/sample 10 model/Variacao gen/leaf-generators))
(pprint (g/sample 10 BigDecimal gen/leaf-generators))
