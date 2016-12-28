package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRules;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.sm.impl.SynchManager;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;

public class ShowBusinessRulesFormAction
    implements Action
{

    private InstanceBusinessRulesService instanceBusinessRulesService;

    public void setInstanceBusinessRulesService( InstanceBusinessRulesService instanceBusinessRulesService )
    {
        this.instanceBusinessRulesService = instanceBusinessRulesService;
    }

    private SynchManager synchManager;

    public void setSynchManager( SynchManager synchManager )
    {
        this.synchManager = synchManager;
    }

    private SynchInstanceService synchInstanceService;

    public void setSynchInstanceService( SynchInstanceService synchInstanceService )
    {
        this.synchInstanceService = synchInstanceService;
    }

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    private Integer instanceId;

    public void setInstanceId( Integer instanceId )
    {
        this.instanceId = instanceId;
    }

    private List<UserGroup> approvalUserGroups = new ArrayList<UserGroup>();

    public List<UserGroup> getApprovalUserGroups()
    {
        return approvalUserGroups;
    }

    private List<UserGroup> accUserGroups = new ArrayList<UserGroup>();

    public List<UserGroup> getAccUserGroups()
    {
        return accUserGroups;
    }

    private SynchInstance instance;

    public SynchInstance getInstance()
    {
        return instance;
    }

    private InstanceBusinessRules businessRules;

    public InstanceBusinessRules getBusinessRules()
    {
        return businessRules;
    }

    private String statusMesssage;

    public String getStatusMesssage()
    {
        return statusMesssage;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
    {

        List<UserGroup> userGroups = new ArrayList<UserGroup>();

        instance = synchInstanceService.getInstance( instanceId );

        approvalUserGroups.addAll( userGroupService.getAllUserGroups() );

        String url = instance.getUrl() + "/metadata.xml?assumeTrue=false&userGroups=true";

        try
        {

            MetaData metaData = synchManager.getMetaData( instance, url, null );
            userGroups = metaData.getUserGroups();
            for ( UserGroup userGroup : userGroups )
            {
                System.out.println( userGroup.getUid() + " : " + userGroup.getName() );
            }

            accUserGroups.addAll( userGroups );

            businessRules = instanceBusinessRulesService.getInstanceRulesByInstance( instance );

        }
        catch ( Exception e )
        {
            statusMesssage = "Remote server not available... Please try after sometime";
        }

        return SUCCESS;
    }
}
