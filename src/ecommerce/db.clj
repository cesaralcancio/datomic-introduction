(ns ecommerce.db
  (:use clojure.pprint)
  (:require [datomic.api :as d]))

(pprint (def db-uri "datomic:dev://localhost:4334/hello"))

(defn abre-conexao []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn apaga-banco []
  (d/delete-database db-uri))

; id entidade,    atributo,       valor,        id transaction,     operacao (insert/delete)
; 15              :produto/nome   Computador    123                 true
; 15              :produto/slug   /computador   456                 true
; 16              :produto/nome   Celular       789                 true
; 16              :produto/slug   /celular      159                 true


(def schema [{:db/ident         :produto/nome
              :db/valueType     :db.type/string
              :db/cardinality   :db.cardinality/one
              :db/doc           "O nome de um produto"
              }
             {:db/ident         :produto/slug
              :db/valueType     :db.type/string
              :db/cardinality   :db.cardinality/one
              :db/doc           "O caminho para acessar o produto via http"
              }
             {:db/ident         :produto/preco
              :db/valueType     :db.type/bigdec
              :db/cardinality   :db.cardinality/one
              :db/doc           "O preco de um produto com precisao monetaria."
              }])

(defn cria-schema [conn]
  (d/transact conn schema))

; #datom [id-da-entidade atributo valor id-da-tx added?]
; #datom [72 10 :produto/nome 13194139534312 true]
; #datom [72 40 23 13194139534312 true]
; #datom [72 41 35 13194139534312 true]
; #datom [72 62 "O nome de um produto" 13194139534312 true]
; #datom [73 10 :produto/slug 13194139534312 true]
; #datom [73 40 23 13194139534312 true]
; #datom [73 41 35 13194139534312 true]
; #datom [73 62 "O caminho para acessar esse produto via http 13194139534312 true]


(defn todos-os-produtos [db]
  (d/q '[:find ?entidade
         :where [?entidade :produto/nome]] db))
