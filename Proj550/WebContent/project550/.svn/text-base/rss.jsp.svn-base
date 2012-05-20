<?xml version="1.0"?>
<%@ page contentType="text/xml; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
  <channel>
    <atom:link href="<s:url action="getRss" forceAddSchemeHostAndPort="true" escapeAmp="true"><s:param name="uid">${user.uid}</s:param></s:url>" rel="self" type="application/rss+xml" />
    <title>Friend Feed</title>
    <description>Activity Feed for friend: ${user.firstName} ${user.lastName}</description>
    <link><s:url action="showProfile" forceAddSchemeHostAndPort="true" escapeAmp="true"><s:param name="lid">${user.uid}</s:param></s:url></link>
    <s:iterator value="links" id="link">
      <item>
        <title>${user.firstName} ${user.lastName} Posted A Link</title>
        <description>${user.firstName} ${user.lastName} added a link to Project550, titled "${link.title}."</description>
        <link><s:url action="showLink" forceAddSchemeHostAndPort="true" escapeAmp="true"><s:param name="lid">${link.lid}</s:param></s:url></link>
        <pubDate>${link.formattedDate}</pubDate>
      </item>
    </s:iterator>
    <s:iterator value="comments" id="comment">
      <item>
        <title>${user.firstName} ${user.lastName} Posted A Comment</title>
        <description>${user.firstName} ${user.lastName} commented on a link in Project550.</description>
        <link><s:url action="showLink" forceAddSchemeHostAndPort="true" escapeAmp="true"><s:param name="lid">${comment.lid}</s:param></s:url></link>
        <pubDate>${comment.formattedDate}</pubDate>
      </item>
    </s:iterator>
    <s:iterator value="votes" id="vote">
      <item>
        <title>${user.firstName} ${user.lastName} Rated A Link</title>
        <description>${user.firstName} ${user.lastName} rated a link in Project550, giving it a score of ${vote.rating}.</description>
        <link><s:url action="showLink" forceAddSchemeHostAndPort="true" escapeAmp="true"><s:param name="lid">${vote.lid}</s:param></s:url></link>
        <pubDate>${vote.formattedDate}</pubDate>
      </item>
    </s:iterator>
  </channel>
</rss>