(ns utopia.core.macros)


(defmacro defentity
  "Creates a normal defrecord and an =Record? fn that checks instance? of record"
  [name arg & args]
  (let [get-name-f (concat ['get-name '[this]] [(str name)])
        dr (concat ['defrecord name arg 'Entity get-name-f] args)
        match-f-name (symbol (str "=" name "?"))
        e (symbol "e")
        defn-args [match-f-name [e] (list 'instance? name e)]
        match-f (cons 'defn defn-args)
        ]
    (list 'do dr match-f)))


(comment
  (macroexpand-1 '(defentity Game [a] Ooo))
  )
