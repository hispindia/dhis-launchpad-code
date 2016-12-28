package org.hisp.dhis.webapi.controller;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;
import org.hisp.dhis.schema.descriptors.DocumentSchemaDescriptor;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.utils.ContextUtils.CacheStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = DocumentSchemaDescriptor.API_ENDPOINT )
public class DocumentController
    extends AbstractCrudController<Document>
{
    @Autowired
    private DocumentService documentService;

    @Autowired
    private LocationManager locationManager;

    @Autowired
    private ContextUtils contextUtils;

    @RequestMapping( value = "/{uid}/data", method = RequestMethod.GET )
    public void getDocumentContent( @PathVariable( "uid" ) String uid, HttpServletResponse response ) throws Exception
    {
        Document document = documentService.getDocument( uid );

        if ( document == null )
        {
            ContextUtils.notFoundResponse( response, "Resource not found for identifier: " + uid );
            return;
        }

        if ( document.isExternal() )
        {
            response.sendRedirect( response.encodeRedirectURL( document.getUrl() ) );
        }
        else
        {
            contextUtils.configureResponse( response, document.getContentType(), CacheStrategy.CACHE_TWO_WEEKS, document.getUrl(),
                document.getAttachment() );

            InputStream in = null;

            try
            {
                in = locationManager.getInputStream( document.getUrl(), DocumentService.DIR );

                IOUtils.copy( in, response.getOutputStream() );
            }
            catch ( LocationManagerException ex )
            {
                ContextUtils.conflictResponse( response, "Document could not be found: " + document.getUrl() );

                return;
            }
            finally
            {
                IOUtils.closeQuietly( in );
            }
        }
    }
}
