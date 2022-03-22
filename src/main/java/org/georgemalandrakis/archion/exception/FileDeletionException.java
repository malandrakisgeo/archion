package org.georgemalandrakis.archion.exception;

import org.georgemalandrakis.archion.core.ArchionConstants;
import org.georgemalandrakis.archion.model.FileMetadata;

public class FileDeletionException extends Exception{

    /*
        Throwed when a scheduled tasks fails to permanently delete a file.
        The FileProcedurePhase is not updated during permanent deletion
        to avoid unnecessary db queries, hence the dedicated exception.
     */
    public FileDeletionException(FileMetadata fileMetadata){
        super(ArchionConstants.FAILED_DELETION_MESSAGE + fileMetadata.toJSON());
    }
}
