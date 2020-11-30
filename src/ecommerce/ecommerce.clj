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

(let [computador (model/novo-produto "Celular Novo" "/celular/novo" 1500.10M)]
  (pprint (d/transact conn [computador])))

(pprint (d/q '[:find ?entidade
               :where [?entidade :produto/nome]] db))

(pprint "fim")