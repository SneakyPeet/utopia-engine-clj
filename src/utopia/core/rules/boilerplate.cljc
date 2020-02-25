(ns utopia.core.rules.boilerplate)

(defn ->CurrentAction [action]
  {:rule-type :CurrentAction
   :action action})

(defn ->NextAction [action]
  {:rule-type :NextAction
   :action action})

(defn ->Effect [effect]
  {:rule-type :Effect
   :effect effect})

(defn ->StateEntity [entity]
  {:rule-type :StateEntity
   :entity entity})

(defn ->PreviousActions [actions]
  {:rule-type :PreviousActions
   :actions actions})

(defn ->PreviousState [state]
  {:rule-type :PreviousState
   :state state})

(defn ->NextState [state]
  {:rule-type :NextState
   :state state})

(defn ->StateChange [f]
  {:rule-type :StateChange
   :f f})

(defn ->GameError [message]
  {:rule-type :GameError
   :message message})
