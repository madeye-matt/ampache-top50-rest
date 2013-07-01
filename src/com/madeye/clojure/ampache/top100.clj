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
(require '[clojure.data.json :as json])

(require '[com.madeye.clojure.common.common :as c])
(require '[com.madeye.clojure.ampache.ampachedb :as adb])

(declare default-top server-port server)

(def date-formatter (tfmt/formatter "yyyy-MM-dd HH:mm:ss"))
(def date-parser (tfmt/formatter "yyyy-MM-dd"))

(def ^:const ctx-top-songs "/top-songs")
(def ^:const ctx-top-albums "/top-albums")
(def ^:const ctx-top-artists "/top-artists")
(def ^:const ctx-song-plays "/song-plays")

(def ^:const prm-album-id "album")
(def ^:const prm-artist-id "artist")
(def ^:const prm-song-id "song")
(def ^:const prm-start-time "start")
(def ^:const prm-end-time "end")
(def ^:const prm-period "period")

(defn- reload [] (use :reload-all 'com.madeye.clojure.ampache.top100))

(defn- restart [] ((.stop server) (reload) (.start server)))

(defn- translate-json-values
  "Translates any difficult values in the JSON e.g. date-time"
  [k value]
  (case k
    :start
      (tfmt/unparse date-formatter value)
    :end
      (tfmt/unparse date-formatter value)
    value
  )
)

(defn- json-response-map
  "Returns a response map with the specified status and the body parameter JSONified"
  [status body]
  { :status status
    :headers { "Content-Type" "applciation/json" }
    :body (json/write-str body :value-fn translate-json-values)
  }
)

(defn- get-body
  "Creates a body map based on the results and the filter set"
  [filters result]
  { :filters filters :num-results (count result) :result result }
)

(defn- null-clean-fn
  "Default function for cleaning results for display - just returns the supplied map"
  [m]
  m
)

(defn- global-clean-fn
  "Function for cleaning results for display - should be applied to all types of result map" 
  [m]
  (dissoc m :type)
)

(defn- artist-clean-fn
  "Function for cleaning entries unneccessary for display from the artist result map"
  [m]
  (dissoc (global-clean-fn m) :mbid)
)
 
(defn- album-clean-fn
  "Function for cleaning entries unneccessary for display from the album result map"
  [m]
  (dissoc (global-clean-fn m) :disk :year :mbid)
)
 
(defn- song-clean-fn
  "Function for cleaning entries unneccessary for display from the song result map"
  [m]
  (dissoc (global-clean-fn m) :enabled :rate :year :addition_time :size :bitrate :update_time :played :track :time :mode :file :mbid :catalog)
)

(defn- get-plays-for-album-link 
  "Gets a link that shows the plays for a given album"
  [albumid start end] 
  (format "%s?%s=%d&%s=%s&%s=%s" ctx-top-songs prm-album-id albumid prm-start-time (tfmt/unparse date-parser start) prm-end-time (tfmt/unparse date-parser end) )
)

(defn- add-links
 "Adds any links required for the given result type"
  [filters m]
  (let [t (:type m)]
    ; (pprint m)
    (case t
      :album
      (conj m { :link (get-plays-for-album-link ( :id m ) ( :start filters) (:end filters) ) })
      m
    )
  )
) 
 
(defn- top-results
  "Default 'top tracks' function"
  ([filters group-fn clean-fn num-results]
  (json-response-map 200 (get-body filters (map #(clean-fn (add-links filters %)) (adb/top-result filters group-fn num-results)))))
  ([filters group-fn num-results]
  (top-results filters group-fn global-clean-fn num-results))
)

(defn- song-play-results
  "Function for returning raw song plays in JSON"
  [filters]
  (json-response-map 200 (get-body filters (adb/find-song-listen filters)))
)

(defn- get-num-results
  "Gets number of results query map (assumes a string) or returns default top"
  [params]
  (if-let [numResults (params "num" )]
    (read-string numResults)
    default-top
  )
)

(defn- get-period-filter
  "Gets a date range from either the 'period' parameter (last-week last-month or ever) or the 'start' and 'end' parameters"
  [params filters]
  (case (params prm-period)
    "last-week"
      (conj filters (c/get-date-range :last_week))
    "last-month"
      (conj filters (c/get-date-range :last_month))
    "ever"
      (conj filters (c/get-date-range :all_time))
    nil
      (if-let [startstr (params prm-start-time)]
        (if-let [endstr (params prm-end-time)]
          (conj filters { :start (tfmt/parse date-parser startstr) :end (tfmt/parse date-parser endstr) })
          ; Default to "last month" if no period specified
          (conj filters (c/get-date-range :last_month))
        )
        ; Default to "last month" if no period specified
        (conj filters (c/get-date-range :last_month))
      )
  )
)

(defn- get-artist-filter
  "Adds a filter on the specified artist to the supplied map"
  [params filters]
  (if-let [artistid (params prm-artist-id)]
    (conj filters { :artist (read-string artistid) })
    filters
  )
)

(defn- get-album-filter
  "Adds a filter on the specified album to the supplied map"
  [params filters]
  (if-let [albumid (params prm-album-id)]
    (conj filters { :album (read-string albumid) })
    filters
  )
)

(defn- get-filters
  "Gets the aggregate of all the specified filters from the params map"
  ([params] (get-filters params {}))
  ([params filters] (get-period-filter params (get-artist-filter params (get-album-filter params filters))))
)

(defroutes app*
    (GET "/" request "Welcome!")
    (GET ctx-top-songs {params :query-params} (top-results (get-filters params) adb/group-song (get-num-results params)))
    (GET ctx-top-albums {params :query-params} (top-results (get-filters params) adb/group-album (get-num-results params)))
    (GET ctx-top-artists {params :query-params} (top-results (get-filters params) adb/group-artist (get-num-results params)))
    (GET ctx-song-plays {params :query-params} (song-play-results (get-filters params)))
)

(def app (compojure.handler/api app*))

(defn- startserver [port] (jetty/run-jetty #'app {:port port :join? false}))

(defn- initialise 
  "Initialisation function - initialises database and starts Jetty server"
  [config-file]
  (def config (c/load-props config-file))
  (def server-port (read-string (config :server-port)))
  (def default-top (read-string (config :default-top)))
  (adb/initialise config-file)
  (def server (startserver server-port))
)

(defn -main
  "Main functinn"
  [config-file]
  (initialise config-file)
)
