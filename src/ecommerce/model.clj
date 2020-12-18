(ns ecommerce.model
  (:use clojure.pprint)
  (:require [datomic.api :as d]))

(defn uuid [] (java.util.UUID/randomUUID))

(defn novo-produto
  [uuid nome slug preco]
  {
   :produto/id   uuid
   :produto/nome nome
   :produto/slug slug
   :produto/preco preco})

(pprint (novo-produto (uuid) "Computador" "/computador" 3500.10))
