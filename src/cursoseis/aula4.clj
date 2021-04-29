(ns cursoseis.aula2
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

; (db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))
(db.config/cria-schema! conn)
(db.config/cria-dados-de-exemplo! conn)

(def produtos (db.produto/todos-os-produtos (dt/db conn)))
(def primeiro (first produtos))
(pprint primeiro)

(pprint (g/sample 10 model/Categoria))
(pprint (g/sample 10 model/Variacao gen/leaf-generators))
(pprint (g/sample 10 BigDecimal gen/leaf-generators))


(defn gera-1000-produtos [conn]
  (dotimes [atual 50]
    (def produtos-gerados (g/sample 200 model/Produto gen/leaf-generators))
    (println atual
             (count @(db.produto/adiciona-ou-altera! conn produtos-gerados)))))

; (time (gera-1000-produtos conn))

(time (count (db.produto/todos-os-produtos (d/db conn))))

(time (db.produto/busca-mais-caro (d/db conn)))

(time (count (db.produto/busca-mais-caro-que (d/db conn) 50000M)))

(def preco-mais-caro (db.produto/busca-mais-caro (d/db conn)))

(println preco-mais-caro)
(time (db.produto/busca-por-preco (d/db conn) preco-mais-caro))
(time (count (db.produto/busca-por-preco-e-nome (d/db conn) 10M "com")))
