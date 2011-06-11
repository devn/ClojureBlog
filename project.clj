(defproject blog "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.2.1"]
		 [compojure "0.6.2"]
		 [clj-soy "0.1.0"]
		 [mysql/mysql-connector-java "5.1.6"]
		 [ring "0.3.8"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
		     [lein-ring "0.4.0"]]
  :ring {:handler blog.system.routes/app}
  :resources-path "resources")
