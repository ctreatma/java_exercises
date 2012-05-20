<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:header />

<div id="content">
  <h2>Friends</h2>
  <s:actionerror />
  <s:iterator value="friends" id="friend">
    <div class="content-box">
      <span class="friend"><a href="<s:url action="showProfile"><s:param name="uid">${friend.uid}</s:param></s:url>">${friend.usrName}</a>&nbsp;(${friend.firstName}&nbsp;${friend.lastName})</span>
    </div>
  </s:iterator>
</div>

<site:footer />