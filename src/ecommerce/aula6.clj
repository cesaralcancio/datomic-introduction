(ns ecommerce.aula6
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as dt]))

(db/apaga-banco)

(def conn (db/abre-conexao))

(db/cria-schema conn)

(let [computador (model/novo-produto "Computador Novo", "/computador-novo", 2500.00M)
      celular (model/novo-produto "Celular Caro", "/celular", 15000.99M)
      calculadora {:produto/nome "Calculadora com 4 operações"}
      celular-barato (model/novo-produto "Celular Barato", "/celular-barato", 500.00M)]
  (print @(dt/transact conn [computador celular calculadora celular-barato])))

(def db (dt/db conn))

; trazer dois
(pprint (count (db/todos-os-produtos-por-preco db 1000)))

; trazer um
(pprint (count (db/todos-os-produtos-por-preco db 5000)))

; testando
(db/todos-os-produtos-top-top (dt/db conn))

(dt/transact conn [[:db/add 17592186045418 :produto/palavra-chave "desktop"]
                   [:db/add 17592186045418 :produto/palavra-chave "computador"]])

(dt/transact conn [[:db/retract 17592186045418 :produto/palavra-chave "computador"]])

(dt/transact conn [[:db/add 17592186045419 :produto/palavra-chave "celular"]
                   [:db/add 17592186045421 :produto/palavra-chave "celular"]])

(db/todos-os-produtos-por-palavra-chave (dt/db conn) "celular")
(db/todos-os-produtos-por-palavra-chave (dt/db conn) "desktop")
(db/todos-os-produtos-por-palavra-chave (dt/db conn) "computador")
(db/todos-os-produtos-por-palavra-chave (dt/db conn) "barato")

