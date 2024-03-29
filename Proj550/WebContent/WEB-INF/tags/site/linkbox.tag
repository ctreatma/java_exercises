<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ attribute
    name="link"
    type="com.project550.model.LinkBean"
    required="true"
%>

<div class="content-box">
  <div class="rating">
    <p>${link.rating}</p>
    <a href="rateLink.action?lid=${link.lid}">Rate it!</a>
  </div>
  <span class="posted-by">Posted By: <a href="<s:url action="showProfile"><s:param name="uid">${link.user.uid}</s:param></s:url>">${link.user.usrName}</a></span>
  <h3><a href="<s:url action="showLink"><s:param name="lid">${link.lid}</s:param></s:url>">${link.title}</a></h3>
  <p class="linkbox-date">Posted on:&nbsp;${link.postDateTime}</p>
  <p class="linkbox-desc">${link.description}</p>
  <span>Category:&nbsp;<em>${link.category}</em></span>
</div>