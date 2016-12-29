package org.hisp.dhis.asha.util;

public class ReportCell
{
    String datatype;
    String service;
    Integer row;
    Integer col;
    private String expression;
    


    public ReportCell()
    {
        
    }
    
    public ReportCell( String datatype, String service, Integer row, Integer col, String expression )
    {
        this.datatype = datatype;
        this.service = service;
        this.row = row;
        this.col = col;
        this.expression = expression;
    }
    
    public String getDatatype()
    {
        return datatype;
    }
    public String getService()
    {
        return service;
    }
    public Integer getRow()
    {
        return row;
    }
    public Integer getCol()
    {
        return col;
    }
    public void setDatatype( String datatype )
    {
        this.datatype = datatype;
    }
    public void setService( String service )
    {
        this.service = service;
    }
    public void setRow( Integer row )
    {
        this.row = row;
    }
    public void setCol( Integer col )
    {
        this.col = col;
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
