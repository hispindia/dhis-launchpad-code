package org.hisp.dhis.sqlview;

import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.Grid;

/**
 * @author Dang Duy Hieu
 * @version $Id SqlViewExpandStore.java July 06, 2010$
 */
public interface SqlViewExpandStore
{
    String ID = SqlViewExpandStore.class.getName();

    // -------------------------------------------------------------------------
    // SqlView expanded
    // -------------------------------------------------------------------------

    List<String> getAllSqlViewNames();

    boolean isViewTableExists( String viewTableName );

    String createView( SqlView sqlViewInstance );

    void dropViewTable( String sqlViewName );

    void setUpDataSqlViewTable( Grid sqlViewGrid, String viewTableName, Map<String, String> criteria );

    String testSqlGrammar( String sql );
}
