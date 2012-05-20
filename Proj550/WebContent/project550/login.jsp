<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:header />

<div id="content">
  <div class="content-box">
  <h2>Member Login</h2>
    <s:actionerror/>
    <s:form action="login" method="POST">
      <s:textfield name="username" label="User Name" />
      <s:password name="password" label="Password" />
      <s:submit value="Login"/>
    </s:form>
  </div>
</div>

<site:footer />