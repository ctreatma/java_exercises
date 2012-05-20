<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:header />

<div id="content">
  <s:actionerror/>
  <s:if test="links.length > 0">
    <s:if test="type == null || type == 'keyword'">
      <h2>Search results for "<s:property value="searchstring"/>":</h2>
      <s:if test="byRating">
        <a class="sort-link" href="<s:url action="search"><s:param name="type">${type}</s:param><s:param name="byRating">false</s:param><s:param name="searchstring">${searchstring}</s:param></s:url>">View results by date</a>
      </s:if>
      <s:else>
        <a class="sort-link" href="<s:url action="search"><s:param name="type">${type}</s:param><s:param name="byRating">true</s:param><s:param name="searchstring">${searchstring}</s:param></s:url>">View results by popularity</a>
      </s:else>
    </s:if>
    <s:elseif test="type == 'rating'">
      <h2>Search results for rating from <s:property value="minRating"/> to <s:property value="maxRating"/>:</h2>
      <s:if test="byRating">
        <a class="sort-link" href="<s:url action="search"><s:param name="type">${type}</s:param><s:param name="byRating">false</s:param><s:param name="minRating">${minRating}</s:param><s:param name="maxRating">${maxRating}</s:param></s:url>">View results by date</a>   
      </s:if>
      <s:else>
        <a class="sort-link" href="<s:url action="search"><s:param name="type">${type}</s:param><s:param name="byRating">true</s:param><s:param name="minRating">${minRating}</s:param><s:param name="maxRating">${maxRating}</s:param></s:url>">View results by popularity</a> 
      </s:else>
    </s:elseif>
    <s:else>
      <h2>Search results for date search:</h2>
      <s:if test="byRating">
        <a class="sort-link" href="<s:url action="search"><s:param name="type">${type}</s:param><s:param name="byRating">${!byRating}</s:param><s:param name="minDate">${minDate}</s:param><s:param name="maxDate">${maxDate}</s:param></s:url>">View results by date</a>  
      </s:if>
      <s:else>
        <a class="sort-link" href="<s:url action="search"><s:param name="type">${type}</s:param><s:param name="byRating">${!byRating}</s:param><s:param name="minDate">${minDate}</s:param><s:param name="maxDate">${maxDate}</s:param></s:url>">View results by popularity</a>    
      </s:else>
    </s:else>
    <s:iterator value="links" id="link">
      <site:linkbox link="${link}" />
    </s:iterator>
  </s:if>
  <s:else>
    <h2>No results for your search.  Please try a different <a href="advancedsearch.jsp">search</a>.</h2>
  </s:else>
</div>

<site:footer />