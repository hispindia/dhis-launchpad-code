package org.hisp.dhis.sm.impl;

import static org.apache.commons.lang.StringUtils.trimToNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.dxf2.synch.AvailabilityStatus;
import org.hisp.dhis.dxf2.synch.DefaultSynchronizationManager;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.system.util.CodecUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class SynchManager
{
    private static final Log log = LogFactory.getLog( DefaultSynchronizationManager.class );

    private static final String PING_PATH = "/system/ping";

    private static final String HEADER_AUTHORIZATION = "Authorization";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private RenderService renderService;

    @Autowired
    private RestTemplate restTemplate;

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /*
     * public void getUserGroups( SynchInstance instance, String resource ) {
     * String userId = instance.getUserId(); String password =
     * instance.getPassword();
     * 
     * String authString = userId + ":" + password; byte[] authEncBytes =
     * Base64.encodeBase64( authString.getBytes() ); String authStringEnc = new
     * String( authEncBytes );
     * 
     * String instanceURL = instance.getUrl() + "/" + resource +".xml"; try {
     * URL url = new URL( instanceURL ); URLConnection urlConnection =
     * url.openConnection(); urlConnection.setRequestProperty( "Authorization",
     * "Basic " + authStringEnc ); InputStream is =
     * urlConnection.getInputStream();
     * 
     * if( resource.equalsIgnoreCase( "userGroups" ) ) { StaXParser read = new
     * StaXParser();
     * 
     * UserGroup userGroup = renderService.fromXml( is, UserGroup.class ); for(
     * Object ug : userGroups ) { UserGroup userGroup = (Object) ug;
     * System.out.println( userGroup.getUid() + " : " + userGroup.getName() ); }
     * } } catch ( MalformedURLException e ) { e.printStackTrace(); } catch (
     * IOException e ) { e.printStackTrace(); } }
     */

    public AvailabilityStatus isRemoteInstanceAvailable( SynchInstance instance )
    {
        if ( !isInstanceServerConfigured( instance ) )
        {
            return new AvailabilityStatus( false, "Remote server is not configured" );
        }

        String url = instance.getUrl() + PING_PATH;

        log.info( "Remote server ping URL: " + url + ", username: " + instance.getUserId() );

        HttpEntity<String> request = getBasicAuthRequestEntity( instance.getUserId(), instance.getPassword() );

        ResponseEntity<String> response = null;
        HttpStatus sc = null;
        String st = null;
        AvailabilityStatus status = null;

        try
        {
            response = restTemplate.exchange( url, HttpMethod.GET, request, String.class );
            sc = response.getStatusCode();
        }
        catch ( HttpClientErrorException ex )
        {
            sc = ex.getStatusCode();
            st = ex.getStatusText();
        }
        catch ( HttpServerErrorException ex )
        {
            sc = ex.getStatusCode();
            st = ex.getStatusText();
        }
        catch ( ResourceAccessException ex )
        {
            return new AvailabilityStatus( false, "Network is unreachable" );
        }

        log.info( "Response: " + response + ", status code: " + sc );

        if ( HttpStatus.FOUND.equals( sc ) )
        {
            status = new AvailabilityStatus( false,
                "Server is available but no authentication was provided, status code: " + sc );
        }
        else if ( HttpStatus.UNAUTHORIZED.equals( sc ) )
        {
            status = new AvailabilityStatus( false, "Server is available but authentication failed, status code: " + sc );
        }
        else if ( HttpStatus.INTERNAL_SERVER_ERROR.equals( sc ) )
        {
            status = new AvailabilityStatus( false,
                "Server is available but experienced an internal error, status code: " + sc );
        }
        else if ( HttpStatus.OK.equals( sc ) )
        {
            status = new AvailabilityStatus( true, "Server is available and authentication was successful" );
        }
        else
        {
            status = new AvailabilityStatus( false, "Server is not available, status code: " + sc + ", text: " + st );
        }

        log.info( status );

        return status;
    }

    public void postMetaData( MetaData metaData, String url, SynchInstance instance )
    {
        AvailabilityStatus availability = isRemoteInstanceAvailable( instance );

        if ( !availability.isAvailable() )
        {
            log.info( "Aborting synch, server not available" );
            return;
        }

        // HttpEntity<String> request =
        // getBasicAuthRequestEntity(instance.getUserId(),
        // instance.getPassword());

        ResponseEntity<String> response = null;
        HttpStatus sc = null;
        String st = null;
        AvailabilityStatus status = null;

        // set your headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );
        headers
            .set( HEADER_AUTHORIZATION, CodecUtils.getBasicAuthString( instance.getUserId(), instance.getPassword() ) );

        HttpEntity<MetaData> entity = new HttpEntity<MetaData>( metaData, headers );
        // HttpEntity<ImportSummary> entity = new HttpEntity<ImportSummary>(
        // metaData, headers );

        try
        {
            response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class );

            System.out.println( "after post call : " + response.getBody() );

            /*
             * final RequestCallback requestCallback = new RequestCallback() {
             * 
             * public void doWithRequest( ClientHttpRequest request ) throws
             * IOException { request.getHeaders().setContentType(
             * MediaType.APPLICATION_JSON ); request.getHeaders().add(
             * HEADER_AUTHORIZATION, CodecUtils.getBasicAuthString(
             * config.getRemoteServerUsername(),
             * config.getRemoteServerPassword() ) );
             * 
             * } };
             * 
             * restTemplate.execute( url, HttpMethod.POST, requestCallback, null
             * );
             */

        }
        catch ( HttpClientErrorException ex )
        {
            sc = ex.getStatusCode();
            st = ex.getStatusText();
            log.error( "ERROR1: " + st + " : " + ex.getMessage() );
        }
        catch ( HttpServerErrorException ex )
        {
            sc = ex.getStatusCode();
            st = ex.getStatusText();
            log.error( "ERROR2: " + st + " : " + ex.getMessage() );
        }
        catch ( Exception e )
        {
            log.error( "ERROR3: " + e.getMessage() );
        }

    }

    public void postMessage( String url, String message, SynchInstance instance )
    {

        AvailabilityStatus availability = isRemoteInstanceAvailable( instance );

        if ( !availability.isAvailable() )
        {
            log.info( "Aborting synch, server not available" );
            return;
        }

        ResponseEntity<String> response = null;
        String str = null;
        HttpStatus sc = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_XML );
        headers
            .set( HEADER_AUTHORIZATION, CodecUtils.getBasicAuthString( instance.getUserId(), instance.getPassword() ) );
        // headers.set( HEADER_AUTHORIZATION, CodecUtils.getBasicAuthString(
        // instance.getUserId(), instance.getPassword() ) );

        HttpEntity<String> entity = new HttpEntity<String>( message, headers );

        try
        {

            response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class );

            System.out.println( "after Message sent call : " + response.getBody() );

            /*
             * HttpURLConnection connection = (HttpURLConnection)new
             * URL(url).openConnection(); connection.setRequestMethod("POST");
             * BASE64Encoder enc = new sun.misc.BASE64Encoder(); String
             * userpassword = username + ":" + password; String
             * encodedAuthorization = enc.encode( userpassword.getBytes() );
             * connection.setRequestProperty("Authorization", "Basic "+
             * encodedAuthorization);
             * connection.setRequestProperty("Content-Type",
             * "application/xml; charset=UTF-8");
             * connection.setRequestProperty("Accept", "application/xml");
             * connection.setDoOutput(true); connection.setDoInput(true);
             * 
             * OutputStream out = connection.getOutputStream();
             * out.write(message.getBytes()); out.flush(); out.close();
             * 
             * System.out.println(connection.getResponseCode());
             * System.out.println(connection.getResponseMessage());
             */

        }
        catch ( HttpClientErrorException ex )
        {
            sc = ex.getStatusCode();
            str = ex.getStatusText();
            log.error( "ERROR1: " + str + " : " + ex.getMessage() );
        }
        catch ( HttpServerErrorException ex )
        {
            sc = ex.getStatusCode();
            str = ex.getStatusText();
            log.error( "ERROR2: " + str + " : " + ex.getMessage() );
        }
        catch ( Exception e )
        {
            log.error( "ERROR3: " + e.getMessage() );
        }
    }

    public MetaData getMetaData( SynchInstance instance, String url, String thisURL )
    {

        AvailabilityStatus availability = isRemoteInstanceAvailable( instance );

        if ( !availability.isAvailable() )
        {
            log.info( "Aborting synch, server not available" );
            return null;
        }

        // String url = instance.getUrl() + "/metadata.xml?assumeTrue=false&" +
        // resource + "=true";
        // String url = instance.getUrl() + "/AccepetanceDE.xml";

        HttpEntity<String> request = getBasicAuthRequestEntity( instance.getUserId(), instance.getPassword() );

        ResponseEntity<String> response = null;
        HttpStatus sc = null;
        String st = null;

        AvailabilityStatus status = null;

        MetaData metaData = null;
        try
        {
            if ( thisURL != null )
            {
                url += "?cleintURL=" + thisURL;
            }

            response = restTemplate.exchange( url, HttpMethod.GET, request, String.class );
            sc = response.getStatusCode();

            System.out.println( url + " : " + sc );
            System.out.println( response.getBody() );

            InputStream stream = new ByteArrayInputStream( response.getBody().getBytes( "UTF-8" ) );
            metaData = renderService.fromXml( stream, MetaData.class );

        }
        catch ( HttpClientErrorException ex )
        {
            sc = ex.getStatusCode();
            st = ex.getStatusText();
            log.error( "ERROR: " + st );
        }
        catch ( HttpServerErrorException ex )
        {
            sc = ex.getStatusCode();
            st = ex.getStatusText();
            log.error( "ERROR: " + st );
        }
        catch ( Exception e )
        {
            log.error( "ERROR: " + e.getMessage() );
        }

        return metaData;

    }

    public String getMetaDataString( SynchInstance instance, String url )
    {

        AvailabilityStatus availability = isRemoteInstanceAvailable( instance );

        String metaDataString = "";
        if ( !availability.isAvailable() )
        {
            log.info( "Aborting synch, server not available" );
            return null;
        }

        // String url = instance.getUrl() + "/AccepetanceDE.xml";

        HttpEntity<String> request = getBasicAuthRequestEntity( instance.getUserId(), instance.getPassword() );

        ResponseEntity<String> response = null;
        HttpStatus sc = null;
        String st = null;
        AvailabilityStatus status = null;

        MetaData metaData = null;
        try
        {
            response = restTemplate.exchange( url, HttpMethod.GET, request, String.class );
            sc = response.getStatusCode();

            metaDataString = response.getBody();
            System.out.println( url + " : " + sc );
            System.out.println( response.getBody() );

        }
        catch ( HttpClientErrorException ex )
        {
            sc = ex.getStatusCode();
            st = ex.getStatusText();
            log.error( "ERROR: " + st );
        }
        catch ( HttpServerErrorException ex )
        {
            sc = ex.getStatusCode();
            st = ex.getStatusText();
            log.error( "ERROR: " + st );
        }
        catch ( Exception e )
        {
            log.error( "ERROR: " + e.getMessage() );
        }

        return metaDataString;

    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    /**
     * Indicates whether a remote instance has been properly configured.
     */
    private boolean isInstanceServerConfigured( SynchInstance instance )
    {
        if ( trimToNull( instance.getUrl() ) == null )
        {
            log.info( "Remote server URL not set" );
            return false;
        }

        if ( trimToNull( instance.getUserId() ) == null || trimToNull( instance.getPassword() ) == null )
        {
            log.info( "Remote server username or password not set" );
            return false;
        }

        return true;
    }

    /**
     * Creates an HTTP entity for requests with appropriate header for basic
     * authentication.
     */
    private <T> HttpEntity<T> getBasicAuthRequestEntity( String username, String password )
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set( HEADER_AUTHORIZATION, CodecUtils.getBasicAuthString( username, password ) );
        return new HttpEntity<T>( headers );
    }
}
