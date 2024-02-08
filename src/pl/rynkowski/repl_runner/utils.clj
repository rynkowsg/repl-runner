(ns pl.rynkowski.repl-runner.utils)

(defmacro try-it [args]
  `(try
     ~args
     (catch Throwable _#)))
#_(comment
   (macroexpand-1 '(try-it 1))
   (try-it [:a (/ 1 0)]) ; => nil
   (try-it [:a :b])) ; => [:a :b]

(defn require-fn
  ([sym]
   (require-fn sym {:throw-on-fail? true}))
  ([sym {:keys [throw-on-fail? error-msg] :as _opts}]
   (try
     (requiring-resolve sym)
     (catch Exception _
       (let [error-msg' (or error-msg (c/on-grey (c/yellow (str "Symbol '" sym " can't be find in classpath"))))]
         (if throw-on-fail?
           (throw error-msg')
           (fn [] (println error-msg'))))))))
