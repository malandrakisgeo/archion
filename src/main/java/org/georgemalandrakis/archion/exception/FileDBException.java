package org.georgemalandrakis.archion.exception;

public class FileDBException extends Exception {

    public FileDBException(){
        super("Could not save file to database.");
    }

}
