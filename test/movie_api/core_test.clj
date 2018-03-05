(ns movie-api.core-test
  (:require [clojure.test :refer :all]
            [movie-api.core :refer :all]))

(defn add3exclaims [req] (str "test" "!!!"))
(def test-handler (create-200-handler add3exclaims))
(deftest create-handler-test
  (testing "should call the function with `test` and stick it in the body"
    (is (= (:body (test-handler "")) "test!!!"))))