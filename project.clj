(defproject com.madeye.clojure.ampache/ampache-top50 "0.1.2"
  :description "A Clojure project for a REST interface to provide song play statistics for an Ampache server (for example, top 10 tracks of the last week or top 100 albums of all time)"
  :url "https://github.com/madeye-matt/ampache-top50-rest"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
      [org.clojure/clojure "1.5.1"]
      [com.madeye.clojure.common/common "0.1.2"] 
      [com.madeye.clojure.ampache/ampachedb "0.1.8"]
      [ring "1.2.0-RC1"]
      [compojure "1.1.5"]
      [org.clojure/data.json "0.2.2"]
;     [enlive "1.1.1"]
  ]
  :main com.madeye.clojure.ampache.top50)
