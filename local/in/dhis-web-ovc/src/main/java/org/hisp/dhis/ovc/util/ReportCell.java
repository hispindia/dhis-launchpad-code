
package org.hisp.dhis.ovc.util;

public class ReportCell
{
    private String datatype;
    
    private String service;
    
    private int sheetno;
    
    private Integer rowno;
    
    private Integer colno;
    
    private String expression;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    
    public ReportCell()
    {
        
    }
    
    public ReportCell( String datatype, String service, Integer sheetno, Integer rowno, Integer colno, String expression )
    {
        this.datatype = datatype;
        this.service = service;
        this.sheetno = sheetno;
        this.rowno = rowno;
        this.colno = colno;
        this.expression = expression;
    }

    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public String getDatatype()
    {
        return datatype;
    }

    public void setDatatype( String datatype )
    {
        this.datatype = datatype;
    }

    public String getService()
    {
        return service;
    }

    public void setService( String service )
    {
        this.service = service;
    }

    public int getSheetno()
    {
        return sheetno;
    }

    public void setSheetno( int sheetno )
    {
        this.sheetno = sheetno;
    }

    public Integer getRowno()
    {
        return rowno;
    }

    public void setRowno( Integer rowno )
    {
        this.rowno = rowno;
    }

    public Integer getColno()
    {
        return colno;
    }

    public void setColno( Integer colno )
    {
        this.colno = colno;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }
    
}
