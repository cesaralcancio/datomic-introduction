(ns cursocinco_bancofiltrado.aula1
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

(def custo (db.venda/custo (dt/db conn) venda-id))
(def custo2 (db.venda/custo (dt/db conn) venda-id2))

(def instante (d/q '[:find ?instante .
                     :in $ ?id
                     :where [?venda :venda/id ?id ?tx true]
                     [?tx :db/txInstant ?instante]
                     ] (d/db conn) venda-id))
(def produto (d/q '[:find ?preco .
                    :in $ ?id
                    :where [?produto :produto/id ?id]
                    [?produto :produto/preco ?preco]
                    ] (d/db conn) (:produto/id primeiro)))

(pprint "Produto preco")
(pprint produto)

(pprint "Custo")
(pprint custo)
(pprint custo2)

(println "Instante")
(pprint instante)

(def produto-alterado @(db.produto/adiciona-ou-atualiza-produtos! conn [{:produto/id    (:produto/id primeiro)
                                                                         :produto/preco 100M}]))
