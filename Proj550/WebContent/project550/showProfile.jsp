<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="site" tagdir="/WEB-INF/tags/site" %>

<site:header />

<div id="content">
  <div class="content-box">
    <s:actionerror/>
    <h2>${user.usrName}'s Profile</h2>
    <s:if test="#session['user'] != null">
      <p class="user-info">
      <s:if test="#session['user'].uid != user.uid">
        <s:if test="friend">
          <a href="<s:url action="getRss"><s:param name="uid">${user.uid}</s:param></s:url>">Subscribe to RSS</a>
        </s:if>
        <s:else>
          <a href="<s:url action="addFriend"><s:param name="fid">${user.uid}</s:param></s:url>">Add Friend</a>
        </s:else>
      </s:if>
      <s:else>
        <a href="<s:url action="viewFriends"/>">Friends</a>
      <div class = "recommendedlinks">
    		<p><strong>Recommended Links</strong></p>
    		<s:iterator value="recommendedLinks" id="link">
    			<strong><a href="${link.linkUrl}">${link.title}</a></strong><br />
    			<span class = "rec">Posted By: <a href="<s:url action="showProfile"><s:param name="uid">${link.user.uid}</s:param></s:url>">${link.user.usrName}</a></span><br/>
  			</s:iterator>
  			<p><span class = "rec"><a href="<s:url action="moreRecommendations"/>">More....</a></span></p>
  		</div>
      </s:else>
      </p>
    </s:if>
    <p class="user-info">Name:&nbsp;${user.firstName}&nbsp;${user.lastName}</p>
    <p class="user-info">Age:&nbsp;${user.age}<p>
    <p class="user-info">Profession:&nbsp;${user.profession}</p>
    <p class="user-info">Location:&nbsp;${user.country}</p>
  </div>
</div>

<site:footer />