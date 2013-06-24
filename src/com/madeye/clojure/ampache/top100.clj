(ns com.madeye.clojure.ampache.top100
  (:gen-class))

(require '[clojure.string :as str])
(require '[clojure.xml :as xml])
(require '[clj-time.core :as tm])
(require '[clj-time.local :as tloc])
(require '[clj-time.format :as tfmt])
(require '[com.madeye.clojure.common.common :as c])

(defn reload [] (use :reload-all 'com.madeye.clojure.ampache.top100))

(use '[ring.adapter.jetty :only (run-jetty)])

(defn app
  [{:keys [uri]}]
  {:body (format "You requested %s" uri)})

(def server (run-jetty #'app {:port 8080 :join? false}))

