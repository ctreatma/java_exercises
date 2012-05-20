<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:header />

<div id="content">
  <s:actionerror />
  <div class="content-box">
  <h2>Post A New Link</h2>
    <s:form action="submitLink" method="POST">
      <s:textfield name="urlname" label = "URL" />
      <s:textfield  name="title" label = "Title" />
      <s:select label = "Category" list = "#{'Sports':'Sports','Tech':'Tech','Entertainment':'Entertainment','Politics':'Politics','Business':'Business','Science':'Science','Health':'Health'}" name = "category"></s:select>
      <s:textarea  name="description" label = "Description" rows="6" cols="40"/>
      <s:submit value = "Add Link"/>
      </s:form>
  </div>
</div>

<site:footer />