(ns blog.controllers.c-user
  (:use	blog.system.utils)
  (:require [blog.models.m-user :as user]
	    [clj-soy.template :as soy]
	    [clojure.contrib.sql :as sql]))

(defaction login-page []
  (render-action user#login_page
		 {}))

(defaction login [name password]
  (let [blog-user (user/fetch name password)]
    (if (= (count blog-user) 0)
      (redirect-to "/nologin")
      (with-session {:user name}
	(redirect-to "/")))))

(defaction nologin []
  (render-action user#nologin
   		 {}))

(defaction logout []
  (with-session {:user ""}
    (redirect-to "/")))


