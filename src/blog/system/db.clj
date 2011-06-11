(ns blog.system.db)

(def ^{:doc "The binding contains the database connection settings."}
   *db* {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname "//127.0.0.1:3306/myblog"
         :user "user"
         :password "pass"})
