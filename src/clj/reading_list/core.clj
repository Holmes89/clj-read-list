(ns reading-list.core
  (:use ring.util.response)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.api.sweet :refer :all]
            [compojure.route :as route]
            [reading-list.book :refer [book-routes]])
  (:gen-class))


(defroutes root-routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (api
    book-routes
    root-routes))

(defn -main
  [& args]
  (run-jetty app {:port 3000}))
