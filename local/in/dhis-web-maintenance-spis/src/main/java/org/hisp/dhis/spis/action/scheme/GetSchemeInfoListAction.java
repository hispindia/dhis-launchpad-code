package org.hisp.dhis.spis.action.scheme;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.spis.CriteriaValueService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetSchemeInfoListAction extends ActionPagingSupport<Program>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private CriteriaValueService criteriaValueService ;
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private List<Program> programs = new ArrayList<>();

    public List<Program> getPrograms()
    {
        return programs;
    }

    private String key;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }
    
    private List<Integer> programIds =new ArrayList<Integer>();
    
    public List<Integer> getProgramIds()
    {
        return programIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( isNotBlank( key ) )
        {
            this.paging = createPaging( programService.getProgramCountByName( key ) );

            programs = new ArrayList<>( programService.getProgramBetweenByName( key, paging.getStartPos(),
                paging.getPageSize() ) );
        }
        else
        {
            this.paging = createPaging( programService.getProgramCount() );
            
            programs = new ArrayList<>( programService.getProgramsBetween( paging.getStartPos(),
                paging.getPageSize() ) );
        }

        Collections.sort( programs, IdentifiableObjectNameComparator.INSTANCE );
        
        programIds =new ArrayList<Integer>( criteriaValueService.getProgramIds() );
        
        /*
        for ( Integer id : programIds )
        {
            System.out.println( " Program Id  -- " + id ) ;
        }
        */
        
        return SUCCESS;
    }
}
