package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatus;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 * 
 */

public class GetIndicatorAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private InstanceBusinessRulesService instanceBusinessRulesService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private IndicatorService indicatorService;

    private IndicatorSynchStatusService indicatorSynchStatusService;

    // ------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------

    public void setIndicatorSynchStatusService( IndicatorSynchStatusService indicatorSynchStatusService )
    {
        this.indicatorSynchStatusService = indicatorSynchStatusService;
    }

    private int indicatorID;

    public void setIndicatorID( int indicatorID )
    {
        this.indicatorID = indicatorID;
    }

    private Indicator indicatorObject;

    public Indicator getIndicatorObject()
    {
        return indicatorObject;
    }

    public void setIndicatorObject( Indicator indicatorObject )
    {
        this.indicatorObject = indicatorObject;
    }

    Collection<SynchInstance> synchInstances = new ArrayList<SynchInstance>();

    public Collection<SynchInstance> getSynchInstances()
    {
        return synchInstances;
    }

    public void setSynchInstances( Collection<SynchInstance> synchInstances )
    {
        this.synchInstances = synchInstances;
    }

    private Collection<IndicatorSynchStatus> AllIndicators;

    public Collection<IndicatorSynchStatus> getAllIndicators()
    {
        return AllIndicators;
    }

    public void setAllIndicators( Collection<IndicatorSynchStatus> allIndicators )
    {
        AllIndicators = allIndicators;
    }

    Collection<SynchInstance> instancesLeft;

    public Collection<SynchInstance> getInstancesLeft()
    {
        return instancesLeft;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    @Override
    public String execute()
        throws Exception
    {

        // System.out.println("indicator id :"+indicatorID);

        indicatorObject = indicatorService.getIndicator( indicatorID );
        
      /*  System.out.println("numerator "+indicatorObject.getNumerator());
        System.out.println("numerator desc "+indicatorObject.getNumeratorDescription());
        
        System.out.println("denominator "+indicatorObject.getDenominator());
        System.out.println("denominator desc "+indicatorObject.getDenominatorDescription());
        
        System.out.println("description "+indicatorObject.getDescription());
        
        System.out.println("display desc "+indicatorObject.getDisplayDescription());*/

        AllIndicators = new ArrayList<IndicatorSynchStatus>();
        AllIndicators.addAll( indicatorSynchStatusService.getSynchStausByIndicator( indicatorObject ) );

        synchInstances.addAll( instanceBusinessRulesService.getInstancesForApprovalUser( currentUserService
            .getCurrentUser() ) );

        instancesLeft = new ArrayList<SynchInstance>();
        instancesLeft.addAll( synchInstances );

        for ( IndicatorSynchStatus indicatorSynchStatus : AllIndicators )
        {
            instancesLeft.remove( indicatorSynchStatus.getInstance() );
        }

        return SUCCESS;
    }

}
