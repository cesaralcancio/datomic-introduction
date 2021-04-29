(ns cursoseis.aula4
  (:use clojure.pprint)
  (:require [cursoseis.db.config :as db.config]
            [cursoseis.db.produto :as db.produto]
            [datomic.api :as dt]
            [datomic.api :as d]
            [schema.core :as s]
            [schema-generators.generators :as g]
            [clojure.test.check.generators :as clojure.g]
            [cursoseis.model :as model]
            [cursoseis.generators :as gen]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))
(db.config/cria-schema! conn)
(db.config/cria-dados-de-exemplo! conn)

(defn propriedades-do-valor [valor]
  (cond (= valor java.util.UUID) {:db/valueType :db.type/uuid
                                  :db/unique    :db.unique/identity}
        (= valor s/Str) {:db/valueType :db.type/string}
        (= valor BigDecimal) {:db/valueType :db.type/bigdec}
        (= valor Long) {:db/valueType :db.type/long}
        (= valor s/Bool) {:db/valueType :db.type/boolean}
        (vector? valor) (merge {:db/cardinality :db.cardinality/many} (propriedades-do-valor (first valor)))
        (map? valor) {:db/valueType :db.type/ref}
        :else {:db/valueType (str "unknown: " valor " and type: " (type valor))}))

(defn nome-chave [chave]
  (cond (keyword? chave) chave
        (instance? schema.core.OptionalKey chave) (get chave :k)
        :else (str "unknown: " chave " and type: " (type chave))))

(defn chave-valor-para-definicao [[chave valor]]
  (let [base {:db/ident       (nome-chave chave)
              :db/cardinality :db.cardinality/one}
        extra (propriedades-do-valor valor)
        schema-do-datomic (merge base extra)]
    schema-do-datomic))

(defn schema-to-datomic [definicao]
  (mapv chave-valor-para-definicao definicao)
  )

;(schema-to-datomic model/Categoria)
;(schema-to-datomic model/Variacao)
(pprint (schema-to-datomic model/Produto))

(println "Aula 4")
