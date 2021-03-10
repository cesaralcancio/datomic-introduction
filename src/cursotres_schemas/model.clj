(ns cursotres-schemas.model
  (:require [schema.core :as s]))

(def Categoria {:categoria/id   java.util.UUID
                :categoria/nome s/Str})

(def Produto
  {:produto/id                             java.util.UUID
   (s/optional-key :produto/nome)          s/Str
   (s/optional-key :produto/slug)          s/Str
   (s/optional-key :produto/preco)         BigDecimal
   (s/optional-key :produto/categoria)     Categoria
   (s/optional-key :produto/palavra-chave) [s/Str]
   (s/optional-key :produto/estoque)       s/Num})


(defn uuid [] (java.util.UUID/randomUUID))

(defn novo-produto
  ([nome slug preco]
   (novo-produto (uuid) nome slug preco))
  ([uuid nome slug preco]
   (novo-produto uuid nome slug preco 0))
  ([uuid nome slug preco estoque]
   {:produto/id      uuid
    :produto/nome    nome
    :produto/slug    slug
    :produto/preco   preco
    :produto/estoque estoque}))

(defn nova-categoria
  [uuid nome]
  {:categoria/id   uuid
   :categoria/nome nome})

(println "Carregando modelo")