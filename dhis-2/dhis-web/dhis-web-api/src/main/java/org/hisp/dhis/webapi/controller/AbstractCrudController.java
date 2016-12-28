package org.hisp.dhis.webapi.controller;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.hisp.dhis.acl.Access;
import org.hisp.dhis.acl.AclService;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dxf2.fieldfilter.FieldFilterService;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.metadata.ImportService;
import org.hisp.dhis.dxf2.metadata.ImportTypeSummary;
import org.hisp.dhis.dxf2.objectfilter.ObjectFilterService;
import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.hibernate.exception.CreateAccessDeniedException;
import org.hisp.dhis.hibernate.exception.DeleteAccessDeniedException;
import org.hisp.dhis.hibernate.exception.UpdateAccessDeniedException;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.node.config.InclusionStrategy;
import org.hisp.dhis.node.types.CollectionNode;
import org.hisp.dhis.node.types.ComplexNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.controller.exception.NotFoundException;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.service.LinkService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractCrudController<T extends IdentifiableObject>
{
    //--------------------------------------------------------------------------
    // Dependencies
    //--------------------------------------------------------------------------

    @Autowired
    protected IdentifiableObjectManager manager;

    @Autowired
    protected CurrentUserService currentUserService;

    @Autowired
    protected ObjectFilterService objectFilterService;

    @Autowired
    protected FieldFilterService fieldFilterService;

    @Autowired
    protected AclService aclService;

    @Autowired
    protected SchemaService schemaService;

    @Autowired
    protected LinkService linkService;

    @Autowired
    protected RenderService renderService;

    @Autowired
    protected ImportService importService;

    @Autowired
    protected ContextService contextService;

    //--------------------------------------------------------------------------
    // GET
    //--------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody RootNode getObjectList(
        @RequestParam Map<String, String> parameters, HttpServletResponse response, HttpServletRequest request )
    {
        List<String> fields = Lists.newArrayList( contextService.getParameterValues( "fields" ) );
        List<String> filters = Lists.newArrayList( contextService.getParameterValues( "filter" ) );

        WebOptions options = new WebOptions( parameters );
        WebMetaData metaData = new WebMetaData();

        if ( fields.isEmpty() )
        {
            fields.add( ":identifiable" );
        }

        boolean hasPaging = options.hasPaging();

        List<T> entityList;

        if ( filters.isEmpty() || DataElementOperand.class.isAssignableFrom( getEntityClass() ))
        {
            entityList = getEntityList( metaData, options );
        }
        else
        {
            Iterator<String> iterator = filters.iterator();
            String name = null;

            while ( iterator.hasNext() )
            {
                String filter = iterator.next();

                if ( filter.startsWith( "name:like:" ) )
                {
                    name = filter.substring( "name:like:".length() );
                    iterator.remove();
                    break;
                }
            }

            if ( name != null )
            {
                int count = manager.getCountByName( getEntityClass(), name );

                Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
                metaData.setPager( pager );

                entityList = Lists.newArrayList( manager.getBetweenByName( getEntityClass(), name, pager.getOffset(), pager.getPageSize() ) );
            }
            else
            {
                // get full list if we are using filters
                if ( !filters.isEmpty() )
                {
                    options.getOptions().put( "links", "false" );

                    if ( options.hasPaging() )
                    {
                        hasPaging = true;
                        options.getOptions().put( "paging", "false" );
                    }
                }

                entityList = getEntityList( metaData, options );
            }
        }

        Pager pager = metaData.getPager();

        // enable object filter
        if ( !filters.isEmpty() )
        {
            entityList = objectFilterService.filter( entityList, filters );

            if ( hasPaging )
            {
                pager = new Pager( options.getPage(), entityList.size(), options.getPageSize() );
                entityList = PagerUtils.pageCollection( entityList, pager );
            }
        }

        postProcessEntities( entityList );
        postProcessEntities( entityList, options, parameters );

        if ( fields.contains( "access" ) )
        {
            options.getOptions().put( "viewClass", "sharing" );
        }

        handleLinksAndAccess( options, entityList, false );

        linkService.generatePagerLinks( pager, getEntityClass() );

        RootNode rootNode = new RootNode( "metadata" );
        rootNode.setDefaultNamespace( DxfNamespaces.DXF_2_0 );
        rootNode.setNamespace( DxfNamespaces.DXF_2_0 );

        rootNode.getConfig().setInclusionStrategy( getInclusionStrategy( parameters.get( "inclusionStrategy" ) ) );

        if ( pager != null )
        {
            ComplexNode pagerNode = rootNode.addChild( new ComplexNode( "pager" ) );
            pagerNode.addChild( new SimpleNode( "page", pager.getPage() ) );
            pagerNode.addChild( new SimpleNode( "pageCount", pager.getPageCount() ) );
            pagerNode.addChild( new SimpleNode( "total", pager.getTotal() ) );
            pagerNode.addChild( new SimpleNode( "nextPage", pager.getNextPage() ) );
            pagerNode.addChild( new SimpleNode( "prevPage", pager.getPrevPage() ) );
        }

        rootNode.addChild( fieldFilterService.filter( getEntityClass(), entityList, fields ) );

        return rootNode;
    }

    @RequestMapping(value = "/{uid}/{property}", method = RequestMethod.GET)
    public @ResponseBody RootNode getObjectProperty( @PathVariable("uid") String uid, @PathVariable("property") String property,
        @RequestParam Map<String, String> parameters, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        return getObjectInternal( uid, parameters, Lists.<String>newArrayList(), Lists.newArrayList( property ) );
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public @ResponseBody RootNode getObject( @PathVariable("uid") String uid, @RequestParam Map<String, String> parameters,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        List<String> fields = Lists.newArrayList( contextService.getParameterValues( "fields" ) );
        List<String> filters = Lists.newArrayList( contextService.getParameterValues( "filter" ) );

        if ( fields.isEmpty() )
        {
            fields.add( ":all" );
        }

        return getObjectInternal( uid, parameters, filters, fields );
    }

    private RootNode getObjectInternal( String uid, Map<String, String> parameters,
        List<String> filters, List<String> fields ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        List<T> entities = getEntity( uid, options );

        if ( entities.isEmpty() )
        {
            throw new NotFoundException( uid );
        }

        entities = objectFilterService.filter( entities, filters );

        if ( options.hasLinks() )
        {
            linkService.generateLinks( entities, true );
        }

        if ( aclService.isSupported( getEntityClass() ) )
        {
            addAccessProperties( entities );
        }

        for ( T entity : entities )
        {
            postProcessEntity( entity );
            postProcessEntity( entity, options, parameters );
        }

        CollectionNode collectionNode = fieldFilterService.filter( getEntityClass(), entities, fields );

        if ( options.isTrue( "useWrapper" ) || entities.size() > 1 )
        {
            RootNode rootNode = new RootNode( "metadata" );
            rootNode.setDefaultNamespace( DxfNamespaces.DXF_2_0 );
            rootNode.setNamespace( DxfNamespaces.DXF_2_0 );
            rootNode.addChild( collectionNode );

            rootNode.getConfig().setInclusionStrategy( getInclusionStrategy( parameters.get( "inclusionStrategy" ) ) );

            return rootNode;
        }
        else
        {
            RootNode rootNode = new RootNode( collectionNode.getChildren().get( 0 ) );
            rootNode.setDefaultNamespace( DxfNamespaces.DXF_2_0 );
            rootNode.setNamespace( DxfNamespaces.DXF_2_0 );

            rootNode.getConfig().setInclusionStrategy( getInclusionStrategy( parameters.get( "inclusionStrategy" ) ) );

            return rootNode;
        }
    }

    //--------------------------------------------------------------------------
    // POST
    //--------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.POST, consumes = { "application/xml", "text/xml" })
    public void postXmlObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        if ( !aclService.canCreate( currentUserService.getCurrentUser(), getEntityClass() ) )
        {
            throw new CreateAccessDeniedException( "You don't have the proper permissions to create this object." );
        }

        T parsed = renderService.fromXml( request.getInputStream(), getEntityClass() );
        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.CREATE );

        if ( ImportStatus.SUCCESS.equals( summary.getStatus() ) )
        {
            postCreateEntity( parsed );
        }

        renderService.toXml( response.getOutputStream(), summary );
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        if ( !aclService.canCreate( currentUserService.getCurrentUser(), getEntityClass() ) )
        {
            throw new CreateAccessDeniedException( "You don't have the proper permissions to create this object." );
        }

        T parsed = renderService.fromJson( request.getInputStream(), getEntityClass() );
        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.CREATE );

        if ( ImportStatus.SUCCESS.equals( summary.getStatus() ) )
        {
            postCreateEntity( parsed );
        }

        renderService.toJson( response.getOutputStream(), summary );
    }

    //--------------------------------------------------------------------------
    // PUT
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/{uid}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void putXmlObject( HttpServletResponse response, HttpServletRequest request, @PathVariable("uid") String uid, InputStream
        input ) throws Exception
    {
        List<T> objects = getEntity( uid );

        if ( objects.isEmpty() )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), objects.get( 0 ) ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this object." );
        }

        T parsed = renderService.fromXml( request.getInputStream(), getEntityClass() );
        ((BaseIdentifiableObject) parsed).setUid( uid );

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.UPDATE );

        if ( ImportStatus.SUCCESS.equals( summary.getStatus() ) )
        {
            postUpdateEntity( parsed );
        }

        renderService.toXml( response.getOutputStream(), summary );
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable("uid") String uid, InputStream
        input ) throws Exception
    {
        List<T> objects = getEntity( uid );

        if ( objects.isEmpty() )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), objects.get( 0 ) ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this object." );
        }

        T parsed = renderService.fromJson( request.getInputStream(), getEntityClass() );
        ((BaseIdentifiableObject) parsed).setUid( uid );

        ImportTypeSummary summary = importService.importObject( currentUserService.getCurrentUser().getUid(), parsed, ImportStrategy.UPDATE );

        if ( ImportStatus.SUCCESS.equals( summary.getStatus() ) )
        {
            postUpdateEntity( parsed );
        }

        renderService.toJson( response.getOutputStream(), summary );
    }

    //--------------------------------------------------------------------------
    // DELETE
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/{uid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable("uid") String uid ) throws
        Exception
    {
        List<T> objects = getEntity( uid );

        if ( objects.isEmpty() )
        {
            ContextUtils.conflictResponse( response, getEntityName() + " does not exist: " + uid );
            return;
        }

        if ( !aclService.canDelete( currentUserService.getCurrentUser(), objects.get( 0 ) ) )
        {
            throw new DeleteAccessDeniedException( "You don't have the proper permissions to delete this object." );
        }

        manager.delete( objects.get( 0 ) );
    }

    //--------------------------------------------------------------------------
    // Hooks
    //--------------------------------------------------------------------------

    /**
     * Override to process entities after it has been retrieved from
     * storage and before it is returned to the view. Entities is null-safe.
     */

    protected void postProcessEntities( List<T> entityList, WebOptions options, Map<String, String> parameters )
    {

    }

    /**
     * Override to process entities after it has been retrieved from
     * storage and before it is returned to the view. Entities is null-safe.
     */

    protected void postProcessEntities( List<T> entityList )
    {

    }

    /**
     * Override to process a single entity after it has been retrieved from
     * storage and before it is returned to the view. Entity is null-safe.
     */
    protected void postProcessEntity( T entity ) throws Exception
    {
    }

    /**
     * Override to process a single entity after it has been retrieved from
     * storage and before it is returned to the view. Entity is null-safe.
     */
    protected void postProcessEntity( T entity, WebOptions options, Map<String, String> parameters ) throws Exception
    {
    }

    // TODO replace with hooks directly in idManager
    protected void postCreateEntity( T entity )
    {
    }

    // TODO replace with hooks directly in idManager
    protected void postUpdateEntity( T entity )
    {
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    protected List<T> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<T> entityList;

        if ( options.getOptions().containsKey( "query" ) )
        {
            entityList = Lists.newArrayList( manager.filter( getEntityClass(), options.getOptions().get( "query" ) ) );
        }
        else if ( options.hasPaging() )
        {
            int count = manager.getCount( getEntityClass() );

            Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
            metaData.setPager( pager );

            entityList = Lists.newArrayList( manager.getBetween( getEntityClass(), pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = Lists.newArrayList( manager.getAllSorted( getEntityClass() ) );
        }

        return entityList;
    }

    protected List<T> getEntity( String uid )
    {
        return getEntity( uid, new WebOptions( new HashMap<String, String>() ) );
    }

    protected List<T> getEntity( String uid, WebOptions options )
    {
        ArrayList<T> list = new ArrayList<>();
        Optional<T> identifiableObject = Optional.fromNullable( manager.getNoAcl( getEntityClass(), uid ) );

        if ( identifiableObject.isPresent() )
        {
            list.add( identifiableObject.get() );
        }

        return list; //TODO consider ACL
    }

    protected Schema getSchema()
    {
        return schemaService.getDynamicSchema( getEntityClass() );
    }

    protected void addAccessProperties( List<T> objects )
    {
        User user = currentUserService.getCurrentUser();

        for ( T object : objects )
        {
            Access access = new Access();
            access.setManage( aclService.canManage( user, object ) );
            access.setExternalize( aclService.canExternalize( user, object.getClass() ) );
            access.setWrite( aclService.canWrite( user, object ) );
            access.setRead( aclService.canRead( user, object ) );
            access.setUpdate( aclService.canUpdate( user, object ) );
            access.setDelete( aclService.canDelete( user, object ) );

            ((BaseIdentifiableObject) object).setAccess( access );
        }
    }

    protected void handleLinksAndAccess( WebOptions options, List<T> entityList, boolean deepScan )
    {
        if ( options != null && options.hasLinks() )
        {
            linkService.generateLinks( entityList, deepScan );
        }

        if ( entityList != null && aclService.isSupported( getEntityClass() ) )
        {
            addAccessProperties( entityList );
        }
    }

    //--------------------------------------------------------------------------
    // Reflection helpers
    //--------------------------------------------------------------------------

    private Class<T> entityClass;

    private String entityName;

    private String entitySimpleName;

    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass()
    {
        if ( entityClass == null )
        {
            Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
            entityClass = (Class<T>) actualTypeArguments[0];
        }

        return entityClass;
    }

    protected String getEntityName()
    {
        if ( entityName == null )
        {
            entityName = getEntityClass().getName();
        }

        return entityName;
    }

    protected String getEntitySimpleName()
    {
        if ( entitySimpleName == null )
        {
            entitySimpleName = getEntityClass().getSimpleName();
        }

        return entitySimpleName;
    }

    @SuppressWarnings("unchecked")
    protected T getEntityInstance()
    {
        try
        {
            return (T) Class.forName( getEntityName() ).newInstance();
        }
        catch ( InstantiationException | IllegalAccessException | ClassNotFoundException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    private InclusionStrategy.Include getInclusionStrategy( String inclusionStrategy )
    {
        if ( inclusionStrategy != null )
        {
            Optional<InclusionStrategy.Include> optional = Enums.getIfPresent( InclusionStrategy.Include.class, inclusionStrategy );

            if ( optional.isPresent() )
            {
                return optional.get();
            }
        }

        return InclusionStrategy.Include.NON_NULL;
    }
}
