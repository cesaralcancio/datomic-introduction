(ns cursocinco-bancofiltrado.db.venda
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [cursocinco_bancofiltrado.model :as model]
            [schema.core :as s]
            [clojure.set :as cset]
            [datomic.api :as dt]
            [cursocinco-bancofiltrado.db.entidade :as db.entidade]))

(defn adiciona! [conn produto-id quantidade]
  (let [venda-id (model/uuid)
        return (d/transact conn [{
                                  :db/id            "venda"
                                  :venda/id         venda-id
                                  :venda/produto    [:produto/id produto-id]
                                  :venda/quantidade quantidade
                                  }])]
    [return venda-id]))

(defn instante-da-venda [db venda-id]
  (d/q '[:find ?instante .
         :in $ ?id
         :where [?venda :venda/id ?id ?tx true]
         [?tx :db/txInstant ?instante]] db venda-id))

(defn custo [db venda-id]
  (let [instante (instante-da-venda db venda-id)
        custo (d/q '[:find (sum ?preco-por-quantidade) .
                     :in $ ?id
                     :where [?venda :venda/id ?id]
                     [?venda :venda/produto ?produto]
                     [?venda :venda/quantidade ?quantidade]
                     [?produto :produto/preco ?preco]
                     [(* ?preco ?quantidade) ?preco-por-quantidade]] (d/as-of db instante) venda-id)]
    (println "custo em " instante)
    custo))

(defn cancela! [conn venda-id]
  (d/transact conn [[:db/retractEntity [:venda/id venda-id]]]))

(defn todas-nao-canceladas [db]
  (db.entidade/datomic-para-entidade
    (d/q '[:find ?id ?quantidade
           :where [?venda :venda/id ?id]
           [?venda :venda/quantidade ?quantidade]]
         db)))

(defn todas-inclusive-canceladas [db]
  (db.entidade/datomic-para-entidade
    (d/q '[:find ?id ?quantidade
           :where [?venda :venda/id ?id ?trx true]
           [?venda :venda/quantidade ?quantidade ?trx true]]
         (d/history db))))

(defn canceladas [db]
  (db.entidade/datomic-para-entidade
    (d/q '[:find ?id ?quantidade
           :where [?venda :venda/id ?id ?trx false]
           [?venda :venda/quantidade ?quantidade ?trx false]]
         (d/history db))))