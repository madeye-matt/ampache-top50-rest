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
; (require '[ring.middleware.params :as params :only (wrap-params)])
(require '[clojure.data.json :as json])

(require '[com.madeye.clojure.common.common :as c])
(require '[com.madeye.clojure.ampache.ampachedb :as adb])

(declare default-top server-port)

(defn reload [] (use :reload-all 'com.madeye.clojure.ampache.top100))

(defn top-tracks
  "Default 'top tracks' function"
  [num-results]
  { :status 200
    :body (json/write-str (adb/top-result (c/get-date-range :last_month) adb/group-song num-results))
  }
)

(defn get-num-results
  "Gets number of results query map (assumes a string) or returns default top"
  [params]
  (if-let [numResults (params "num" )]
    (read-string numResults)
    default-top
  )
)

(defroutes app*
    (GET "/" request "Welcome!")
    (GET "/song-play" {params :query-params} (top-tracks (get-num-results params)))
)

(def app (compojure.handler/api app*))

(defn startserver [port] (jetty/run-jetty #'app {:port port :join? false}))

(defn initialise 
  "Initialisation function - initialises database and starts Jetty server"
  [config-file]
  (def config (c/load-props config-file))
  (def server-port (read-string (config :server-port)))
  (def default-top (read-string (config :default-top)))
  (adb/initialise config-file)
  (startserver server-port)
)

(defn -main
  "Main functinn"
  [config-file]
  (initialise config-file)
)
