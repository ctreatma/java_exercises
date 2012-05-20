<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:header />

<div id="content">
<div class="content-box">
  <s:actionerror/>
  <h2>Other Recommended Links</h2>
    <s:iterator value="recommendedLinks" id="link">
    	<div class = "rec">
    		<p><strong><a href="${link.linkUrl}">${link.title}</a></strong><br />
    		<span class = "rec">&nbsp;&nbsp;Posted By: <a href="<s:url action="showProfile"><s:param name="uid">${link.user.uid}</s:param></s:url>">${link.user.usrName}</a></span></p>
    	</div>
  	</s:iterator>
  	</div>
</div>

<site:footer />