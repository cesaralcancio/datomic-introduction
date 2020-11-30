(ns ecommerce.model
  (:use clojure.pprint)
  (:require [datomic.api :as d]))

(defn novo-produto
  [nome slug preco]
  {
   :produto/nome nome
   :produto/slug slug
   :produto/preco preco})

(pprint (novo-produto "Computador" "/computador" 3500.10))
