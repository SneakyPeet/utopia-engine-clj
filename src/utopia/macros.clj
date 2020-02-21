(ns utopia.macros)


(defmacro defentity
  "Creates a normal defrecord and an =Record? fn that checks instance? of record"
  [& args]
  (let [dr (cons 'defrecord args)
        record-class (first args)
        match-f-name (symbol (str "=" record-class "?"))
        e (symbol "e")
        defn-args [match-f-name [e] (list instance? record-class e)]
        match-f (cons 'defn defn-args)
        ]
    (list 'do dr match-f)))


(comment
  (macroexpand-1 '(defentity Game [] Ooo))
  )
