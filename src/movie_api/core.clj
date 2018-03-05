(ns movie-api.core
  (:gen-class)
  (:require
    [ring.adapter.jetty :as ring]
    [ring.logger :as ring-logger]
    [clojure.edn :as edn]
    [clojure.tools.logging :as log]
    [taoensso.carmine :as car]))

(def config-defaults
  {
    :port 3000
    :app "DefaultApp"
    :redis {
            :uri "localhost:6379/"
            }
  })

(def config
    (merge
      config-defaults
      (edn/read-string (slurp "config.edn"))))

(def redis-conn {:pool {} :spec (:redis config)})
(defmacro wcar* [& body] `(car/wcar redis-conn ~@body))

(defn get-r-test [_]
  (or (wcar* (car/get "test")) "empty!"))

(defn set-r-test [_]
  (wcar* (car/setex "test" 10 "value")))

(defn hello-world
  "I don't do a whole lot."
  [_]
  (log/info "Calling Hello World!!")
  (str "Hello, World!"))

(defn whats-my-ip
  "returns the remote address from the request"
  [request]
  (log/info "Checking the IP of the request!")
  (:remote-addr request))

(defn get-app-name
  "returns the application name from the config"
  [_]
  (log/info "Getting the App name")
  (:app config))

(defn create-200-handler
  "wraps a function with a handler that responds with a 200 and the result of the function called with the request"
  [func]
  (fn
    ([request] {
                :status 200
                :body (func request)
                })
    ([request respond raise] (respond {
                                       :status 200
                                       :body (func request)
                                       }))))

(def r-test-handler (create-200-handler get-r-test))
(def r-test-set-handler (create-200-handler set-r-test))
(def hello-handler (create-200-handler hello-world))
(def ip-handler (create-200-handler whats-my-ip))
(def app-name-handler (create-200-handler get-app-name))
(def default-handler hello-handler)

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
