(ns cursoquatro_bindings.aula0
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
(def categorias (db/todas-categorias (dt/db conn)))

(do produtos)
(pprint produtos)
(pprint (first produtos))

(db/um-produto! (dt/db conn) (:produto/id (first produtos)))
(db/um-produto (dt/db conn) (model/uuid))

; com estoque
(def produtos-com-estoque (db/todos-os-produtos-vendaveis (dt/db conn)))
(do produtos-com-estoque)

(db/um-produto-vendavel (dt/db conn) (:produto/id (first produtos)))
(db/um-produto-vendavel (dt/db conn) (:produto/id (second produtos)))

(defn verifica-se-pode-vender [produto]
  (println "")
  (println "Analisando produto...")
  (println (:produto/estoque produto))
  (println (:produto/digital produto))
  (let [produto-from-datomic (db/um-produto-vendavel (dt/db conn) (:produto/id produto))]
    ; (pprint produto-from-datomic)
    produto-from-datomic))

(map verifica-se-pode-vender produtos)