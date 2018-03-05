(ns movie-api.core
  (:gen-class)
  (:use
    [movie-api.static]
    [movie-api.util]
    [movie-api.config])
  (:require
    [ring.adapter.jetty :as ring]
    [ring.logger :as ring-logger]
    [movie-api.persistence :as store]))

;redis
(def redis-conn {:pool {} :spec (:redis config)})
(def get-r-test (partial store/get-r-test redis-conn))
(def set-r-test (partial store/set-r-test redis-conn))

;handlers
(def r-test-handler (create-200-handler get-r-test))
(def r-test-set-handler (create-200-handler set-r-test))
(def hello-handler (create-200-handler hello-world))
(def ip-handler (create-200-handler whats-my-ip))
(def app-name-handler (create-200-handler (partial get-app-name (:app config))))
(def default-handler hello-handler)

;routes
(def get-handler-by-uri
  #(condp = %
     "/hello" hello-handler
     "/ip" ip-handler
     "/app" app-name-handler
     "/r" r-test-handler
     "/rset" r-test-set-handler
     default-handler))

(defn router
  ([request] ((get-handler-by-uri (:uri request)) request))
  ([request respond raise] (respond (router request))))

(def app (->
           router
           (#(ring-logger/wrap-with-logger %1 {:printer :no-color}))))

(defn -main [] (ring/run-jetty app {:port (:port config)}))
