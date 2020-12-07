(ns ecommerce.aula3
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as dt]))

(db/apaga-banco)

(def conn (db/abre-conexao))

(db/cria-schema conn)

(let [computador (model/novo-produto "Computador Novo", "/computador-novo", 2500.20M)
      celular (model/novo-produto "Celular Caro", "/celular", 888888.10M)
      calculadora {:produto/nome "Calculadora com 4 operações"}
      celular-barato (model/novo-produto "Celular Barato", "/celular-barato", 0.1M)]
  (dt/transact conn [computador celular calculadora celular-barato]))

(def db (dt/db conn))

(pprint (db/todos-os-produtos db))

(pprint (db/todos-os-produtos-por-slug-fixo db))

(pprint (db/todos-os-produtos-por-slug db "/computador-novo"))

(pprint (db/todos-os-slugs db))

(pprint (db/todos-os-precos db))

(pprint (db/todos-os-produtos-top db))

(pprint (db/todos-os-produtos-top-top db))
