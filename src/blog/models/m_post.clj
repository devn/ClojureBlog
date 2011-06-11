(ns blog.models.m-post
  (:use blog.system.db
	blog.system.utils)
  (:require [clojure.contrib.sql :as sql]))

(defn fetch-list []
  (doall
   (map struct-map->soy
	(doall
	 (sql/with-connection *db*
	   (sql/with-query-results post
	     ["SELECT * FROM post order by id desc"]
	     (doall post)))))))

(defn create [post]
  (sql/with-connection *db*
    (sql/insert-values "post" ["title" "body"]
		       [(:title post)
			(:text post)])))

(defn fetch [id]
  (struct-map->soy
   (sql/with-connection *db*
     (sql/with-query-results post
       [(str "SELECT * FROM post where id = " id)]
       (first (doall post))))))

(defn update [post]
  (sql/with-connection *db*
    (sql/update-values "post" [(str "id=" (:id post))] post)))

(defn delete [id]
  (sql/with-connection *db*
    (sql/delete-rows "post" [(str "id=" id)])))

