(ns movie-api.static
  (:require
    [clojure.tools.logging :as log]))

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
  [appName, _]
  (log/info "Getting the App name")
  appName)