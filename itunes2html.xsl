<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:template match="/">
    <html>
      <body>
        <table border="1" width="100%">
          <thead>
            <tr>
              <th><strong>Name</strong></th>
              <th><strong>Artist</strong></th>
              <th><strong>Album</strong></th>
              <th><strong>Year</strong></th>
              <th><strong>Genre</strong></th>
              <th><strong>Kind</strong></th>
              <th><strong>Size</strong></th>
              <th><strong>Total Time</strong></th>
              <th><strong>Bit Rate</strong></th>
              <th><strong>Sample Rate</strong></th>
            </tr>
          </thead>
          <tbody>
            <xsl:call-template name="records" />
          </tbody>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="records">
    <xsl:for-each select="/*/*/dict[1]/dict">
      <xsl:element name="tr">
        <xsl:call-template name="songs" />
      </xsl:element>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="songs">
    <td>
      <xsl:value-of select="child::*[preceding-sibling::* = 'Name']" />
    </td>
    <td>
      <xsl:value-of select="child::*[preceding-sibling::* = 'Artist']" />
    </td>
    <td>
      <xsl:value-of select="child::*[preceding-sibling::* = 'Album']" />
    </td>
    <td>
      <xsl:value-of select="child::*[preceding-sibling::* = 'Year']" />
    </td>
    <td>
      <xsl:value-of select="child::*[preceding-sibling::* = 'Genre']" />
    </td>
    <td>
      <xsl:value-of select="child::*[preceding-sibling::* = 'Kind']" />
    </td>
    <td>
      <xsl:value-of select="child::*[preceding-sibling::* = 'Size']" />
    </td>
    <td>
      <xsl:value-of select="child::*[preceding-sibling::* = 'Total Time']" />
    </td>
    <td>
      <xsl:value-of select="child::*[preceding-sibling::* = 'Bit Rate']" />
    </td>
    <td>
      <xsl:value-of select="child::*[preceding-sibling::* = 'Sample Rate']" />
    </td>
  </xsl:template>
</xsl:stylesheet>
