(ns repl-runner.utils
  (:import
   (clojure.lang DynamicClassLoader)
   (java.lang.management ManagementFactory)
   (java.util Date))
  (:require
   [medley.core :refer [map-vals filter-vals]]))

(defn up-since
  "Return the date this REPL (Java process) was started."
  []
  (Date. (- (.getTime (Date.)) (.getUptime (ManagementFactory/getRuntimeMXBean)))))
#_(up-since)

(defn check-requiring-resolve []
  (when-not (resolve 'requiring-resolve)
    (throw (ex-info ":dev-repl and repl.clj require at least Clojure 1.10" {:version *clojure-version*}))))

(defn setup-dcl
  "Ensures we have a DynamicClassLoader, in case we want to use
  add-libs from the add-lib3 branch of clojure.tools.deps.alpha (to
  load new libraries at runtime)."
  []
  (try
    (let [cl (.getContextClassLoader (Thread/currentThread))]
      (.setContextClassLoader (Thread/currentThread) (DynamicClassLoader. cl)))
    (catch Throwable t
      (println "Unable to establish a DynamicClassLoader!")
      (println (ex-message t)))))

(defn setup-jedi
  "If Jedi Time is on the classpath, require it
  (so that Java Time  objects will support datafy/nav)."
  []
  (try
    (require 'jedi-time.core)
    (println "Java Time is Datafiable...")
    (catch Throwable _)))

(defmacro when-sym
  "Usage: (when-sym some/thing (some/thing ...))
  Allows for conditional compilation of code that depends on a
  symbol being available (in our case below, a macro)."
  [sym expr]
  (when (resolve sym)
    `~expr))
#_(comment
   (resolve +)
   (resolve plus)
   (when-sym + (println "we can add"))                      ; => we can add
   (when-sym plus (println "we can add"))                   ; => nil
   (macroexpand-1 '(when-sym + (println "we can add")))     ; since + exists it will return form
   (macroexpand-1 '(when-sym plus (println "we can add")))) ; since plus doesn't exist, it will return nil

(defmacro try-it [args]
  `(try
     ~args
     (catch Throwable _#)))
#_(comment
   (macroexpand-1 '(try-it 1))
   (try-it [:a (/ 1 0)])                                    ; => nil
   (try-it [:a :b]))                                        ; => [:a :b]
