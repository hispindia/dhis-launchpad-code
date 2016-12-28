package org.hisp.dhis.webapi.controller.programcriteria;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.query.Order;
import org.hisp.dhis.spis.CriteriaService;
import org.hisp.dhis.spis.CriteriaValue;
import org.hisp.dhis.spis.CriteriaValueService;
import org.hisp.dhis.spis.ProgramCriteriasService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;

/**
 * @author Mithilesh Kumar Thakur
 */

@Controller
@RequestMapping( value = ProgramCriteriaController.RESOURCE_PATH )
public class ProgramCriteriaController extends AbstractCrudController<Program>
{
    public static final String RESOURCE_PATH = "/programCriterias";
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private TrackedEntityAttributeService trackedEntityAttributeService;
    
    @Autowired
    private CriteriaService criteriaService;
    
    @Autowired
    private CriteriaValueService criteriaValueService;
    
    @Autowired
    private ProgramCriteriasService programCriteriasService;
    
    @Override
    protected List<Program> getEntityList( WebMetaData metaData, WebOptions options, List<String> filters, List<Order> orders )
    {
        //programCriterias?teas=Y57XUbBrJk3,jco3O4Ga7sn&ops==&values=Family of the Community,Uneducated
        
        List<Program> entityList = null;
        
        if ( options.getOptions().containsKey( "teas" ) && options.getOptions().containsKey( "ops" ) && options.getOptions().containsKey( "values" ) )
        {
            List<TrackedEntityAttribute> teaList = new ArrayList<TrackedEntityAttribute>();
            List<String> operator = new ArrayList<String>();
            List<String> validationValue = new ArrayList<String>();
            
            String teaUids = options.getOptions().get("teas");
            if ( teaUids != null && teaUids.length() > 0 )
            {
                String[] teaUidsArray = teaUids.split( "," );
                
                //System.out.println( "teaUids " + teaUids );
                
                for ( String teaUid : teaUidsArray )
                {
                    TrackedEntityAttribute tea = trackedEntityAttributeService.getTrackedEntityAttribute( teaUid );
                    teaList.add( tea );
                }
            }
            
            String ops = options.getOptions().get("ops");
            if ( ops != null && ops.length() > 0 )
            {
                String[] opsArray = ops.split( "," );
                
                //System.out.println( "ops " + ops );
                
                for ( String opr : opsArray )
                {
                    operator.add( opr );
                }
            }
            
            String values = options.getOptions().get("values");
            if ( values != null && values.length() > 0 )
            {
                String[] valuesArray = values.split( "," );
                
                //System.out.println( "values " + values );
                
                for ( String val : valuesArray )
                {
                    validationValue.add( val );
                }
            }
            
            if( teaList.size() > 0 && operator.size() > 0 && validationValue.size() > 0 )
            {
                //System.out.println( " 22222 --- " +  teaList + "--" + operator + "--" + validationValue );
                
            	entityList =Lists.newArrayList( criteriaValueService.getCriteriaValues( teaList, operator, validationValue ) );
               
                
                //System.out.println( "entityList Size" + entityList.size() );
            }
            
            else if( teaList != null && teaList.size() > 0  )
            {
                List<CriteriaValue> CriteriaValues = new ArrayList<CriteriaValue>( criteriaValueService.getCriteriaValues( teaList ) );
                
                for ( CriteriaValue crValue : CriteriaValues )
                {
                    entityList = Lists.newArrayList( programCriteriasService.getPrograms( crValue.getCriteria() ) );
                }
            }
            
            else
            {
                entityList = Lists.newArrayList( programService.getAllPrograms() );
            }
        }
        
        else if ( options.getOptions().containsKey( "teas" ) )
        {
            List<TrackedEntityAttribute> teaList = new ArrayList<TrackedEntityAttribute>();
           
            String teaUids = options.getOptions().get("teas");
            if ( teaUids != null && teaUids.length() > 0 )
            {
                String[] teaUidsArray = teaUids.split( "," );
                
                for ( String teaUid : teaUidsArray )
                {
                    TrackedEntityAttribute tea = trackedEntityAttributeService.getTrackedEntityAttribute( teaUid );
                    teaList.add( tea );
                }
            }
            
            if( teaList != null && teaList.size() > 0  )
            {
                List<CriteriaValue> CriteriaValues = new ArrayList<CriteriaValue>( criteriaValueService.getCriteriaValues( teaList ) );
                
                for ( CriteriaValue crValue : CriteriaValues )
                {
                    entityList = Lists.newArrayList( programCriteriasService.getPrograms( crValue.getCriteria() ) );
                }
            }
            
            else
            {
                entityList = Lists.newArrayList( programService.getAllPrograms() );
            }
        }
        
        else
        {
            entityList = Lists.newArrayList( programService.getAllPrograms() );
        }
        
        return entityList;
    }
}
