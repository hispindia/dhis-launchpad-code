package org.hisp.dhis.spis;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.program.Program;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class DefaultProgramCriteriasService
    implements ProgramCriteriasService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ProgramCriteriasStore programCriteriasStore;

    public void setProgramCriteriasStore( ProgramCriteriasStore programCriteriasStore )
    {
        this.programCriteriasStore = programCriteriasStore;
    }

    // -------------------------------------------------------------------------
    // ProgramCriteriasService implementation
    // -------------------------------------------------------------------------  
    
    @Override
    public void save( ProgramCriterias programCriterias )
    {
        // TODO Auto-generated method stub
        programCriteriasStore.save( programCriterias );
    }

    @Override
    public void deleteProgramCriterias( ProgramCriterias programCriterias )
    {
        // TODO Auto-generated method stub
        programCriteriasStore.deleteProgramCriterias( programCriterias );
    }
    
    
    @Override
    public Collection<ProgramCriterias> getProgramCriterias( Program program )
    {
        return programCriteriasStore.getProgramCriterias( program );
    }     
    
    @Override
    public Collection<ProgramCriterias> getProgramCriterias( org.hisp.dhis.spis.Criteria ctr  )
    {
        return programCriteriasStore.getProgramCriterias( ctr );
    }    
    

    @Override
    public List<Program> getPrograms( org.hisp.dhis.spis.Criteria ctr )
    {
        return programCriteriasStore.getPrograms( ctr );
    } 
 
    @Override
    public List<org.hisp.dhis.spis.Criteria> getCriterias( Program program )
    {
        return (List<Criteria>) programCriteriasStore.getCriterias( program );
    }     
    
    // update ProgramCriterias
    @Override
    public void updateProgramCriterias( org.hisp.dhis.spis.Criteria ctr, String[] programArray )
    {
        programCriteriasStore.updateProgramCriterias( ctr, programArray );
    }
    
}