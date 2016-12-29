package org.hisp.dhis.ovc.approved.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.util.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class SaveOVCExitFormDataAction implements Action
{
    
    public static final String PREFIX_ATTRIBUTE = "attr";
    
    public static final String OVC_EXIT_ATTRIBUTE = "OVC Exit";
    
    public static final String OVC_EXIT_MESSAGE_ALERT = "OVC Exit Message Alert";//3.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private MessageService messageService;
    
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
     
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    private PatientAttributeOptionService patientAttributeOptionService;
    
    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }
    
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    /*
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    
    // -------------------------------------------------------------------------
    // Input/output getter/setter
    // -------------------------------------------------------------------------

 

    private int ovcId;
    
    public void setOvcId( int ovcId )
    {
        this.ovcId = ovcId;
    }
    
    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }
    
    private PatientAttributeGroup attributeGroup;
    
    public PatientAttributeGroup getAttributeGroup()
    {
        return attributeGroup;
    }
    
    private String attr301;
    
    public void setAttr301( String attr301 )
    {
        this.attr301 = attr301;
    }

    private String attr450;
    
    public void setAttr450( String attr450 )
    {
        this.attr450 = attr450;
    }

    private String cboName;
    
    public void setCboName( String cboName )
    {
        this.cboName = cboName;
    }

    private String ovcSystemId;
    
    public void setOvcSystemId( String ovcSystemId )
    {
        this.ovcSystemId = ovcSystemId;
    }

    private String chvId;
    
    public void setChvId( String chvId )
    {
        this.chvId = chvId;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    
    
    public String execute()
    {
       
        
        //OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        patient = patientService.getPatient( ovcId );
        
        //OrganisationUnit organisationUnit = patient.getOrganisationUnit();
        
        
        // --------------------------------------------------------------------------------------------------------
        // Save Patient Attributes
        // -----------------------------------------------------------------------------------------------------
        
        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;

        
        //Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();
        
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( OVC_EXIT_ATTRIBUTE );
        
        Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();
        
        //Collection<PatientAttribute> attributes = attributeGroup.getAttributes();
        
        

        //List<PatientAttributeValue> valuesForSave = new ArrayList<PatientAttributeValue>();
       // List<PatientAttributeValue> valuesForUpdate = new ArrayList<PatientAttributeValue>();
        //Collection<PatientAttributeValue> valuesForDelete = null;

        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            //patient.getAttributes().clear();
            //valuesForDelete = patientAttributeValueService.getPatientAttributeValues( patient );

            for ( PatientAttribute attribute : attributes )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    attributeValue = patientAttributeValueService.getPatientAttributeValue( patient, attribute );

                    if ( !patient.getAttributes().contains( attribute ) )
                    {
                        patient.getAttributes().add( attribute );
                    }

                    if ( attributeValue == null )
                    {
                        attributeValue = new PatientAttributeValue();
                        attributeValue.setPatient( patient );
                        attributeValue.setPatientAttribute( attribute );
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }
                        
                        patientAttributeValueService.savePatientAttributeValue( attributeValue );
                        
                        //valuesForSave.add( attributeValue );
                    }
                    else
                    {
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }
                        
                        patientAttributeValueService.updatePatientAttributeValue( attributeValue );
                        
                        //valuesForUpdate.add( attributeValue );
                        //valuesForDelete.remove( attributeValue );
                    }
                }
            }
        }

       // patientService.updatePatient( patient, null, null, valuesForSave, valuesForUpdate, valuesForDelete );
       
        
        String currentUser = currentUserService.getCurrentUsername();
        
        
        // Sending message regarding Exit Exit Request Received to County Users
        if( attr301.equalsIgnoreCase( "469" ))
        {
            PatientAttributeOption regionForRejectOption = patientAttributeOptionService.get( NumberUtils.toInt( attr450, 0 ) );
            
            /*
            System.out.println( " currentUser " + currentUser  );
            System.out.println( " attr301 " + attr301  );
            
            System.out.println( " attr450 " + regionForRejectoption.getName()  );
            
            System.out.println( " cboName " + cboName  );
            System.out.println( " ovcSystemId " + ovcSystemId  );
            System.out.println( " chvId " + chvId  );
            */
            
            Set<User> users = new HashSet<User>();
            Constant msgAlertConstant = constantService.getConstantByName( OVC_EXIT_MESSAGE_ALERT );
            
            List<OrganisationUnit> orgUnitBranchList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitBranch( patient.getOrganisationUnit().getId() ) );
        
            for ( OrganisationUnit unit :  orgUnitBranchList )
            {   
               
                //System.out.println( " unit Level " +  unit.getOrganisationUnitLevel()  );
                // System.out.println( " unit Level " + organisationUnitService.getOrganisationUnitLevel( unit.getId() ).getLevel()  );
               
                if( (int) msgAlertConstant.getValue() == unit.getOrganisationUnitLevel() )
                {
                    users.addAll( unit.getUsers() );
                    break;
                }
                
                //System.out.println( " unit Name : " +  unit.getName() + " -- Unit Id : " + unit.getId() ); 
            }
            
            //System.out.println( " Size of Users : " + users.size() ); 
            
            Set<User> recipientUsers = new HashSet<User>();
            
            //List<User> recipientUsers = new ArrayList<User>();
            for ( User user :  users )
            {
                for( String authority : user.getUserCredentials().getAllAuthorities() )
                {
                    if( authority.equalsIgnoreCase( "F_OVC_EXIT_MESSAGE" ))
                    {
                        recipientUsers.add( user );
                    }
                }
            }
            
            //System.out.println( " Size of Recipient Users : " + recipientUsers.size() );
            
            String metaData = MessageService.META_USER_AGENT +
            ServletActionContext.getRequest().getHeader( ContextUtils.HEADER_USER_AGENT );
            
            String subject = "Exit Request for OVC ID " + ovcSystemId;
           
            
            //"<font size=\"3\" ><b>Exit Request Recieved</b></font>";
            
            //String subText = "<font size=3><strong>"+ "Exit Request Recieved "+ "</strong></font>";
            
            //System.out.println( " Sub Text : " +  subText );
            
            //String subText = "<font size=\"3\" ><b>Exit Request Recieved</b></font>";
            
            String text = "Exit Request Received " +  "\n"  +  "\n" + "OVC Name: " + patient.getFullName() + "\n" + "OVC ID: " + ovcSystemId + "\n" + "CBO: " + cboName  + "\n" + "CHV: " + chvId
                          + "\n" + "Requested By: " + currentUser + "\n" + "Reason for Rejection: " + regionForRejectOption.getName();
                    
            messageService.sendMessage( subject, text, metaData, recipientUsers );
            /*
            System.out.println( " Subject : " +  subject );
            System.out.println( " text : " +  text );
            */
        }
        
        // Sending message regarding Exit Request Rejected to CBO Leader
        else if( attr301.equalsIgnoreCase( "303" ) )
        {
            Set<User> users = new HashSet<User>();
            
            users.addAll( patient.getOrganisationUnit().getUsers() );
            
            Set<User> recipientUsers = new HashSet<User>();
            for ( User user :  users )
            {
                for( String authority : user.getUserCredentials().getAllAuthorities() )
                {
                    if( authority.equalsIgnoreCase( "F_OVC_EXIT_REJECT_MESSAGE" ))
                    {
                        recipientUsers.add( user );
                    }
                }
            }
            
            String metaData = MessageService.META_USER_AGENT +
            ServletActionContext.getRequest().getHeader( ContextUtils.HEADER_USER_AGENT );
            
            String subject = "Exit Request Rejected of OVC ID  " + ovcSystemId;
            
            String text = "Exit Request Rejected- " +  "\n"  +  "\n" + "OVC Name: " + patient.getFullName() + "\n" + "OVC ID: " + ovcSystemId + "\n" + "CHV: " + chvId + "\n"  + "CBO: " + cboName  + "\n" 
                          + "\n" + "Requested By: " + currentUser;
      
            messageService.sendMessage( subject, text, metaData, recipientUsers );
            
        }
        
        
        /*
        System.out.println( " Size of Users "   + users.size());
        
        Set<User> tempUsers = new HashSet<User>();
        
        for ( User user :  users )
        {
           
            for( String authority : user.getUserCredentials().getAllAuthorities() )
            {
                if( authority.equalsIgnoreCase( "F_OVC_EXIT_MESSAGE" ))
                {
                    tempUsers.add( user );
                }
            }
            
            
        }
        
        messageService.sendMessage( subject, text, metaData, users );
        
        */
  
        
        return SUCCESS;
    }

}
