<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>To Do Web Manager</title>
<link rel="stylesheet" href="index.css" />
</head>
<body>
  <div id="rootDiv">
  <h1>To Do List</h1>
  <div id="tasksDiv">
  <table id="taskList">
     <tr>
        <th>Description</th>
        <th>Completed</th>
        <th colspan="2"></th>
     </tr>
  <% Object[][] toDos = (Object[][]) request.getAttribute("toDos"); %>
  <% for (int i = 0; i < toDos.length; ++i) { %>
     <tr>
       <form action="toDoManager.do" method="POST">
          <% Object[] toDo = toDos[i]; %>
          <input name="row" type="hidden" value="<%= i %>" />
          <td><input name="description" value="<%= toDo[0] %>" /></td>
          <% boolean completed = (Boolean) toDo[1]; %>
          <% if (completed) { %>
             <td><input name="completed" type="checkbox" checked /></td>
          <% } else { %>
             <td><input name="completed" type="checkbox" /></td>
          <% } %>
          <td><input name="command" type="submit" value="Update" /></td>
          <td><input name="command" type="submit" value="Remove" /></td>
       </form>
     </tr>
  <% } %>
  </table>
  </div>
  <div id="formsDiv">
  <form id="removeCompleted" action="toDoManager.do" method="POST">
     <h4>Remove all completed tasks:</h4>
     <div class="button">
       <input type="submit" name="command" value="Remove Completed"/>
     </div>
  </form>
  <form id="addNew" action="toDoManager.do" method="POST">
     <h4>Add a new task:</h4>
     <table>
       <tr>
         <td>Description:</td>
         <td><input name="description" value=""/></td>
       </tr>
       <tr>
         <td colspan="2" class="button">
           <input name="command" type="submit" value="Add"/>
         </td>
       </tr>
     </table>
  </form>
  </div>
  </div>
</body>
</html>