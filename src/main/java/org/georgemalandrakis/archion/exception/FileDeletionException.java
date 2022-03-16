package org.georgemalandrakis.archion.exception;

import org.georgemalandrakis.archion.core.ArchionConstants;

public class FileDeletionException extends Exception{

    public FileDeletionException(String id){
        super(ArchionConstants.FAILED_DELETION_MESSAGE + id);
    }
}
