# repl-runner

## Briefly

Composes the function launching REPL based on classpath content and JVM flags.

## Example

For given aliases in your project or `~/.clojure/deps.edn`:

```clojure
:morse
{:extra-deps {io.github.nubank/morse {:git/tag "v2023.04.30.01" :git/sha "d99b09c"}
              io.github.rynkowsg/morse-nrepl {:git/url "https://github.com/rynkowsg/morse-nrepl.git"
                                              :git/branch "master"
                                              :git/sha "05bfb6fd2979c6f83a701d786b830930c051bf93"}}}

:morse-run
{:extra-deps {io.github.nubank/morse {:git/tag "v2023.04.30.01" :git/sha "d99b09c"}
              io.github.rynkowsg/morse-nrepl {:git/url "https://github.com/rynkowsg/morse-nrepl.git"
                                              :git/branch "master"
                                              :git/sha "05bfb6fd2979c6f83a701d786b830930c051bf93"}}
 :jvm-opts ["-Dpl.rynkowski.repl-runner.morse-autostart=true"]}

:repl
{:extra-deps {rynkowsg/repl-runner {:git/url "https://github.com/rynkowsg/repl-runner"
                                    :git/branch "master"
                                    :git/sha "43baf4df917725fc9e7b0b4855e559d73f1ac62d"}}

;; this ensures we have a DynamicClassLoader, which is needed for
;; add-lib to work, if we're starting other processes via aliases
;; such as a socket REPL or Cognitect's REBL etc
 :main-opts  ["-e" "(->>(Thread/currentThread)(.getContextClassLoader)(clojure.lang.DynamicClassLoader.)(.setContextClassLoader,(Thread/currentThread)))"
              "-m" "pl.rynkowski.repl-runner"]}
```

You could call repl in a variety of ways:


`clj -M:repl ` - runs `clojure.main/-main`

`clj -M:nrepl:repl` - starts nREPL server

`clj -M:nrepl:morse:repl` - starts nREPL server and applies morse-nrepl middleware

`clj -M:nrepl:morse-run:repl` - starts nREPL server, applies morse-nrepl middleware and launches morse on process start.

`clj -M:morse-run:repl` - runs `clojure.main/-main` and launches morse on process start.

## License

Copyright © 2024 Grzegorz Rynkowski

Released under the MIT license.
