(ns utopia.debug-ui.server)

(def index
  "<!DOCTYPE html>
<html>
  <head>
    <meta charset=\"UTF-8\">
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
    <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css\">
  </head>
  <body>
    <section class=\"section\" id=\"app\"></section>
    <script src=\"/cljs-out/debug-main.js\" type=\"text/javascript\"></script>
  </body>
</html>")


(defn handler [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body index})
