<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="d:modelTypeAttribute">
    <div class="modelTypeAttribute">
      <h2> <xsl:value-of select="@name" /> </h2>
      <table>
      	<tr>
          <td>Name</td>
          <td> <xsl:value-of select="@name" /> </td>
        </tr>
        <tr>
          <td>ID</td>
          <td> <xsl:value-of select="@id" /> </td>
        </tr>
        <tr>
          <td>Created</td>
          <td> <xsl:value-of select="@created" /> </td>
        </tr>
        <tr>
          <td>Code</td>
          <td> <xsl:value-of select="@code" /> </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td> <xsl:value-of select="@lastUpdated" /> </td>
        </tr>
        <tr>
          <td>Description</td>
          <td> <xsl:value-of select="d:description" /> </td>
        </tr> 
        <tr>
          <td>ValueType</td>
          <td> <xsl:value-of select="d:valueType" /> </td>
        </tr>
        <tr>
          <td>Mandatory</td>
          <td> <xsl:value-of select="d:mandatory" /> </td>
        </tr>
        <tr>
          <td>Display</td>
          <td> <xsl:value-of select="d:display" /> </td>
        </tr>
      </table>
      <xsl:apply-templates select="d:models|d:modelTypes|d:modelTypeAttributeGroups|d:dataElements|d:dataElementGroups|d:dataSets|d:organisationUnits" mode="short"/>           
    </div>
  </xsl:template>
  
  <xsl:template match="d:modelTypeAttributes" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Model Type Attributes</h3>
      <table class="modelTypeAttributes">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>
  
  </xsl:stylesheet>
