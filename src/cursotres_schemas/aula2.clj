(ns cursotres-schemas.aula2
  (:use clojure.pprint)
  (:require [cursotres-schemas.db :as db]
            [cursotres-schemas.model :as model]
            [datomic.api :as dt]
            [schema.core :as s]))

(s/set-fn-validation! false)
(s/set-fn-validation! true)

(db/apaga-banco!)
(def conn (db/abre-conexao!))
(db/cria-schema! conn)

(defn testa-schema []
  (def eletronicos (model/nova-categoria (model/uuid) "Eletronicos"))
  (def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.00M))

  (s/validate model/Categoria eletronicos)
  (s/validate model/Produto computador)
  (s/validate model/Produto (assoc computador :produto/categoria eletronicos)))
(testa-schema)

; testando
(db/cria-dados-de-exemplo! conn)
(db/todos-os-produtos (dt/db conn))
(db/todas-categorias (dt/db conn))

(def dama {:produto/nome  "Dama"
           :produto/slug  "/dama"
           :produto/preco 15M
           :produto/id    (model/uuid)})
(db/adiciona-produtos! conn [dama])
(db/um-produto (dt/db conn) (:produto/id dama))


(db/adiciona-produtos! conn [(assoc dama :produto/slug "/dama")])
(db/um-produto (dt/db conn) (:produto/id dama))

(db/adiciona-produtos! conn [(assoc dama :produto/preco 15M)])
(db/um-produto (dt/db conn) (:produto/id dama))


(defn atualiza-preco []
  (let [produto (db/um-produto (dt/db conn) (:produto/id dama))
        result (println "consulta produto preco: " produto)
        produto (assoc dama :produto/preco 99M)]
    (db/adiciona-produtos! conn [produto])
    (println "atualizado preco: " produto)))

(defn atualiza-slug []
  (println "atualizando slug")
  (let [produto (db/um-produto (dt/db conn) (:produto/id dama))
        result (println "consulta produto slug: " produto)
        result (println "dormiu")
        result (Thread/sleep 3000)
        result (println "acordou")
        produto (assoc dama :produto/slug "/jogo-de-dama")
        result (println produto)]
    (db/adiciona-produtos! conn [produto])
    (println "atualizado slug: " produto)))

; maneira correta para nao sobrescrever tudo
(defn atualiza-preco []
  (let [produto (db/um-produto (dt/db conn) (:produto/id dama))
        result (println "consulta produto preco: " produto)
        produto {:produto/id (:produto/id dama) :produto/preco 99M}]
    (db/adiciona-produtos! conn [produto])
    (println "atualizado preco: " produto)
    produto))

(defn atualiza-slug []
  (println "atualizando slug")
  (let [produto (db/um-produto (dt/db conn) (:produto/id dama))
        result (println "consulta produto slug: " produto)
        result (println "dormiu")
        result (Thread/sleep 3000)
        result (println "acordou")
        produto {:produto/id (:produto/id dama) :produto/slug "/jogo-de-dama"}
        result (println produto)]
    (db/adiciona-produtos! conn [produto])
    (println "atualizado slug: " produto)
    produto))


(defn roda-transacoes [trxs]
  (let [futuros (mapv #(future (%)) trxs)]
    (pprint (map deref futuros))
    (pprint "Resultado final")
    (pprint (db/um-produto (dt/db conn) (:produto/id dama)))
    )
  )

(roda-transacoes [atualiza-preco atualiza-slug])