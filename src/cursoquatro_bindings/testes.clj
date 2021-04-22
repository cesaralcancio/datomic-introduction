(ns cursoquatro-bindings.testes
  (:require
    [clojure.spec.gen.alpha :as gen]
    [common-core.schema :as csc]
    [common-core.schema :as schema]
    [schema.core :as s])
  (:import [java.time LocalDateTime]))

(defn- kebab-keyword?
  "This is a way to silence kibit from complaining about the apparently useless
  function wrap around the `keyword?` predicate"
  [v]
  (keyword? v))

(s/defschema KebabKeyword
  ; This is necessary to ensure the specific implementation of coerce-type for
  ; KebabKeyword will be used instead of the Keyword one.
  (schema/with-generators (s/pred kebab-keyword?)
                          :generator gen/keyword))

(def log-entry-skeleton
  {:type      {:schema KebabKeyword :required true :eg :client-open-conversation :doc "type of the event"}
   :timestamp {:schema LocalDateTime :required true :eg "2015-07-28T16:42:00Z" :doc ""}
   :data      {:schema s/Any :required false :eg "toco problema" :doc "data associated with the event"}})
(s/defschema LogEntry (csc/loose-schema log-entry-skeleton))

(def ContactReason (s/named KebabKeyword "ContactReason"))

(def chat-bookend-skeleton
  {:customer-id       {:schema s/Num :required false}
   :log-entry         {:schema s/Num :required false}
   :as-of             {:schema s/Num :required false}
   :author            {:schema (s/maybe s/Str) :required false}
   :version           {:schema s/Str :required false}
   :reason            {:schema ContactReason :required false}
   :customer-messages {:schema [LogEntry] :required false}
   :legacy-session-id {:schema (s/maybe s/Str) :required false}})
(s/defschema ChatBookend (csc/loose-schema chat-bookend-skeleton))

(def Nome s/Str)
(s/validate Nome "Cesar")
(s/validate Nome 1)

(do ChatBookend)
(s/validate ChatBookend {:customer-id 1})



(defn teste [{:keys [a b] :as seila}]
  (println a b)
  (println seila))

(teste {:bb "hahaha" :a "ihu" :b "aha" :outro "hihihihi"})
