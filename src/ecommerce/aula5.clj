(ns ecommerce.aula5
  (:use clojure.pprint)
  (:require [ecommerce.model :as model]
            [ecommerce.db :as db]
            [datomic.api :as dt]))

(db/apaga-banco)

(def conn (db/abre-conexao))

(db/cria-schema conn)

(let [computador (model/novo-produto "Computador Novo", "/computador-novo", 2500.20M)
      celular (model/novo-produto "Celular Caro", "/celular", 888888.10M)
      resultado @(dt/transact conn [computador celular])]
  (pprint resultado))

(def db-antigo (dt/db conn))

(let [calculadora {:produto/nome "Calculadora com 4 operações"}
      celular-barato (model/novo-produto "Celular Barato", "/celular-barato", 0.1M)
      resultado @(dt/transact conn [calculadora celular-barato])]
  (pprint resultado))

(def db-novo (dt/db conn))

(println "---")
(pprint (db/todos-os-produtos-top-top db-antigo))
(println "---")
(pprint (db/todos-os-produtos-top-top db-novo))

(pprint (db/todos-os-produtos-top-top (dt/as-of db-novo #inst "2020-12-06T19:50:38.098")))
(pprint (db/todos-os-produtos-top-top (dt/as-of db-novo #inst "2020-12-06T19:50:38.110")))
