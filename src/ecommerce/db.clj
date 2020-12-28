(ns ecommerce.db
  (:use clojure.pprint)
  (:require [datomic.api :as d]))

(pprint (def db-uri "datomic:dev://localhost:4334/hello"))

(defn abre-conexao! []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn apaga-banco! []
  (d/delete-database db-uri))

; id entidade,    atributo,       valor,        id transaction,     operacao (insert/delete)
; 15              :produto/nome   Computador    123                 true
; 15              :produto/slug   /computador   123                 true
; 15              :produto/valor  10.15M        123                 true
; 16              :produto/nome   Celular       456                 true
; 16              :produto/slug   /celular      789                 true


(def schema [
             ; Produtos
             {:db/ident       :produto/nome
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "O nome de um produto"
              }
             {:db/ident       :produto/slug
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "O caminho para acessar o produto via http"
              }
             {:db/ident       :produto/preco
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "O preco de um produto com precisao monetaria."
              }
             {
              :db/ident       :produto/palavra-chave
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/many
              :db/doc         "Palavras chave para o produto"
              }
             {
              :db/ident       :produto/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              :db/doc         "Produto ID"
              }
             {
              :db/ident       :produto/categoria
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/one
              }
             ; Categorias
             {
              :db/ident       :categoria/nome
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              }
             {
              :db/ident       :categoria/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              }
             ])

(defn cria-schema! [conn]
  (d/transact conn schema))

; #datom [id-da-entidade atributo valor id-da-tx added?]
; #datom [72 10 :produto/nome 13194139534312 true]
; #datom [72 40 23 13194139534312 true]
; #datom [72 41 35 13194139534312 true]
; #datom [72 62 "O nome de um produto" 13194139534312 true]
; #datom [73 10 :produto/slug 13194139534312 true]
; #datom [73 40 23 13194139534312 true]
; #datom [73 41 35 13194139534312 true]
; #datom [73 62 "O caminho para acessar esse produto via http 13194139534312 true]


(defn todos-os-produtos [db]
  (d/q '[:find ?entidade ?valor
         :where [?entidade :produto/slug ?valor]] db))

(defn todos-os-produtos-por-slug-fixo [db]
  (d/q '[:find ?entidade
         :where [?entidade :produto/slug "/computador-novo"]] db))

(defn todos-os-produtos-por-slug [db slug]
  (d/q '[:find ?entidade
         :in $ ?slug
         :where [?entidade :produto/slug ?slug]] db slug))

; se nao for usar pode usar um underscore _
(defn todos-os-slugs [db]
  (d/q '[:find ?slug
         :where [_ :produto/slug ?slug]] db))

(defn todos-os-precos [db]
  (d/q '[:find ?nome ?preco
         :keys produto/nome produto/preco
         :where
         [?produto :produto/preco ?preco]
         [?produto :produto/nome ?nome]] db))

(defn todos-os-produtos-top [db]
  (d/q '[:find (pull ?produto [:produto/nome :produto/slug :produto/preco])
         :where [?produto :produto/nome]] db))

(defn todos-os-produtos-top-top [db]
  (d/q '[:find (pull ?produto [*])
         :where [?produto :produto/nome]] db))

; em geral vamo deixar as condicoes da mais restritiva para a menos restritiva
; o plano de acao somos nos quem tomamos...
(defn todos-os-produtos-por-preco [db preco-minimo-busca]
  (d/q '[:find ?nome ?preco
         :in $ ?preco-minimo
         :keys produto/nome produto/preco
         :where
         [?produto :produto/preco ?preco]
         [(> ?preco ?preco-minimo)]
         [?produto :produto/nome ?nome]] db preco-minimo-busca))

(defn todos-os-produtos-por-palavra-chave [db palavra-chave-buscada]
  (d/q '[:find (pull ?produto [*])
         :in $ ?palavra-chave
         :where [?produto :produto/palavra-chave ?palavra-chave]]
       db palavra-chave-buscada))

(defn um-produto [db db-id]
  (d/q '[:find (pull ?id [*])
         :in $ ?id
         ] db db-id))

(defn um-produto-melhor [db db-id]
  (d/pull db '[*] db-id))

(defn um-produto-por-produto-uuid [db produto-uuid]
  (d/pull db '[*] [:produto/id produto-uuid]))

(defn todas-categorias [db]
  (d/q '[:find ?id ?nome
         :keys categoria/id categoria/nome
         :where
         [?seila :categoria/id ?id]
         [?seila :categoria/nome ?nome]] db))

(defn todas-categorias-pull [db]
  (d/q '[:find (pull ?dbid [*])
         :where
         [?dbid :categoria/id]] db))






(defn adiciona-produtos! [conn produtos]
  @(d/transact conn produtos))

(defn adiciona-categorias! [conn categorias]
  @(d/transact conn categorias))

; relacionar individualmente
;(d/transact conn [[:db/add
;                   [:produto/id (:produto/id computador)]
;                   :produto/categoria
;                   [:categoria/id (:categoria/id eletronicos)]]])
;
;(d/transact conn [[:db/add
;                   [:produto/id (:produto/id tabuleiro-de-xadrez)]
;                   :produto/categoria
;                   [:categoria/id (:categoria/id esporte)]]])

; se o produt n tem ID da erro
(defn atribui-categorias! [conn produtos categoria]
  (let [para-transacionar (reduce (fn [db-adds produto]
                                    (conj db-adds [:db/add
                                                   [:produto/id (:produto/id produto)]
                                                   :produto/categoria
                                                   [:categoria/id (:categoria/id categoria)]]))
                                  []
                                  produtos)]
    (d/transact conn para-transacionar)))


(defn todos-nomes-produtos-categorias [db]
  (d/q '[:find ?produto-nome ?produto-categoria ?categoria-nome
         :keys produto categoria-id categoria
         :where
         [?produto :produto/id ?produto-id]
         [?produto :produto/nome ?produto-nome]
         [?produto :produto/categoria ?produto-categoria]
         [?produto-categoria :categoria/id ?categoria-id]
         [?produto-categoria :categoria/nome ?categoria-nome]] db))
∏
(pprint "Carregado DB")