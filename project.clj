(defproject atr "0.1.0-SNAPSHOT"
  :description "A Clojure project for a REST interface to provide song play statistics for an Ampache server (for example, top 10 tracks of the last week or top 100 albums of all time)"
  :url "http://madeye.com/atr"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main ampache-top100-rest.core)
