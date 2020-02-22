(ns utopia.core
  (:require [utopia.core.rules :as r]))


(defn start []
  (r/initial-game-state))


(defn next [state action]
  (r/run state action))
