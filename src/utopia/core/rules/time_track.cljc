(ns utopia.core.rules.time-track
  #?(:cljs (:require-macros [clara.macros :refer [defrule]]))
  (:require #?(:clj [clara.rules :refer :all]
               :cljs [clara.rules :refer [clear-ns-productions! insert! insert-all! retract!]])
            [clara.rules :refer [clear-ns-productions! insert! insert-all! retract!]]
            [clara.rules.accumulators :as acc]
            [utopia.core.rules.boilerplate :as b]
            [utopia.core.entities :as e]
            [utopia.core.universe :as u]))

(clear-ns-productions!)
