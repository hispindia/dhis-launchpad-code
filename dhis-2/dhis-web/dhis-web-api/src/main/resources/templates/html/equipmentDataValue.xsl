<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="d:equipmentDataValue">
    <div class="equipment">
      <h2> <xsl:value-of select="@name" /> </h2>
      <table>
      	<tr>
          <td>ID</td>
          <td> <xsl:value-of select="@id" /> </td>
        </tr>
        <tr>
          <td>Created</td>
          <td> <xsl:value-of select="@created" /> </td>
        </tr>
        <tr>
          <td>LastUpdated</td>
          <td> <xsl:value-of select="@lastUpdated" /> </td>
        </tr>
        <tr>
          <td>Code</td>
          <td> <xsl:value-of select="@code" /> </td>
        </tr>       
        <tr>
          <td>Equipment</td>
          <td> <xsl:value-of select="d:equipment" /> </td>
        </tr>
        <tr>
          <td>DataElement</td>
          <td> <xsl:value-of select="d:dataElement" /> </td>
        </tr>
        <tr>
          <td>Period</td>
          <td> <xsl:value-of select="d:period" /> </td>
        </tr>
        <tr>
          <td>Value</td>
          <td> <xsl:value-of select="d:value" /> </td>
        </tr>
        <tr>
          <td>StoredBy</td>
          <td> <xsl:value-of select="d:storedBy" /> </td>
        </tr>
        <tr>
          <td>Timestamp</td>
          <td> <xsl:value-of select="d:timestamp" /> </td>
        </tr>
      </table>
    </div>
  </xsl:template>
  
  <xsl:template match="d:equipmentDataValues" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Equipment Data Values</h3>
      <table class="equipmentDataValues">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template> 

  </xsl:stylesheet>
