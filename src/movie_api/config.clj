(ns movie-api.config
  (:require    [clojure.edn :as edn]))

(def ^:private config-defaults
  {
   :port  3000
   :app   "DefaultApp"
   :redis {
           :uri "localhost:6379/"
           }
   })

(def config
  (merge
    config-defaults
    (edn/read-string (slurp "config.edn"))))