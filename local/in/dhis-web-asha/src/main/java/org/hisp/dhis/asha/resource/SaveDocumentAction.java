package org.hisp.dhis.asha.resource;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveDocumentAction implements Action
{
    private static final Log log = LogFactory.getLog( SaveDocumentAction.class );

    private static final String HTTP_PREFIX = "http://";

    private static final String HTTPS_PREFIX = "https://";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    private DocumentService documentService;

    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }

    @Autowired
    private CurrentUserService currentUserService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }

    private Boolean external;

    public void setExternal( Boolean external )
    {
        this.external = external;
    }

    private File file;

    public void setUpload( File file )
    {
        this.file = file;
    }

    private String fileName;

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private String contentType;

    public void setUploadContentType( String contentType )
    {
        this.contentType = contentType;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Document document = new Document();

        if ( id != null )
        {
            document = documentService.getDocument( id );
        }

        if ( !external && file != null )
        {
            log.info( "Uploading file: '" + fileName + "', content-type: '" + contentType + "'" );

            File destination = locationManager.getFileForWriting( fileName, DocumentService.DIR );

            boolean fileMoved = file.renameTo( destination );

            if ( !fileMoved )
            {
                throw new RuntimeException( "File was not uploaded" );
            }

            url = fileName;
            document.setUrl( url );
            document.setContentType( contentType );
        }

        else if ( external )
        {
            if ( !(url.startsWith( HTTP_PREFIX ) || url.startsWith( HTTPS_PREFIX )) )
            {
                url = HTTP_PREFIX + url;
            }

            log.info( "Document name: '" + name + "', url: '" + url + "', external: '" + external + "'" );

            document.setUrl( url );
        }

        document.setExternal( external );

        document.setName( name );

        documentService.saveDocument( document );

        return SUCCESS;
    }
}
