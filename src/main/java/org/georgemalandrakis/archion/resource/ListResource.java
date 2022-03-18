package org.georgemalandrakis.archion.resource;

import com.codahale.metrics.annotation.Timed;
import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.service.FileService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Path("/filelist")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class ListResource extends AbstractResource {
    final FileService fileService;


    public ListResource(FileService fileService) {
        this.fileService = fileService;
    }

    @GET
    @Timed
    public Response fileList(ArchionRequest archionRequest, String fileType) {
        ;
        List<FileMetadata> fileList;

        fileList = fileService.retrieveList(archionRequest, fileType);


        ///filetypes: emailAttachment,uploadedByUser . . . . . .

        if (!fileList.isEmpty()) {
            return buildResponse(archionRequest.getResponseObject(), Response.Status.OK, fileList);
        }

        return buildResponse(archionRequest.getResponseObject(), Response.Status.NO_CONTENT);
    }

    @GET
    @Path("/exportfiles")
    @Timed
    public Response exportFiles(ArchionRequest archionRequest, List<String> fileIds) {
        ZipOutputStream zipFile = null;
        FileMetadata fileMetadata;
        int count = 0;

        try {
            for (String id : fileIds) {
                fileMetadata = fileService.retrieveFileMetadata(archionRequest, id);
                count++;
                zipFile.putNextEntry(new ZipEntry(fileMetadata.getOriginalfilename()));
                zipFile.write(fileService.getFile(fileMetadata));

            }
            zipFile.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        if (count != 0) {
            return buildResponse(archionRequest.getResponseObject(), Response.Status.OK, zipFile);
        }

        return buildResponse(archionRequest.getResponseObject(), Response.Status.NO_CONTENT);
    }

}


