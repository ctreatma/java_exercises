<?xml version='1.0' encoding='ISO-8859-1'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/documentcollection">
     <html>
       <head>
         <title>War and Peace RSS Feeds</title>
       </head>
       <body>
       <h2>War and Peace RSS Feeds</h2>
       <xsl:for-each select="document">
         <div>
           <h3>
             <a>
               <xsl:attribute name="href">
                 <xsl:value-of select="@location" />
               </xsl:attribute>
               <xsl:value-of select="rss[@version = '2.0']/channel/title" />
             </a>
           </h3>
           <xsl:for-each select="rss[@version = '2.0']/channel/item">
             <xsl:choose>
               <xsl:when test="title[contains(text(),'war')]">
                 <xsl:apply-templates select="." />
               </xsl:when>
               <xsl:when test="title[contains(text(),'peace')]">
                 <xsl:apply-templates select="." />
               </xsl:when>
               <xsl:when test="description[contains(text(),'war')]">
                 <xsl:apply-templates select="." />
               </xsl:when>
               <xsl:when test="description[contains(text(),'peace')]">
                 <xsl:apply-templates select="." />
               </xsl:when>
             </xsl:choose>
           </xsl:for-each>
         </div>
       </xsl:for-each>
       </body>
     </html>
  </xsl:template>
  
  <xsl:template match="item">
     <p><em>Title:</em> <xsl:value-of select="title" /></p>
     <p><em>Description:</em> <xsl:value-of select="description" /></p>
     <xsl:apply-templates select="link" />
     <hr />
     <br />
  </xsl:template>
  
  <xsl:template match="link">
     <p><a>
       <xsl:attribute name="href">
         <xsl:value-of select="." />
       </xsl:attribute>
       <xsl:value-of select="." />
     </a></p>
  </xsl:template>
</xsl:stylesheet>
