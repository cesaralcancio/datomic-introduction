(ns cursoquatro_bindings.aula2
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
(def primeiro (first produtos))
(do primeiro)

; sem threading
@(db/atualiza-preco conn (:produto/id primeiro) (:produto/preco (first (db/todos-os-produtos (dt/db conn)))) 2503M)

; com threading
(defn help-atualiza [preco-velho conn produto-id preco-novo]
  @(db/atualiza-preco conn produto-id preco-velho preco-novo))
(-> (dt/db conn)
    db/todos-os-produtos
    first
    :produto/preco
    (help-atualiza conn (:produto/id primeiro) 200M))



(def segundo (second produtos))
(def a-atualizar {:produto/id (:produto/id segundo) :produto/preco 50M :produto/estoque 10 :produto/slug "/celular-atualizado"})
(do segundo)
(do a-atualizar)
(db/atualiza-produto! conn segundo a-atualizar)