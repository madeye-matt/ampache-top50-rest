(defproject atr "0.1.0"
  :description "A Clojure project for a REST interface to provide song play statistics for an Ampache server (for example, top 10 tracks of the last week or top 100 albums of all time)"
  :url "https://github.com/madeye-matt/ampache-top50-rest"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
      [org.clojure/clojure "1.5.1"]
      [com.madeye.clojure.common/common "0.1.1"] 
      [com.madeye.clojure.ampache/ampachedb "0.1.3"]
  ]
  :main com.madeye.clojure.ampache.top100)
