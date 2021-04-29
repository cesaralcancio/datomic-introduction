(ns cursoseis.db.variacao
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [cursoseis.model :as model]
            [schema.core :as s]))

(s/defn adiciona-variacao!
  [conn produto-id :- java.util.UUID variacao-nome :- s/Str variacao-preco :- s/Num]
  (let []
    (d/transact conn [{
                       :db/id          "id-temporario"
                       :variacao/nome  variacao-nome
                       :variacao/preco variacao-preco
                       :variacao/id    (model/uuid)
                       }
                      {:produto/id       produto-id
                       :produto/variacao "id-temporario"}])))

(println "Carregado variacao")
