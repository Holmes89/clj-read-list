(defproject reading-list "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.758"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs "2.8.110"]
                 [reagent "0.10.0"]
                 [re-frame "0.12.0"]
                 [day8.re-frame/tracing "0.5.5"]
                 [secretary "1.2.3"]
                 [prismatic/schema "1.1.12"]
                 [metosin/compojure-api "2.0.0-alpha31"]
                 [yogthos/config "1.1.7"]
                 [ring/ring-jetty-adapter "1.8.0"]
                 [com.taoensso/carmine "2.19.1"]
                 [environ "1.1.0"]
                 [buddy "2.0.0"]]

  :plugins [[lein-shadow "0.1.7"]
            
            [lein-shell "0.5.0"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]


  :shell {:commands {"open" {:windows ["cmd" "/c" "start"]
                             :macosx  "open"
                             :linux   "xdg-open"}}}

  :shadow-cljs {:nrepl {:port 8777}
                
                :builds {:app {:target :browser
                               :output-dir "resources/public/js/compiled"
                               :asset-path "/js/compiled"
                               :modules {:app {:init-fn reading-list.core/init
                                               :preloads [devtools.preload
                                                          day8.re-frame-10x.preload
                                                          re-frisk.preload]}}
                               :dev {:compiler-options {:closure-defines {re-frame.trace.trace-enabled? true
                                                                          day8.re-frame.tracing.trace-enabled? true}}}
                               :release {:build-options
                                         {:ns-aliases
                                          {day8.re-frame.tracing day8.re-frame.tracing-stubs}}}

                               :devtools {:http-root "resources/public"
                                          :http-port 8280
                                          :http-handler reading-list.core/app
                                          }}}}

  :aliases {"dev"          ["with-profile" "dev" "do"
                            ["shadow" "watch" "app"]]
            "prod"         ["with-profile" "prod" "do"
                            ["shadow" "release" "app"]]
            "build-report" ["with-profile" "prod" "do"
                            ["shadow" "run" "shadow.cljs.build-report" "app" "target/build-report.html"]
                            ["shell" "open" "target/build-report.html"]]
            "karma"        ["with-profile" "prod" "do"
                            ["shadow" "compile" "karma-test"]
                            ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "1.0.0"]
                   [day8.re-frame/re-frame-10x "0.6.4"]
                   [re-frisk "1.3.0"]]
    :source-paths ["dev"]}

   :prod {}
   
   :uberjar {:source-paths ["env/prod/clj"]
             :omit-source  true
             :main         reading-list.server
             :aot          [reading-list.server]
             :uberjar-name "reading-list.jar"
             :prep-tasks   ["compile" ["prod"]]}}

  :prep-tasks [])
