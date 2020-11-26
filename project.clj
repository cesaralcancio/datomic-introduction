(defproject datomic-introduction "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.datomic/datomic-pro "1.0.6222"]]
  :repl-options {:init-ns datomic-introduction.core})

; (require '[datomic.api :as d])
;(def db-uri "datomic:dev://localhost:4334/hello")
;(d/create-database db-uri)

; On Premise installation
; https://docs.datomic.com/on-prem/dev-setup.html