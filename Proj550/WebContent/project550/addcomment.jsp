<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:header />

<div id="content">
  <div class="content-box">
  <s:actionerror/>
	<h2><s:property value ="title"/></h2>
    <s:form action="submitComment" method="POST">
      <s:textfield name="commentTitle" label="Title"/>
      <s:textarea name="content" label="Comment" rows="6" cols="40" />
      <s:hidden name="lid" />
      <s:hidden name="rep" />
      <s:submit value="Post Comment"/>
    </s:form>

  </div>
</div>

<site:footer />