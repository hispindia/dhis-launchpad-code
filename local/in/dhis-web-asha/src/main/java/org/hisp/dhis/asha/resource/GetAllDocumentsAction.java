package org.hisp.dhis.asha.resource;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetAllDocumentsAction extends ActionPagingSupport<Document>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DocumentService documentService;

    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Document> documents;

    public List<Document> getDocuments()
    {
        return documents;
    }

    private String key;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( isNotBlank( key ) )
        {
            this.paging = createPaging( documentService.getDocumentCountByName( key ) );

            documents = new ArrayList<Document>( documentService.getDocumentsBetweenByName( key, paging.getStartPos(),
                paging.getPageSize() ) );
        }
        else
        {
            this.paging = createPaging( documentService.getDocumentCount() );

            documents = new ArrayList<Document>( documentService.getDocumentsBetween( paging.getStartPos(), paging
                .getPageSize() ) );
        }

        return SUCCESS;
    }
}
