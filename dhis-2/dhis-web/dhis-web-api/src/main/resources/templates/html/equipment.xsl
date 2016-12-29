<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="d:equipment">
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
          <td>Last Updated</td>
          <td> <xsl:value-of select="@lastUpdated" /> </td>
        </tr>
        <tr>
          <td>Code</td>
          <td> <xsl:value-of select="d:code" /> </td>
        </tr>
        <tr>
          <td>Registration Date</td>
          <td> <xsl:value-of select="d:registrationDate" /> </td>
        </tr>
        <tr>
          <td>Tracking Id</td>
          <td> <xsl:value-of select="d:equipmentTrackingID" /> </td>
        </tr>
        
      </table>
      
	<h3>OrganisationUnit</h3>
	<table>
		<tr>
	 	<td><xsl:apply-templates select="d:organisationUnit" mode="row"/></td>                
		</tr>
    </table>
	  
	      <h3>Equipment Type</h3>
	  <table>
		<tr>
	 	<td><xsl:apply-templates select="d:equipmentType" mode="row"/> </td>              
		</tr>
    </table>
    </div>
  </xsl:template>
  
  <xsl:template match="d:equipments" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Equipments</h3>
      <table class="equipments">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>


</xsl:stylesheet>





