(ns cursocinco_bancofiltrado.aula5
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
(let [[return3 venda-id3] (db.venda/adiciona! conn (:produto/id primeiro) 8)]
  (def return3 return3)
  (def venda-id3 venda-id3)
  )

(pprint (db.venda/todas-nao-canceladas (d/db conn)))

(pprint @(db.venda/altera-status! conn venda-id "preparando"))
(pprint @(db.venda/altera-status! conn venda-id2 "preparando"))
(pprint @(db.venda/altera-status! conn venda-id2 "a caminho"))
(pprint @(db.venda/altera-status! conn venda-id2 "entregue"))

(pprint (db.venda/historico (d/db conn) venda-id))
(pprint (db.venda/historico (d/db conn) venda-id2))
(pprint (db.venda/historico (d/db conn) venda-id3))

(def todas-vendas (db.venda/todas-inclusive-canceladas (d/db conn)))

(pprint (db.venda/historico-geral (d/db conn) #inst "2021-04-28T02:19:19.319-00:00"))
