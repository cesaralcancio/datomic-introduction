(ns cursocinco_bancofiltrado.aula4
  (:use clojure.pprint)
  (:require [cursocinco-bancofiltrado.db.config :as db.config]
            [cursocinco-bancofiltrado.db.produto :as db.produto]
            [datomic.api :as dt]
            [datomic.api :as d]
            [schema.core :as s]
            [cursocinco-bancofiltrado.db.venda :as db.venda]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))
(db.config/cria-schema! conn)
(db.config/cria-dados-de-exemplo! conn)

(def produtos (db.produto/todos-os-produtos (dt/db conn)))
(def primeiro (first produtos))
(pprint primeiro)

(let [[return venda-id] (db.venda/adiciona! conn (:produto/id primeiro) 3)]
  (def return return)
  (def venda-id venda-id)
  )
(let [[return2 venda-id2] (db.venda/adiciona! conn (:produto/id primeiro) 4)]
  (def return2 return2)
  (def venda-id2 venda-id2)
  )

(pprint @(db.venda/cancela! conn venda-id))

(pprint (db.venda/todas-nao-canceladas (d/db conn)))

(pprint (db.venda/todas-inclusive-canceladas (d/db conn)))

(pprint (db.venda/canceladas (d/db conn)))

(pprint (db.venda/historico (d/db conn) venda-id))

(pprint (:produto/preco primeiro))

(pprint @(db.produto/adiciona-ou-altera! conn [{:produto/id    (:produto/id primeiro)
                                                 :produto/preco 300M}]))
(pprint @(db.produto/adiciona-ou-altera! conn [{:produto/id    (:produto/id primeiro)
                                                 :produto/preco 250M}]))
(pprint @(db.produto/adiciona-ou-altera! conn [{:produto/id    (:produto/id primeiro)
                                                 :produto/preco 277M}]))
(pprint @(db.produto/adiciona-ou-altera! conn [{:produto/id    (:produto/id primeiro)
                                                 :produto/preco 20M}]))

(pprint (db.produto/historico-de-preco (d/db conn) (:produto/id primeiro)))