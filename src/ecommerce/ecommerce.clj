(ns ecommerce.ecommerce
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as d]))

(db/apaga-banco)

(def conn (db/abre-conexao))

(db/cria-schema conn)

(let [computador (model/novo-produto "Computador Novo" "/computador/novo" 3500.10M)]
  (pprint (d/transact conn [computador])))

(pprint "query...")

(def db (d/db conn))

(pprint (d/q '[:find ?entidade
               :where [?entidade :produto/nome]] db))

(let [celular (model/novo-produto "Celular Novo" "/celular/novo" 1500.10M)]
  (pprint (d/transact conn [celular])))

(pprint (d/q '[:find ?entidade
               :where [?entidade :produto/nome]] db))

; suporta nao enviar os valores
(let [calculadora {:produto/nome "Calculadora"}]
  (pprint (d/transact conn [calculadora])))

; nao suporta
; (let [calculadora {:produto/nome "Calculadora" :produto/slug nil}]
; (pprint (d/transact conn [calculadora])))


(let [celular-barato (model/novo-produto "Celular Barato" "/celular/barato" 500.99M)
      resultado @(d/transact conn [celular-barato])
      id-entidade-old (first (vals (:tempids resultado)))
      id-entidade (-> resultado :tempids vals first)]
  (pprint resultado)
  (pprint id-entidade)
  (pprint @(d/transact conn [[:db/add id-entidade :produto/preco 0.99M]]))
  (pprint @(d/transact conn [[:db/retract id-entidade :produto/slug "/celular/barato"]])))




