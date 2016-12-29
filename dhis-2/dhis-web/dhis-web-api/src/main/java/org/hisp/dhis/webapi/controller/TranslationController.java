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

import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.translation.TranslationService;
import org.hisp.dhis.translation.Translations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hisp.dhis.webapi.utils.ContextUtils.CONTENT_TYPE_JSON;

/**
 * @author kprakash.
 */
@Controller
@RequestMapping(value = TranslationController.RESOURCE_PATH)
public class TranslationController
{
    public static final String RESOURCE_PATH = "/translations";

    @Autowired
    private ContextUtils contextUtils;


    @Autowired
    private TranslationService translationService;


    @RequestMapping( produces = CONTENT_TYPE_JSON, method = RequestMethod.GET )
    public void exportJson( HttpServletResponse response ) throws IOException
    {
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, ContextUtils.CacheStrategy.NO_CACHE, "translations.json", false );
        JacksonUtils.toJson( response.getOutputStream(), new Translations( translationService.getAllTranslations() ) );
    }

    @RequestMapping(method = RequestMethod.POST, consumes = CONTENT_TYPE_JSON)
    public void importJson( HttpServletRequest request ) throws IOException
    {
        Translations translations = JacksonUtils.fromJson( request.getInputStream(), Translations.class );
        translationService.createOrUpdate( translations.getTranslations() );
    }
}