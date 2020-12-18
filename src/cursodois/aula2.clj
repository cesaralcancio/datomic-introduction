(ns cursodois.aula2
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as dt]))

(db/apaga-banco)

(def conn (db/abre-conexao))

(db/cria-schema conn)

(let [computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.00M)
      celular (model/novo-produto (model/uuid) "Celular Caro", "/celular", 15000.99M)
      calculadora {:produto/nome "Calculadora com 4 operações"}
      celular-barato (model/novo-produto (model/uuid) "Cel ular Barato", "/celular-barato", 500.00M)]
  (print @(dt/transact conn [computador celular calculadora celular-barato])))

(def db (dt/db conn))

(def produtos (db/todos-os-produtos-top-top (dt/db conn)))