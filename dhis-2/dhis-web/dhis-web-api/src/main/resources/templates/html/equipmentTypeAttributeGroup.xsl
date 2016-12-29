<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="d:equipmentTypeAttributeGroup">
    <div class="equipmentTypeAttributeGroup">
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
          <td>Last Updated</td>
          <td> <xsl:value-of select="@lastUpdated" /> </td>
        </tr>
        <tr>
          <td>Code</td>
          <td> <xsl:value-of select="@code" /> </td>
        </tr> 
        <tr>
          <td>Description</td>
          <td> <xsl:value-of select="d:description" /> </td>
        </tr>
        <tr>
          <td>EquipmentType</td>
          <td> <xsl:value-of select="d:equipmentType" /> </td>
        </tr>
        <tr>
          <td>SortOrder</td>
          <td> <xsl:value-of select="d:sortOrder" /> </td>
        </tr> 
      </table>
    </div>
  </xsl:template>
  
  <xsl:template match="d:equipmentTypeAttributeGroups" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Equipment Type Attribute Groups</h3>
      <table class="equipmentTypeAttributeGroups">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template> 

  </xsl:stylesheet>
