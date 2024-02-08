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
  ([sym {:keys [throw-on-fail? failure-msg] :as _opts}]
   (try
     (let [f (requiring-resolve sym)]
       (when (nil? f) (throw (ex-info "Could not find required function" {:symbol sym})))
       f)
     (catch Exception e
       (let [failure-msg' (or failure-msg (str "Symbol '" sym " can't be found in classpath."))]
         (if throw-on-fail?
           (throw (ex-info failure-msg' {:origin e}))
           (fn [] (println failure-msg'))))))))
#_ (require-fn 'pl.rynkowski.repl-runner.utils/require-fn)
#_ (require-fn 'pl.rynkowski.repl-rnner.utils/dfd)
#_ ((require-fn 'pl.rynkowski.repl-rnner.utils/dfd {:throw-on-fail? false}))
