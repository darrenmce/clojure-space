(ns movie-api.util)

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