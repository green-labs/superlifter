(defproject example "0.0.1-SNAPSHOT"
  :description "An example use of superlifter for lacinia"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service "0.5.7"]
                 [io.pedestal/pedestal.jetty "0.5.7"]
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]
                 [com.walmartlabs/lacinia-pedestal "0.13.0-alpha-1"]
                 [funcool/promesa "11.0.678"]
                 [superlifter "0.1.3-SNAPSHOT"]]
  :min-lein-version "2.0.0"
  :source-paths ["src" "../src"]
  :resource-paths ["config", "resources"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "example.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.7"]
                                  [clj-http "3.10.0"]]}
             :uberjar {:aot [example.server]}}
  :main ^{:skip-aot true} example.server)
