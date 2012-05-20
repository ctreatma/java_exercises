<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
  <head>
    <link type="text/css" rel="stylesheet" href="<s:url value = "/project550/cis550.css"/>" />
    <s:head theme="ajax" />
  </head>
  <body>
    <div id="main">
      <div id="header">
        <h1><a href = "<s:url action="home" />">Project 550</a></h1>
      </div>
      <div id="nav">
        <ul id="main_nav">
          <s:if test="category == null">
            <li class="selected">
          </s:if>
          <s:else>
            <li>
          </s:else>
          <a href="<s:url action="home" />">Home</a></li>
          <li class="sep">|</li>
          <s:if test="category == 'Sports'">
            <li class="selected">
          </s:if>
          <s:else>
            <li>
          </s:else>
          <a href="<s:url action="categories"><s:param name="category">Sports</s:param></s:url>">Sports</a></li>
          <li class="sep">|</li>
          <s:if test="category == 'Entertainment'">
            <li class="selected">
          </s:if>
          <s:else>
            <li>
          </s:else>
          <a href="<s:url action="categories"><s:param name="category">Entertainment</s:param></s:url>">Entertainment</a></li>
          <li class="sep">|</li>
          <s:if test="category == 'Politics'">
            <li class="selected">
          </s:if>
          <s:else>
            <li>
          </s:else>
          <a href="<s:url action="categories"><s:param name="category">Politics</s:param></s:url>">Politics</a></li>
          <li class="sep">|</li>          
          <s:if test="category == 'Tech'">
            <li class="selected">
          </s:if>
          <s:else>
            <li>
          </s:else>
          <a href="<s:url action="categories"><s:param name="category">Tech</s:param></s:url>">Tech</a></li>
          <li class="sep">|</li>
          
          <s:if test="category == 'Science'">
            <li class="selected">
          </s:if>
          <s:else>
            <li>
          </s:else>
          <a href="<s:url action="categories"><s:param name="category">Science</s:param></s:url>">Science</a></li>
          <li class="sep">|</li>
          
          <s:if test="category == 'Business'">
            <li class="selected">
          </s:if>
          <s:else>
            <li>
          </s:else>
          <a href="<s:url action="categories"><s:param name="category">Business</s:param></s:url>">Business</a></li>
          <li class="sep">|</li>
          
          <s:if test="category == 'Health'">
            <li class="selected">
          </s:if>
          <s:else>
            <li>
          </s:else>
          <a href="<s:url action="categories"><s:param name="category">Health</s:param></s:url>">Health</a></li>
        </ul>
        <s:if test="#session['user'] == null">
          <ul id="user_nav">
            <li><a href="<s:url action="doLogin" />">Log In</a></li>
            <li><a href="<s:url action="doReg" />">Register</a></li>
          </ul>
        </s:if>
        <s:else>
          <ul id="user_nav">
            <li><a href = "<s:url action="postLink" />">Post Link</a></li>
            <li><a href = "<s:url action="logout" />">Log out</a></li>
            <li>Welcome,&nbsp;<a href="<s:url action="showProfile"><s:param name="uid"><s:property value="#session['user'].uid"/></s:param></s:url>"><s:property value="#session['user'].firstName"/></a>:&nbsp;</li>
            
          </ul>
        </s:else>
      </div>
      <div id="search">
        <s:form action="search" method="POST" theme="simple">
          <s:textfield name="searchstring"/>
          <s:submit/>
        </s:form>
      </div>