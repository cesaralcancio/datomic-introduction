(ns cursoquatro_bindings.aula5
  (:use clojure.pprint)
  (:require [cursoquatro_bindings.db :as db]
            [cursoquatro_bindings.model :as model]
            [datomic.api :as dt]
            [datomic.api :as d]
            [schema.core :as s]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db/apaga-banco!)
(def conn (db/abre-conexao!))
(db/cria-schema! conn)
(db/cria-dados-de-exemplo! conn)

(def produtos (db/todos-os-produtos (dt/db conn)))
(def primeiro (first produtos))
(do primeiro)

; ref https://docs.datomic.com/on-prem/reference/database-functions.html
(def ola (dt/function '{:lang   :clojure
                        :params [nome]
                        :code   (str "Ola " nome)}))
(ola "Cesar!")

; interessante que aqui nao existe o d, mas dentro do codigo db.clj existe....
(def incrementa-visualizacao
  #db/fn {:lang   :clojure
          :params [db produto-id]
          :code
                  (let [visualizacoes (d/q '[:find ?visualizacoes .
                                             :in $ ?id
                                             :where [?produto :produto/id ?id]
                                             [?produto :produto/visualizacoes ?visualizacoes]] db produto-id)
                        atual (or visualizacoes 0)
                        total-novo (inc atual)
                        ]
                    [{:produto/id            produto-id
                      :produto/visualizacoes total-novo}])})

(dt/transact conn [{:db/doc "Incrementa o atributo :produto/visualizacoes de uma entidade"
                    :db/ident :incrementa-visualizacao
                    :db/fn incrementa-visualizacao}])

(incrementa-visualizacao (dt/db conn) (:produto/id primeiro))

(dotimes [n 10] (db/visualizacao! conn (:produto/id primeiro)))
(db/um-produto (dt/db conn) (:produto/id primeiro))