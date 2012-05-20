<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:ajaxheader />

<div id="content">
  <s:actionerror/>
  <div class="content-box">
    <h2>Search by Keyword</h2>
    <s:form action="search" id="searchKeyword" method="POST">
      <s:hidden name="type" value="keyword" />
      <s:hidden name="byRating" value="true" />
	  <s:textfield name="searchstring" />
	  <s:submit />
    </s:form>
  </div>
  <div class="content-box">
    <h2>Search by Rating</h2>
    <s:form action="search" id="searchRating" method="POST">
      <s:hidden name="type" value="rating" />
      <s:select label="Min Rating" name="minRating" list="#{'0':'0','1':'1','2':'2','3':'3','4':'4','5':'5'}" value="0"></s:select>
      <s:select label="Max Rating" name="maxRating" list="#{'0':'0','1':'1','2':'2','3':'3','4':'4','5':'5'}" value="5"></s:select>
      <s:submit value="Submit"/>
    </s:form>
  </div>
  <div class="content-box">
    <h2>Search by Date</h2>
    <s:form action="search" id="searchDate" method="POST">
      <s:hidden name="type" value="date" />
      <s:datetimepicker label="From Date" name="minDate" />
      <s:datetimepicker label="To Date" name="maxDate" />
      <s:submit value="Submit"/>
    </s:form>
  </div>
</div>

<site:footer />