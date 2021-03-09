(ns cursotres-schemas.model)

(defn uuid [] (java.util.UUID/randomUUID))

(defn novo-produto
  [uuid nome slug preco]
  {
   :produto/id   uuid
   :produto/nome nome
   :produto/slug slug
   :produto/preco preco})

(defn nova-categoria
  [uuid nome]
  {:categoria/id uuid
   :categoria/nome nome})

(println "Carregando modelo")