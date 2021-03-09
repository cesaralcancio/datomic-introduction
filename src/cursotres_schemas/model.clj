(ns cursotres-schemas.model
  (:require [schema.core :as s]))

(def Categoria {:categoria/id   java.util.UUID
                :categoria/nome s/Str})

(def Produto
  {:produto/nome                           s/Str
   :produto/slug                           s/Str
   :produto/preco                          BigDecimal
   :produto/id                             java.util.UUID
   (s/optional-key :produto/categoria)     Categoria
   (s/optional-key :produto/palavra-chave) [s/Str]})


(defn uuid [] (java.util.UUID/randomUUID))

(defn novo-produto
  [uuid nome slug preco]
  {
   :produto/id    uuid
   :produto/nome  nome
   :produto/slug  slug
   :produto/preco preco})

(defn nova-categoria
  [uuid nome]
  {:categoria/id   uuid
   :categoria/nome nome})

(println "Carregando modelo")