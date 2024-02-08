(ns pl.rynkowski.repl-runner.checks)

(defn check-requiring-resolve []
  (when-not (resolve 'requiring-resolve)
    (throw (ex-info "repl-runner require at least Clojure 1.10" {:version *clojure-version*}))))
