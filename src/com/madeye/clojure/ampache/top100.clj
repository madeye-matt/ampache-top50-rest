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

(defn top-results
  "Default 'top tracks' function"
  [group-fn date-range num-results]
  { :status 200
    :body (json/write-str (adb/top-result date-range group-fn num-results))
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

(defn get-date-range
  "Gets a date range from either the 'period' parameter (last-week last-month or ever) or the 'start' and 'end' parameters"
  [params]
  (case (params "period")
    "last-week"
      (c/get-date-range :last_week)
    "last-month"
      (c/get-date-range :last_month)
    "ever"
      (c/get-date-range :all_time)
    nil
      (c/get-date-range :last_month)
  )
)

(defroutes app*
    (GET "/" request "Welcome!")
    (GET "/song-play" {params :query-params} (top-results adb/group-song (get-date-range params) (get-num-results params)))
    (GET "/album-play" {params :query-params} (top-results adb/group-album (get-date-range params) (get-num-results params)))
    (GET "/artist-play" {params :query-params} (top-results adb/group-artist (get-date-range params) (get-num-results params)))
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
