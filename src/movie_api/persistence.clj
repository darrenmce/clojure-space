(ns movie-api.persistence
  (:require [taoensso.carmine :as car]))

(defn get-r-test [conn, _]
  (or (car/wcar conn (car/get "test")) "empty!"))

(defn set-r-test [conn, _]
  (car/wcar conn (car/setex "test" 10 "value")))





