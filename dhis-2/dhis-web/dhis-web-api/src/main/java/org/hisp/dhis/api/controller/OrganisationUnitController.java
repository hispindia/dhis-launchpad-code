package org.hisp.dhis.api.controller;

import org.hisp.dhis.api.utils.IdentifiableObjectParams;
import org.hisp.dhis.api.utils.ObjectPersister;
import org.hisp.dhis.api.utils.WebLinkPopulator;
import org.hisp.dhis.api.view.Jaxb2Utils;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.OrganisationUnits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = OrganisationUnitController.RESOURCE_PATH )
public class OrganisationUnitController
{
    public static final String RESOURCE_PATH = "/organisationUnits";

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private ObjectPersister objectPersister;

    //-------------------------------------------------------------------------------------------------------
    // GET
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getOrganisationUnits( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        OrganisationUnits organisationUnits = new OrganisationUnits();

        if ( params.isPaging() )
        {
            int total = organisationUnitService.getNumberOfOrganisationUnits();

            Pager pager = new Pager( params.getPage(), total );
            organisationUnits.setPager( pager );

            List<OrganisationUnit> organisationUnitList = new ArrayList<OrganisationUnit>(
                organisationUnitService.getOrganisationUnitsBetween( pager.getOffset(), pager.getPageSize() ) );

            organisationUnits.setOrganisationUnits( organisationUnitList );
        }
        else
        {
            organisationUnits.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() ) );
        }

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( organisationUnits );
        }

        model.addAttribute( "model", organisationUnits );

        return "organisationUnits";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getOrganisationUnit( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( organisationUnit );
        }

        model.addAttribute( "model", organisationUnit );

        return "organisationUnit";
    }

    //-------------------------------------------------------------------------------------------------------
    // POST
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_WEBAPI_CREATE')" )
    public void postOrganisationUnitXML( HttpServletResponse response, InputStream input ) throws Exception
    {
        OrganisationUnit organisationUnit = Jaxb2Utils.unmarshal( OrganisationUnit.class, input );
        postOrganisationUnit( organisationUnit, response );
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_WEBAPI_CREATE')" )
    public void postOrganisationUnitJSON( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    public void postOrganisationUnit( OrganisationUnit organisationUnit, HttpServletResponse response )
    {
        if ( organisationUnit == null )
        {
            response.setStatus( HttpServletResponse.SC_NOT_IMPLEMENTED );
        }
        else
        {
            try
            {
                organisationUnit = objectPersister.persistOrganisationUnit( organisationUnit );

                if ( organisationUnit.getUid() == null )
                {
                    response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
                }
                else
                {
                    response.setStatus( HttpServletResponse.SC_CREATED );
                    response.setHeader( "Location", DataElementController.RESOURCE_PATH + "/" + organisationUnit.getUid() );
                }
            } catch ( Exception e )
            {
                response.setStatus( HttpServletResponse.SC_CONFLICT );
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------
    // PUT
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/xml, text/xml"} )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putOrganisationUnitXML( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/json"} )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putOrganisationUnitJSON( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // DELETE
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteOrganisationUnit( @PathVariable( "uid" ) String uid ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.DELETE.toString() );
    }
}
