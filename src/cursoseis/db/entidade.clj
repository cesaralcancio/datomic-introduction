(ns cursoseis.db.entidade
  (:use clojure.pprint))

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

(println "Carregado entidade")
