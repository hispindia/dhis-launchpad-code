package org.hisp.dhis.spis.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.spis.CriteriaService;
import org.hisp.dhis.spis.CriteriaValue;
import org.hisp.dhis.spis.CriteriaValueStore;
import org.hisp.dhis.spis.ProgramCriteriasService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class HibernateCriteriaValueStore
    extends HibernateIdentifiableObjectStore<CriteriaValue>
    implements CriteriaValueStore
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private CriteriaService criteriaService;
    
    public void setCriteriaService(CriteriaService criteriaService) {
		this.criteriaService = criteriaService;
	}
    
    private ProgramCriteriasService programCriteriasService;

	public void setProgramCriteriasService(
			ProgramCriteriasService programCriteriasService) {
		this.programCriteriasService = programCriteriasService;
	}

    // -------------------------------------------------------------------------
    // Collections of CriteriasDataValues
    // -------------------------------------------------------------------------
  

	@Override
    public int addCriteriaValue( CriteriaValue criteriaValue )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( criteriaValue );
    }
    
    @Override
    public void updateCriteriaValue( CriteriaValue criteriaValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( criteriaValue );
    }    
    
    @Override
    public void deleteCriteriaValue( CriteriaValue criteriaValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( criteriaValue );
    }
    
    @Override
    public CriteriaValue getCriteriaValueById( int Id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (CriteriaValue) session.get( CriteriaValue.class, Id );
    }    
    
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<CriteriaValue> getAllCriteriaValue()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CriteriaValue.class );

        return criteria.list();
    }
    
 
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CriteriaValue> getCriteriaValues( org.hisp.dhis.spis.Criteria ctr )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CriteriaValue.class );
        criteria.add( Restrictions.eq( "criteria", ctr ) );

        return criteria.list();
    }    

    @Override
    public CriteriaValue getCriteriaValue(TrackedEntityAttribute tea, String operator, String validationValue )
    {
        Session session = sessionFactory.getCurrentSession();
       
        //System.out.println( " tea " + tea );
        //System.out.println( " operator " + operator );
        //System.out.println( " validationValue " + validationValue );
        
        Criteria criteria = session.createCriteria( CriteriaValue.class );
        criteria.add( Restrictions.eq( "tea", tea ) );
        criteria.add( Restrictions.eq( "operator", operator ) );
        criteria.add( Restrictions.eq( "validationValue", validationValue ) );
        
        //System.out.println( " CriteriaValues Size in 1 " + (CriteriaValue) criteria.uniqueResult() );
        
        return (CriteriaValue) criteria.uniqueResult();
    }
    
    @Override
    
    public Set<Program> getProgramByCriteriaValue( TrackedEntityAttribute tea, String operator, String validationValue )
    {
    	Set<Program> programs = new HashSet<Program>();
    	try
        {
            String query = "SELECT criteriaid   FROM criteriavalue  where teaid = "+tea.getId()+" and validationvalue = '"+validationValue+"'"+
            			" union"+" SELECT   distinct criteriaid  FROM criteriavalue   where criteriaid not in (	SELECT criteriaid  FROM criteriavalue  where teaid = "+tea.getId()+" )";
                
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            System.out.println(query);
           System.out.flush();
            while ( rs.next() )
            {
                Integer criteriaId = rs.getInt( 1 );
                org.hisp.dhis.spis.Criteria criteria = (org.hisp.dhis.spis.Criteria) criteriaService.getCriteriaById(criteriaId);
               programs.addAll(programCriteriasService.getPrograms(criteria));
            }
        }
        catch( Exception e )
        {
            System.out.println("In getCriteriaValues Exception :"+ e.getMessage() );
        }
    	System.out.println("program size = == = ="+programs.size());
        return programs;
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CriteriaValue> getCriteriaValues( Collection<TrackedEntityAttribute> teas )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CriteriaValue.class );
        criteria.add( Restrictions.in( "tea", teas ) );

        return criteria.list();
    }
    
    @Override
    public Set<Program> getCriteriaValues( Collection<TrackedEntityAttribute> teas, Collection<String> operators, Collection<String> validationValues )
    {
        /*
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CriteriaValue.class );
        criteria.add( Restrictions.in( "tea", teas ) );
        criteria.add( Restrictions.in( "operator", operators ) );
        criteria.add( Restrictions.in( "validationValue", validationValues ) );
        
        return criteria.list();
        
        */
    	Set<Program> filteredPrograms =new HashSet<Program>();
    	int i=0;
    	boolean flag=true;
    	boolean minParam = false;
    	boolean minDirty = false;
    	boolean maxParam = false;
    	boolean maxDirty = false;
    	Set<Program> programs =new HashSet<>();
        for (TrackedEntityAttribute tea : teas){
        	
        	if (tea.getName().startsWith("Age") || tea.getName().startsWith("Income")){
        		System.out.println("inside -"+tea.getName());
        		Collection<org.hisp.dhis.spis.Criteria> criterias = criteriaService.getAllCriteria();
        		//get criteria
        		for (org.hisp.dhis.spis.Criteria criteria : criterias){
        			boolean hasAgeFlag=false;
        			minDirty=false;
        			maxDirty=false;
        			Collection<CriteriaValue> criteriaValues = getCriteriaValues(criteria);
        				for(CriteriaValue criteriaValue : criteriaValues){
        					if (criteriaValue.getTea().equals(tea)){
        						hasAgeFlag = true;
    							System.out.println(validationValues.toArray()[i].toString()+" "+criteriaValue.getOperator() + " " + criteriaValue.getValidationValue());

        						if (criteriaValue.getOperator().equalsIgnoreCase("<")){
        							minDirty = true;
        							if ( Integer.parseInt(validationValues.toArray()[i].toString()) < Integer.parseInt(criteriaValue.getValidationValue())){
        								minParam = true;
        							}
        						}else if (criteriaValue.getOperator().equalsIgnoreCase(">")){
        							maxDirty = true;
        							if ( Integer.parseInt(validationValues.toArray()[i].toString()) > Integer.parseInt(criteriaValue.getValidationValue())){
        								maxParam=true;
        							}
        						}else if (criteriaValue.getOperator().equalsIgnoreCase("=")){

        							if ( Integer.parseInt(validationValues.toArray()[i].toString()) == Integer.parseInt(criteriaValue.getValidationValue())){
        								programs.addAll(programCriteriasService.getPrograms(criteria));
        							}
        							
        						}
        					}
        					System.out.println("maxdirty="+maxDirty + " maxparam=" + maxParam + " mindirty="+minDirty + "minparam="+minParam);
        					if (minDirty && minParam && !maxDirty || maxDirty && maxParam && !minDirty || minDirty&&minParam&&maxDirty&&maxParam){
								programs.addAll(programCriteriasService.getPrograms(criteria));
        					} // reset min and max 
        					minParam = false; maxParam = false;
        				}
        				if (hasAgeFlag == false){
        					programs.addAll(programCriteriasService.getPrograms(criteria)); 
        				}
        		}
        		
        	}else{
        	programs = getProgramByCriteriaValue(tea,"=",validationValues.toArray()[i].toString());
        	}
        	i++;
        	
        	System.out.println();
        	if (flag){
        		filteredPrograms.addAll(programs);
        	}else{
        		filteredPrograms.retainAll(programs);
        	}
        	flag=false;
        	
        	for (Program program : filteredPrograms){
        		System.out.println("-------------------");
        		System.out.println("filtered = " + program.getDisplayName());
        		System.out.println("-------------------");

        	}
        	programs.clear();
        }
        /* 	
        List<CriteriaValue> criteriaValues = new ArrayList<CriteriaValue>();
        
        for(TrackedEntityAttribute tea : teas )
        {
            for( String op : operators )
            {
                for( String value : validationValues )
                {
                    //System.out.println( " 11111 --- " +  tea + "--" + op + "--" + value );
                    
                    CriteriaValue crtValue = getCriteriaValue(  tea, op, value );
                    
                    if( crtValue != null)
                    {
                        criteriaValues.add( crtValue );
                    }
                    
                }
            }
        }
        
       
        System.out.println( "CriteriaValues Size " + criteriaValues.size() );
        
        for ( CriteriaValue crValue : criteriaValues )
        {
            System.out.println( "crValue--" + crValue.getCriteria().getName() );
            System.out.println( " CR op " + crValue.getOperator()  );
            System.out.println( " CR value " + crValue.getValidationValue() );
        }
        */
        
        return filteredPrograms;
    }    
    

    @Override
    public List<Integer> getProgramIds()
    {
        List<Integer> programIds =new ArrayList<Integer>();
        
        try
        {
            String query = "SELECT programid from program where programid not in ( SELECT programid FROM programattributevalues )";
                        
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while ( rs.next() )
            {
                Integer programId = rs.getInt( 1 );
                
                if( programId != null )
                {
                    programIds.add( programId );
                }
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return programIds;
    }    
    
    
    
    
}