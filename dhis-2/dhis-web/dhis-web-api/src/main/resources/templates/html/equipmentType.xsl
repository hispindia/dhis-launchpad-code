<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="d:equipmentType">
    <div class="equipmentType">
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
          <td>Description</td>
          <td> <xsl:value-of select="d:description" /> </td>
        </tr>
        <tr>
          <td>Tracking</td>
          <td> <xsl:value-of select="d:tracking" /> </td>
        </tr>              
       </table>
       
       <xsl:apply-templates select="d:models|d:modelTypes|d:modelTypeAttributes|d:modelTypeAttributeGroups|
       								d:dataElements|d:dataElementGroups|d:dataSets|d:organisationUnits|d:organisationUnitGroups|
       								d:equipments|d:equipmentTypes|d:equipmentStatuss|d:equipmentTypeAttributes|
       								 d:equipmentTypeAttributeGroups|d:equipmentAttributeValues" mode="short"/>           
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

  <xsl:template match="d:equipmentTypes" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Equipment Types</h3>
      <table class="equipmentTypes">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>  
  
  <xsl:template match="d:equipmentTypeAttributes" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Equipment Type Attributes</h3>
      <table class="equipmentTypeAttributes">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>  
 
   <xsl:template match="d:models" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Model</h3>
      <table class="models">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
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

  <xsl:template match="d:organisationUnits" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Organisation Units</h3>
      <table class="organisationUnits">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="d:organisationUnitGroups" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Organisation Unit Groups</h3>
      <table class="organisationUnitGroups">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="d:dataSets" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Data Sets</h3>
      <table class="dataSets">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>  
  </xsl:stylesheet>


