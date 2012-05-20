<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<form action="">
<table id="taskList">
  <tr>
    <th>Description</th>
    <th>Completed</th>
    <th colspan="2"></th>
  </tr>
  <% Object[][] toDos = (Object[][]) request.getAttribute("toDos"); %>
  <% for (int i = 0; i < toDos.length; ++i) { %>
  <tr id="row<%= i %>">
    <% Object[] toDo = toDos[i]; %>
    <td><input name="description<%= i %>" value="<%= toDo[0] %>" /></td>
    <% boolean completed = (Boolean) toDo[1]; %>
    <% if (completed) { %>
      <td><input name="completed<%= i %>" type="checkbox" checked /></td>
    <% } else { %>
      <td><input name="completed<%= i %>" type="checkbox" /></td>
    <% } %>
    <td><input name="dummy<%= i %>" type="submit" onclick="return sendTask(this);" value="Update" /></td>
    <td><input name="dummy<%= i %>" type="submit" onclick="return sendTask(this);" value="Remove" /></td>
  </tr>
  <% } %>
</table>
</form>