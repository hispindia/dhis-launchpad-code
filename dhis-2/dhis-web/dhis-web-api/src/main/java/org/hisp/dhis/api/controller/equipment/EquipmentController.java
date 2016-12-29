package org.hisp.dhis.api.controller.equipment;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.List;

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Brajesh Murari <brajesh.murari@yahoo.com>
 *
 */

@Controller
@RequestMapping(value = EquipmentController.RESOURCE_PATH)
public class EquipmentController 
	extends AbstractCrudController<Equipment>
{
    public static final String RESOURCE_PATH = "/equipments";

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private EquipmentTypeService equipmentTypeService;

    @Override
    protected List<Equipment> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<Equipment> entityList=null;
     
        String OUId = null;

        String EqTypeId = null;
        
        String eqTrackingId = null;
        
        String eqStatus = null;

        if ( options.getOptions().containsKey( "ou" ) && options.getOptions().containsKey( "eqType" ) && options.getOptions().containsKey( "eqTrackId" ) )
	{
            OUId = options.getOptions().get( "ou" ) ;
            EqTypeId =  options.getOptions().get( "eqType" );
            eqTrackingId = options.getOptions().get( "eqTrackId" );
			
            if( eqTrackingId == null || eqTrackingId.trim().equals("") )
            {			
                entityList= (List<Equipment>) equipmentService.getEquipmentList( organisationUnitService.getOrganisationUnit( OUId ), equipmentTypeService.getEquipmentType( EqTypeId ) );
            }
            else
            {
                entityList= (List<Equipment>) equipmentService.getEquipments( organisationUnitService.getOrganisationUnit( OUId ), equipmentTypeService.getEquipmentType( EqTypeId ), eqTrackingId );
            }
	}
        
        else if ( options.getOptions().containsKey( "ou" ) && options.getOptions().containsKey( "eqType" ) )
	{
            OUId = options.getOptions().get( "ou" ) ;
            EqTypeId =  options.getOptions().get( "eqType" ) ;             
            entityList= (List<Equipment>) equipmentService.getEquipmentList( organisationUnitService.getOrganisationUnit( OUId ), equipmentTypeService.getEquipmentType( EqTypeId ) );
	}
        
        else if ( options.getOptions().containsKey( "status" ) )
        {
            eqStatus = options.getOptions().get( "status" ) ;
            entityList= (List<Equipment>) equipmentService.getEquipmentsByStatus( eqStatus );
        }        
 
        else if ( options.getOptions().containsKey( "eqType" ) && options.getOptions().containsKey( "status" ) )
        {
            EqTypeId =  options.getOptions().get( "eqType" ) ; 
            eqStatus = options.getOptions().get( "status" ) ;
            entityList= (List<Equipment>) equipmentService.getEquipmentsByStatus( equipmentTypeService.getEquipmentType( EqTypeId ), eqStatus );
        }         
        
        else if ( options.getOptions().containsKey( "ou" ) )
        {
            OUId = options.getOptions().get( "ou" ) ;
            entityList= (List<Equipment>) equipmentService.getEquipments( organisationUnitService.getOrganisationUnit( OUId ) );                      
        }
        
	else
	{        	
            entityList=(List<Equipment>) equipmentService.getAllEquipment();
        }
        
        return entityList;
    }

}


