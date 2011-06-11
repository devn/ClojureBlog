(ns blog.controllers.c-comment
  (:use	blog.system.utils)
  (:require [blog.models.m-comment :as comment]
	    [blog.models.m-post :as post]
	    [clj-soy.template :as soy]
	    [clojure.contrib.sql :as sql]))

(defaction new [post_id]
   (render-action comment#new
		  {:post (post/fetch post_id)}))

(defaction create []
   (comment/create params)
   (redirect-to "/"))

(defaction delete [id post_id]
  (check-auth
   (comment/delete id)
   (redirect-to (str "/post/edit/" post_id))))
