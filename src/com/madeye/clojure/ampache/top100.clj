(ns com.madeye.clojure.ampache.top100
  (:use [compojure.core :only (GET PUT POST defroutes)])
  (:require (compojure handler route)
            [ring.util.response :as response])
  (:gen-class))

(require '[clojure.string :as str])
(require '[clojure.xml :as xml])
(require '[clj-time.core :as tm])
(require '[clj-time.local :as tloc])
(require '[clj-time.format :as tfmt])

(require '[com.madeye.clojure.common.common :as c])
(require '[com.madeye.clojure.ampache.ampachedb :as adb])

(defn reload [] (use :reload-all 'com.madeye.clojure.ampache.top100))

;(defn app
;  [{:keys [uri]}]
;  {:body (format "You requested %s" uri)})

;(def server (run-jetty #'app {:port 8080 :join? false}))

(defroutes app*
    (GET "/" request "Welcome!")
)

(def app (compojure.handler/api app*))

(defn startserver [] (run-jetty #'app {:port 8080 :join? false}))

