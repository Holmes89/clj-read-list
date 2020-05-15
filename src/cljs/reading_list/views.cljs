(ns reading-list.views
  (:require
   [re-frame.core :as re-frame]
   [reading-list.subs :as subs]
   [reading-list.events :as events]
   ))

(defn submit-isbn []
  (let [isbn (.-value (.getElementById js/document "isbn"))]
    (re-frame/dispatch [::events/add-book {:isbn isbn}])))

(defn add-book-input
  []
  [:div.field.has-addons.has-addons-centered
   [:div.control
    [:input#isbn.input {:type "text" :placeholder "ISBN"}]]
   [:div.control
    [:button.button.is-success {:on-click submit-isbn}
     [:span.icon
      [:i.fas.fa-plus]]]]])

(defn read-icon
  [{:keys [read]}]
  (if read
    [:i.fas.fa-bookmark]
    [:i.far.fa-bookmark]))

(defn like-icon
  [{:keys [liked]}]
  (if liked
    [:i.fas.fa-heart]
    [:i.far.fa-heart]))

(defn like-book
  [book]
  (re-frame/dispatch [::events/update-book (update-in book [:liked] not)]))

(defn read-book
  [book]
  (as-> (update-in book [:read] not) b
    (re-frame/dispatch [::events/update-book b])))

(defn book-card
  [{id :id
    title :title
    author :author
    thumbnail :thumbnail
    :as book}]
  [:div.card
   [:div.card-image
    [:figure.image
     [:img {:src thumbnail :style {:width 125 :margin-left "20%"}}]]]
   [:div.card-content
    [:h2 title]
    [:h4 author]]
   [:div.card-footer
    [:a.card-footer-item {:on-click #(read-book book)} (read-icon book) "Read"]
    [:a.card-footer-item {:on-click #(like-book book)} (like-icon book) "Like"]]])

(defn book-grid []
  (let [books (re-frame/subscribe [::subs/books])]
    (fn []
      [:div.columns.is-mobile.is-multiline
       (for [book @books]
         ^{:key (:id book)}
         [:div.column.is-3
          [book-card book]])])))


;; home

(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div.container
     [:section.section
      [:h1.title.has-text-centered (str "Reading List")]
      [:div.container.has-text-centered
       [add-book-input]]]
     [:section.section
      [:div.container
       [book-grid]]]]))



;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [show-panel @active-panel]))
