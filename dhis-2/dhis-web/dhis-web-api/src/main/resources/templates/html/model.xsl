<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="d:model">
    <div class="model">
      <h2> <xsl:value-of select="@name" /> </h2>
      <table>
        <tr>
          <td>ID</td>
          <td> <xsl:value-of select="@uid" /> </td>
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
          <td>Name</td>
          <td> <xsl:value-of select="d:name" /> </td>
        </tr> 
         <tr>
          <td>Description</td>
          <td> <xsl:value-of select="d:description" /> </td>
        </tr> 
         <tr>
          <td>Model Type</td>
          <td> <xsl:value-of select="d:modelType" /> </td>
        </tr>    
       </table>
      <xsl:apply-templates select="d:parent|d:children|d:modelTypes|d:modelTypeAttributes|d:modelTypeAttributeGroups|d:dataElements|d:dataElementGroups|d:dataSets|d:organisationUnits|d:equipments|d:equipmentTypes|d:equipmentStatuss|d:equipmentType_Attributes|
	  							d:equipmentTypeAttributes|d:equipmentTypeAttributeGroups|d:equipmentAttributeValues" mode="short"/>           
    </div>
  </xsl:template>
  
  <xsl:template match="d:modelTypes" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Model Type</h3>
      <table class="modelTypes">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template> 
  
 <xsl:template match="d:modelTypeAttributes" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Model Type Attributes</h3>
      <table class="modelTypeAttributes">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template> 
    
  <xsl:template match="d:parent" mode="short">
    <h3>Parent OrganisationUnit</h3>
    <table>
      <xsl:apply-templates select="." mode="row"/>
    </table>
  </xsl:template>

  <xsl:template match="d:children" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Child OrganisationUnits</h3>
      <table>
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="d:organisationUnits" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>OrganisationUnits</h3>
      <table>
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="d:dataSets" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>DataSets</h3>
      <table class="dataSets">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>
  
<xsl:template match="d:equipments" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Equipments</h3>
      <table class="equipments">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="d:equipmentTypes" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Equipment Types</h3>
      <table class="equipmentTypes">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>  
  </xsl:stylesheet>
