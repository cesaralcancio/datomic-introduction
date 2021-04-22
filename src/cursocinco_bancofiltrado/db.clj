(ns cursocinco_bancofiltrado.db
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [cursocinco_bancofiltrado.model :as model]
            [schema.core :as s]
            [clojure.set :as cset]
            [datomic.api :as dt]))

(def db-uri "datomic:dev://localhost:4334/hello")
(pprint db-uri)

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
             ; Transacoes
             {
              :db/ident       :tx-data/ip
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
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
(defn dissoc-db-id [entidade]
  (if (map? entidade) (dissoc entidade :db/id) entidade))

(defn datomic-para-entidade [entidades]
  (clojure.walk/prewalk dissoc-db-id entidades))





; TODOS OS PRODUTOS
(defn todos-os-produtos [db]
  (d/q '[:find ?entidade ?valor
         :where [?entidade :produto/slug ?valor]] db))

(defn todos-os-produtos [db]
  (d/q '[:find (pull ?produto [:produto/nome :produto/slug :produto/preco])
         :where [?produto :produto/nome]] db))

(s/defn todos-os-produtos :- [model/Produto] [db]
  (datomic-para-entidade (d/q '[:find [(pull ?produto [* {:produto/categoria [*]}]) ...]
                                :where [?produto :produto/nome]] db)))





; TODAS AS CATEGORIAS
(defn todas-categorias [db]
  (d/q '[:find ?id ?nome
         :keys categoria/id categoria/nome
         :where
         [?seila :categoria/id ?id]
         [?seila :categoria/nome ?nome]] db))

(s/defn todas-categorias :- [model/Categoria] [db]
  (datomic-para-entidade (d/q '[:find [(pull ?dbid [*]) ...]
                                :where
                                [?dbid :categoria/id]] db)))





; TODOS OS PRODUTOS POR....
(defn todos-os-produtos-por-slug-fixo [db]
  (d/q '[:find ?entidade
         :where [?entidade :produto/slug "/computador-novo"]] db))

(defn todos-os-produtos-por-slug [db slug]
  (d/q '[:find ?entidade
         :in $ ?slug
         :where [?entidade :produto/slug ?slug]] db slug))

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






; TODOS OS SLUGS
; se nao for usar pode usar um underscore _
(defn todos-os-slugs [db]
  (d/q '[:find ?slug
         :where [_ :produto/slug ?slug]] db))






; TODOS OS PRECOS
(defn todos-os-precos [db]
  (d/q '[:find ?nome ?preco
         :keys produto/nome produto/preco
         :where
         [?produto :produto/preco ?preco]
         [?produto :produto/nome ?nome]] db))





; UM PRODUTO....
(defn um-produto [db db-id]
  (d/q '[:find (pull ?id [*])
         :in $ ?id
         ] db db-id))

(defn um-produto [db db-id]
  (d/pull db '[*] db-id))

(s/defn um-produto :- (s/maybe model/Produto) [db produto-uuid :- java.util.UUID]
  (let [resultado (d/pull db '[* {:produto/categoria [*]}] [:produto/id produto-uuid])
        produto (datomic-para-entidade resultado)]
    (if (:produto/id produto) produto)
    ))

(s/defn um-produto! :- model/Produto [db produto-uuid :- java.util.UUID]
  (let [produto (um-produto db produto-uuid)]
    (if (nil? produto)
      (throw (ex-info "Nao encontrou a entidade" {:type :errors/not-found :id produto-uuid}))
      produto)))






; ADICIONA...
(s/defn adiciona-produtos!
  ([conn produtos :- [model/Produto]]
   (d/transact conn produtos))
  ([conn produtos :- [model/Produto] ip]
   (let [db-add-ip [:db/add "datomic.tx" :tx-data/ip ip]]
     (d/transact conn (conj produtos db-add-ip))
     )))

(s/defn adiciona-categorias! [conn categorias :- [model/Categoria]]
  (d/transact conn categorias))

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






; ATRIBUI CATEGORIAS
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

; exemplos com forward navigation
(defn todos-produtos-por-categoria-forward [db nome-categoria]
  (d/q '[:find (pull ?produto [* {:produto/categoria [*]}])
         :in $ ?nome-categoria
         :where
         [?categoria :categoria/nome ?nome-categoria]
         [?produto :produto/categoria ?categoria]
         ] db nome-categoria))

; exemplos com backward navigation
(defn todos-produtos-por-categoria-backward [db nome-categoria]
  (d/q '[:find (pull ?categoria [* {:produto/_categoria [*]}])
         :in $ ?nome-categoria
         :where
         [?categoria :categoria/nome ?nome-categoria]
         [?produto :produto/categoria ?categoria]
         ] db nome-categoria))

(defn resumo-dos-produtos [db]
  (d/q '[:find (min ?preco) (max ?preco) (count ?preco)
         :keys minimo maximo total
         :with ?produto
         :where [?produto :produto/preco ?preco]]
       db))

(defn resumo-dos-produtos-por-categoria [db]
  (d/q '[:find ?categoria-nome (min ?preco) (max ?preco) (count ?preco)
         :keys categoria minimo maximo total
         :with ?produto
         :where [?produto :produto/preco ?preco]
         [?produto :produto/categoria ?categoria]
         [?categoria :categoria/nome ?categoria-nome]]
       db))

; com duas queries
(defn todos-os-produtos-maior-preco [db]
  (let [preco-mais-alto (ffirst (d/q '[:find (max ?preco)
                                       :where [?produto :produto/preco ?preco]
                                       ] db))]
    (d/q '[:find (pull ?produto [*])
           :in $ ?preco
           :where [?produto :produto/preco ?preco]]
         db preco-mais-alto)))

; nested query
(defn todos-os-produtos-mais-caros [db]
  (d/q '[:find (pull ?produto [*])
         :where [(q '[:find (max ?preco)
                      :where [_ :produto/preco ?preco]
                      ] $) [[?preco]]]
         [?produto :produto/preco ?preco]] db))

(defn todos-os-produtos-do-ip [db ip]
  (d/q '[:find (pull ?produto [*])
         :in $ ?ip-buscado
         :where [?transacao :tx-data/ip ?ip-buscado]
         [?produto :produto/id ?produto-id ?transacao]

         ] db ip))

(defn cria-dados-de-exemplo!
  [conn]
  (def eletronicos (model/nova-categoria (model/uuid) "Eletronicos"))
  (def esporte (model/nova-categoria (model/uuid) "Esporte"))

  @(adiciona-categorias! conn [eletronicos esporte])
  (todas-categorias (d/db conn))

  (def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.00M 10))
  (def celular (model/novo-produto (model/uuid) "Celular Caro", "/celular", 15000.99M))
  (def celular-barato (model/novo-produto (model/uuid) "Celular Barato", "/celular-barato", 500.00M))
  (def tabuleiro-de-xadrez (model/novo-produto (model/uuid) "Tabuleiro de Xadrez", "/tabuleiro-xadrez", 30M 5))
  (def jogo-online (assoc (model/novo-produto (model/uuid) "Jogo Online", "/jogo-online", 20M) :produto/digital true))

  @(adiciona-produtos! conn [computador celular celular-barato, tabuleiro-de-xadrez, jogo-online] "192.168.0.1")
  (todos-os-produtos (d/db conn))

  ; relacionar produto com categoria
  (atribui-categorias! conn [computador celular celular-barato, tabuleiro-de-xadrez, jogo-online] eletronicos)
  (atribui-categorias! conn [tabuleiro-de-xadrez] esporte))

(def regras
  '[
    [(estoque ?produto ?estoque)
     [?produto :produto/estoque ?estoque]]
    [(estoque ?produto ?estoque)
     [?produto :produto/digital true]
     [(ground 100) ?estoque]]
    [(pode-vender? ?produto ?estoque)
     (estoque ?produto ?estoque)
     [(> ?estoque 0)]]
    [(produto-na-categoria ?produto ?nome-da-categoria)
     [?categoria :categoria/nome ?nome-da-categoria]
     [?produto :produto/nome]                               ; nao precisa desse, porque todo produto com categoria ja eh um produto
     [?produto :produto/categoria ?categoria]]
    ])

; IMPORTANTE the "[]" for the :find is to remove each tuple inside a array AND bring just one record.
; And the "..." is to bring all records because when we add "[]" we are removing the tuple from the array but it is also bringing just one record.
(s/defn todos-os-produtos-vendaveis :- [model/Produto] [db]
  (datomic-para-entidade
    (d/q '[:find [(pull ?produto [* {:produto/categoria [*]}]) ...]
           :in $ %
           :where (pode-vender? ?produto ?estoque)
           ] db regras)
    ))

(s/defn um-produto-vendavel :- (s/maybe model/Produto) [db produto-uuid :- java.util.UUID]
  (let [query '[:find (pull ?produto [* {:produto/categoria [*]}]) .
                :in $ % ?id
                :where [?produto :produto/id ?id]
                (pode-vender? ?produto ?estoque)]
        resultado (d/q query db regras produto-uuid)
        produto (datomic-para-entidade resultado)]
    (if (:produto/id produto) produto)))


(s/defn todos-os-produtos-nas-categorias [db categorias :- [s/Str]]
  (datomic-para-entidade
    (let [query '[:find [(pull ?produto [* {:produto/categoria [*]}]) ...]
                  :in $ % [?nome-da-categoria ...]
                  :where
                  (produto-na-categoria ?produto ?nome-da-categoria)
                  ]
          ]
      (d/q query db regras categorias)))
  )

(s/defn todos-os-produtos-nas-categorias-e-digital [db categorias :- [s/Str] digital? :- s/Bool]
  (datomic-para-entidade
    (let [query '[:find [(pull ?produto [* {:produto/categoria [*]}]) ...]
                  :in $ % [?nome-da-categoria ...] ?digi
                  :where
                  (produto-na-categoria ?produto ?nome-da-categoria)
                  [?produto :produto/digital ?digi]
                  ]
          ]
      (d/q query db regras categorias digital?)))
  )

(s/defn atualiza-preco
  [conn produto-id :- java.util.UUID preco-antigo :- BigDecimal preco-novo :- BigDecimal]
  (d/transact conn [[:db/cas [:produto/id produto-id] :produto/preco preco-antigo preco-novo]]))

(s/defn atualiza-produto!
  [conn antigo :- model/Produto a-atualizar :- model/Produto]
  (let [produto-id (:produto/id antigo)
        atributos (cset/intersection (set (keys antigo)) (set (keys a-atualizar)))
        atributos (disj atributos :produto/id)
        anonymous-func (fn [attr] [:db/cas [:produto/id produto-id] attr (get antigo attr) (get a-atualizar attr)])
        db-cases (map anonymous-func atributos)]
    (d/transact conn db-cases)))

(pprint "Carregado DB!")

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
                       :produto/variacao "id-temporario"}])
    )
  )

(defn total-de-prpdutos
  [db]
  (d/q '[:find [(count ?produto)]
         :where [?produto :produto/nome]] db))

(s/defn remove-produto!
  [conn produto-id :- java.util.UUID]
  (d/transact conn [[:db/retractEntity [:produto/id produto-id]]]))

; nao tem atomicidade
(defn visualizacoes [db produto-id]
  (let [quantidade (d/q '[:find ?visualizacoes .
                          :in $ ?id
                          :where
                          [?produto :produto/id ?id]
                          [?produto :produto/visualizacoes ?visualizacoes]]
                        db produto-id)]
    (or quantidade 0)))

(s/defn visualizacao!
  [conn produto-id :- java.util.UUID]
  (let [valor-atual (visualizacoes (dt/db conn) produto-id)
        novo-valor (inc valor-atual)]
    (d/transact conn [{:produto/id            produto-id
                       :produto/visualizacoes novo-valor
                       }])))
; nao tem atomicidade


; agora com atomicidade
(s/defn visualizacao!
  [conn produto-id :- java.util.UUID]
  (println "Com atomicidade")
  (d/transact conn [[:incrementa-visualizacao produto-id]])
  )