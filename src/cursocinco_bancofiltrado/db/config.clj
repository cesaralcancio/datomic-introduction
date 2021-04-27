(ns cursocinco-bancofiltrado.db.config
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [cursocinco_bancofiltrado.model :as model]
            [cursocinco-bancofiltrado.db.produto :as db.produto]
            [cursocinco-bancofiltrado.db.categoria :as db.categoria]))

(def db-uri "datomic:dev://localhost:4334/hello")

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
             {
              :db/ident       :produto/estoque
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "Quantidade em estoque"
              }
             {:db/ident       :produto/digital
              :db/valueType   :db.type/boolean
              :db/cardinality :db.cardinality/one
              :db/doc         "Se o produto e digital"}
             {:db/ident       :produto/variacao
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/many
              :db/isComponent true
              :db/doc         "lista de variacao que eu nao sei o que é"}
             {:db/ident       :produto/visualizacoes
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/noHistory   true
              :db/doc         "Quantas vezes o produto foi acessado"
              }
             ; Variacao
             {:db/ident       :variacao/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              }
             {:db/ident       :variacao/nome
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              }
             {:db/ident       :variacao/preco
              :db/valueType   :db.type/bigdec
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
             ; Venda
             {
              :db/ident       :venda/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              }
             {
              :db/ident       :venda/produto
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/one}
             {
              :db/ident       :venda/quantidade
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one}
             {:db/ident       :venda/situacao
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one}
             ; Transacoes
             {
              :db/ident       :tx-data/ip
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              }
             ])

(defn cria-schema! [conn]
  (d/transact conn schema))

(defn cria-dados-de-exemplo!
  [conn]
  (def eletronicos (model/nova-categoria (model/uuid) "Eletronicos"))
  (def esporte (model/nova-categoria (model/uuid) "Esporte"))

  @(db.categoria/adiciona-categorias! conn [eletronicos esporte])
  (db.categoria/todas-categorias (d/db conn))

  (def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.00M 10))
  (def celular (model/novo-produto (model/uuid) "Celular Caro", "/celular", 15000.99M))
  (def celular-barato (model/novo-produto (model/uuid) "Celular Barato", "/celular-barato", 500.00M))
  (def tabuleiro-de-xadrez (model/novo-produto (model/uuid) "Tabuleiro de Xadrez", "/tabuleiro-xadrez", 30M 5))
  (def jogo-online (assoc (model/novo-produto (model/uuid) "Jogo Online", "/jogo-online", 20M) :produto/digital true))

  @(db.produto/adiciona-ou-altera! conn [computador celular celular-barato, tabuleiro-de-xadrez, jogo-online] "192.168.0.1")
  (db.produto/todos-os-produtos (d/db conn))

  ; relacionar produto com categoria
  (db.categoria/atribui-categorias! conn [computador celular celular-barato, tabuleiro-de-xadrez, jogo-online] eletronicos)
  (db.categoria/atribui-categorias! conn [tabuleiro-de-xadrez] esporte))
