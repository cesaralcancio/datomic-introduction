(ns cursoquatro_bindings.aula1
  (:use clojure.pprint)
  (:require [cursoquatro_bindings.db :as db]
            [cursoquatro_bindings.model :as model]
            [datomic.api :as dt]
            [schema.core :as s]
            [datomic.api :as d]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db/apaga-banco!)
(def conn (db/abre-conexao!))
(db/cria-schema! conn)
(db/cria-dados-de-exemplo! conn)

(def produtos (db/todos-os-produtos (dt/db conn)))
(db/todos-os-produtos-nas-categorias (dt/db conn) ["Eletronicos" "Alimentacao"])
(db/todos-os-produtos-nas-categorias (dt/db conn) ["Eletronicos" "Esporte"])
(db/todos-os-produtos-nas-categorias (dt/db conn) [])
(db/todos-os-produtos-nas-categorias (dt/db conn) ["Blabla"])

(db/todos-os-produtos-nas-categorias-e-digital (dt/db conn) ["Esporte" "Alimentacao"] false)
(db/todos-os-produtos-nas-categorias-e-digital (dt/db conn) ["Eletronicos" "Alimentacao"] true)
(db/todos-os-produtos-nas-categorias-e-digital (dt/db conn) ["Eletronicos" "Alimentacao"] false)