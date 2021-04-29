(ns cursoseis.db.categoria
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [cursoseis.model :as model]
            [schema.core :as s]
            [cursoseis.db.entidade :as db.entidade]))

; TODAS AS CATEGORIAS
(defn todas-categorias [db]
  (d/q '[:find ?id ?nome
         :keys categoria/id categoria/nome
         :where
         [?seila :categoria/id ?id]
         [?seila :categoria/nome ?nome]] db))

(s/defn todas-categorias :- [model/Categoria] [db]
  (db.entidade/datomic-para-entidade (d/q '[:find [(pull ?dbid [*]) ...]
                                            :where
                                            [?dbid :categoria/id]] db)))

(s/defn adiciona-categorias! [conn categorias :- [model/Categoria]]
  (d/transact conn categorias))

; relacionar individualmente
;(d/transact conn [[:db/add
;                   [:produto/id (:produto/id computador)]
;                   :produto/categoria
;                   [:categoria/id (:categoria/id eletronicos)]]])
;
;(d/transact conn [[:db/add
;                   [:produto/id (:produto/id tabuleiro-de-xadrez)]
;                   :produto/categoria
;                   [:categoria/id (:categoria/id esporte)]]])

; ATRIBUI CATEGORIAS
; se o produt n tem ID da erro
(defn atribui-categorias! [conn produtos categoria]
  (let [para-transacionar (reduce (fn [db-adds produto]
                                    (conj db-adds [:db/add
                                                   [:produto/id (:produto/id produto)]
                                                   :produto/categoria
                                                   [:categoria/id (:categoria/id categoria)]]))
                                  []
                                  produtos)]
    (d/transact conn para-transacionar)))

(println "Carregado categorias")
