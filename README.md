# repl-runner

## Briefly

Composes the function launching REPL based on classpath content and JVM flags.

## Example

For given aliases in your project or `~/.clojure/deps.edn`:

```clojure
:morse
{:extra-deps {io.github.nubank/morse {:git/tag "v2023.04.30.01" :git/sha "d99b09c"}
              pl.rynkowski/morse-nrepl {:git/url "https://github.com/rynkowsg/morse-nrepl.git"
                                              :git/branch "master"
                                              :git/sha "8ec1c2f14a23557f0664f892dfb78097d6ea61ae"}}}

:morse-run
{:extra-deps {io.github.nubank/morse {:git/tag "v2023.04.30.01" :git/sha "d99b09c"}
              pl.rynkowski/morse-nrepl {:git/url "https://github.com/rynkowsg/morse-nrepl.git"
                                              :git/branch "master"
                                              :git/sha "8ec1c2f14a23557f0664f892dfb78097d6ea61ae"}}
 :jvm-opts ["-Dpl.rynkowski.repl-runner.morse-autostart=true"]}

:repl
{:extra-deps {pl.rynkowski/repl-runner {:git/url "https://github.com/rynkowsg/repl-runner"
                                    :git/branch "master"
                                    :git/sha "be10215c0d6fdb81dfda9c19e99362ca93f3be0d"}}
 :main-opts  ["-m" "pl.rynkowski.repl-runner"]}
```

You could run REPL in a variety of ways:

- `clj -M:repl ` - runs `clojure.main/-main`
- `clj -M:nrepl:repl` - starts nREPL server
- `clj -M:nrepl:morse:repl` - starts nREPL server and applies morse-nrepl middleware
- `clj -M:nrepl:morse-run:repl` - starts nREPL server, applies morse-nrepl middleware and launches morse on process start.
- `clj -M:morse-run:repl` - runs `clojure.main/-main` and launches morse on process start.

## License

Copyright © 2024 Grzegorz Rynkowski

Released under the MIT license.
