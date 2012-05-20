<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:header />

<div id="content">
  <div class="content-box">
    <h2>${link.title}</h2>
    <span class="link-info">Category:&nbsp;<em>${link.category}</em></span>
    <span class="link-info">Rating:&nbsp;<em>${link.rating}</em></span>
    <p class="link-info">${link.formattedDate}</p>
    <p class="link-info">Posted By: <a href="<s:url action="showProfile"><s:param name="uid">${link.user.uid}</s:param></s:url>">${link.user.usrName}</a></p>
    <p class="link-info">URL:&nbsp;<a href="${link.linkUrl}">${link.linkUrl}</a></p>
    <p class="link-info">Description:&nbsp;${link.description}</p>
    <p class="comment-info"><s:url id = "url" action = "startComment">
          		<s:param name = "urlid">${link.lid}</s:param>
          		<s:param name = "reply">-1</s:param>
          		<s:param name = "title">Comment on ${link.title}</s:param>
          		</s:url>
          		<s:a href="%{url}">Add Comment</s:a>
    </p>
  </div>
  <s:iterator value="comments" id="comment">
    <div class="content-box">
      <h2>${comment.title}</h2>
      <p class="comment-info">${comment.formattedDate}</p>
      <p class="comment-info">Posted By: <a href="<s:url action="showProfile"><s:param name="uid">${comment.user.uid}</s:param></s:url>">${comment.user.usrName}</a></p>
      <p class="comment-info">${comment.content}</p>
      <p class="comment-info"><s:url id = "url" action = "startComment">
          		<s:param name = "reply">${comment.cid}</s:param>
          		<s:param name = "urlid">${link.lid}</s:param>
          		<s:param name = "title">Reply to : ${comment.title}</s:param>
          		</s:url>
          		<s:a href="%{url}">Reply</s:a></p>
  </div>
  </s:iterator>
</div>

<site:footer />
