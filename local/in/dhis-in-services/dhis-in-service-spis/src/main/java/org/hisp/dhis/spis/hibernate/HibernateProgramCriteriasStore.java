package org.hisp.dhis.spis.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.spis.ProgramCriterias;
import org.hisp.dhis.spis.ProgramCriteriasStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class HibernateProgramCriteriasStore
    implements ProgramCriteriasStore
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    // -------------------------------------------------------------------------
    // ProgramCriteriasStore implementation
    // -------------------------------------------------------------------------  
    
    @Override
    public void save( ProgramCriterias programCriterias )
    {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate( programCriterias );
        return;
    }
    
    @Override
    public void deleteProgramCriterias( ProgramCriterias programCriterias )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( programCriterias );
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ProgramCriterias> getProgramCriterias( Program program )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramCriterias.class );
        criteria.add( Restrictions.eq( "program", program ) );

        return criteria.list();
    }   
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ProgramCriterias> getProgramCriterias( org.hisp.dhis.spis.Criteria ctr  )
    {
        Session session = sessionFactory.getCurrentSession();

        org.hibernate.Criteria criteria = session.createCriteria( ProgramCriterias.class );
        criteria.add( Restrictions.eq( "criteria", ctr ) );

        return criteria.list();
    }
    
    
    // get Program By Criteria
    public List<Program> getPrograms( org.hisp.dhis.spis.Criteria ctr )
    {
        List<Program> programs = new ArrayList<Program>();
        
        Collection<ProgramCriterias> programCriterias = new ArrayList<ProgramCriterias>( getProgramCriterias(  ctr  ) );
        
        for( ProgramCriterias programCriteria : programCriterias )
        {
            programs.add( programCriteria.getProgram() );
        }
 
        return programs;
    }
    
    // get Criteria  By Program
    public List<org.hisp.dhis.spis.Criteria> getCriterias( Program program )
    {
        List<org.hisp.dhis.spis.Criteria> criterias = new ArrayList<org.hisp.dhis.spis.Criteria>();
        
        Collection<ProgramCriterias> programCriterias = new ArrayList<ProgramCriterias>( getProgramCriterias(  program  ) );
        
        for( ProgramCriterias programCriteria : programCriterias )
        {
            criterias.add( programCriteria.getCriteria() );
        }
 
        return criterias;
    }
    
    // update ProgramCriterias
    @Override
    public void updateProgramCriterias( org.hisp.dhis.spis.Criteria ctr, String[] programArray )
    {
        if( ctr != null )
        {
            Collection<ProgramCriterias> programCriterias = new ArrayList<ProgramCriterias>( getProgramCriterias(  ctr  ) );
            
            if ( programCriterias != null && programCriterias.size() > 0 )
            {
                for( ProgramCriterias programCriteria : programCriterias )
                {
                    deleteProgramCriterias( programCriteria );
                }
            }
            
            if( programArray != null && programArray.length > 0 )
            {
                for ( int i = 0; i < programArray.length; i++ )
                {
                    Program program = programService.getProgram( Integer.parseInt( programArray[i] ) );
                    ProgramCriterias programCriteria = new ProgramCriterias();
                    programCriteria.setCriteria( ctr );
                    programCriteria.setProgram( program );
                    save( programCriteria );
                }
                
            }
            
        }
    }
}