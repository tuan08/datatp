
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
  <head>
    <title>Hello Spring Security</title>
    <meta charset="utf-8" />
  </head>

  <body>
    <div th:substituteby="index::logout"></div>
    <h1>Login Error Page</h1>
    <p><a href="/index" th:href="@{/index}">Back to home page</a></p>
  </body>

</html>

