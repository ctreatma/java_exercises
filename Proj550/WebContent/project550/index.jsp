<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:header />

<div id="content">
  <s:actionerror/>
  <s:if test="byRating">
    <h2>Most Popular Posts</h2>
    <a class="sort-link" href="<s:url action="home"><s:param name="byRating" value="false" /></s:url>">View Most Recent Posts</a>
  </s:if>
  <s:else>
	<h2>Most Recent Posts</h2>
    <a class="sort-link" href="<s:url action="home"><s:param name="byRating" value="true" /></s:url>">View Most Popular Posts</a>
  </s:else>
  <s:iterator value="links" id="link">
    <site:linkbox link="${link}" />
  </s:iterator>
</div>

<site:footer />
