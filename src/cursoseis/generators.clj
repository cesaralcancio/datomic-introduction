(ns cursoseis.generators
  (:require [schema-generators.generators :as g]
            [clojure.test.check.generators :as clojure.g]))

(defn double-para-bigdecimal [double-valor]
  (BigDecimal. double-valor))

(def double-finito (clojure.g/double* {:infinite? false :NaN? false}))

(def bigdecimal-generator (clojure.g/fmap double-para-bigdecimal double-finito))

(def leaf-generators {BigDecimal bigdecimal-generator})

(println "Carregado meus generators")
