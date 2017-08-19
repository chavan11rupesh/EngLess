(ns engless.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [engless.handler :refer :all]
            [engless.core :refer :all]
            ))


(defn before-test [f]
  (-main)
  (f))

(use-fixtures :once before-test)

(deftest test-app
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response)))))

  (testing "check-user-auth"
    (let [response ((app) (request :get "/user"))]
      (is (= 200 (:status response)))))

  (testing "register user"
    (let [response ((app) (request :post "/signup"))]
      (is (= 200 (:status response)))))

  (testing "get all words"
    (let [response ((app) (request :get "/words"))]
      (is (= 200 (:status response)))))

  (testing "returns meaning of word"
    (let [response ((app) (request :get "/dictionary"))]
      (is (= 200 (:status response)))))

  (testing "save word"
    (let [response ((app) (body (request :post "/save-word") {:user "Devashish" :map-word {:word "monitor " :mean "abc" :usage "xyz"}}))]
      (is (= 200 (:status response)))))

  (testing "send email"
    (let [response ((app) (body (request :get "/send-mail") {:email "bhavesh@mindseed.in"  :map-word-data {:from "rupeshbhavesh11@gmail.com" :to "rupesh@mindseed.in"}}))]
      (is (= 200 (:status response)))))

  (testing "display saved words"
    (let [response ((app) (request :get "/saved-word"))]
      (is (= 200 (:status response))))))
