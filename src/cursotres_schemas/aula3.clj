(ns cursotres-schemas.aula3
  (:use clojure.pprint)
  (:require [cursotres-schemas.db :as db]
            [cursotres-schemas.model :as model]
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
(pprint produtos)
(pprint (first produtos))

(db/um-produto! (dt/db conn) (:produto/id (first produtos)))

(db/um-produto! (dt/db conn) (model/uuid))





























; testando produto nome null
;(s/validate model/Produto {:produto/id (model/uuid) :produto/nome ""})
