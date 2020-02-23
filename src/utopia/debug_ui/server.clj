(ns utopia.debug-ui.server)

(def index
  "<!DOCTYPE html>
<html>
  <head>
    <meta charset=\"UTF-8\">
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
  </head>
  <body>
    <div id=\"app\"></div>
    <script src=\"/cljs-out/debug-main.js\" type=\"text/javascript\"></script>
    <script>
      window.onload = function () {
        utopia.debug_ui.core.run();
      }
  </body>
</html>")


(defn handler [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body index})
