(ns com.madeye.clojure.ampache.top100-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as tm]
            [com.madeye.clojure.ampache.top100 :refer :all]))

(deftest test-fail
  (testing "A test that is bound to fail"
    (is (= 1 0))
  )
)
