<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:header />

<div id="content">
  <div class="content-box">
    <s:actionerror/>
    <h2>Rating&nbsp;for:&nbsp;&nbsp;${link.title}</h2>
    <s:form action="submitRating" method="POST">
      <s:hidden name="lid" value="%{link.lid}" />
      <s:select name="rating" list="#{'0':'0','1':'1','2':'2','3':'3','4':'4','5':'5'}"></s:select>
      <s:submit value="Submit"/>
    </s:form>
  </div>
</div>

<site:footer />