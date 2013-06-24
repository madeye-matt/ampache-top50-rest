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
(require '[ring.adapter.jetty :as jetty :only (run-jetty)])

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

(defn startserver [port] (jetty/run-jetty #'app {:port port :join? false}))

(defn initialise 
  "Initialisation function - initialises database and starts Jetty server"
  [config-file]
  (def config (c/load-props config-file))
  (def server-port (read-string (config :server-port)))
  (adb/initialise config-file)
  (startserver server-port)
)

(defn -main
  "Main functinn"
  [config-file]
  (initialise config-file)
)
