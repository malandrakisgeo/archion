package org.georgemalandrakis.archion.resource;

import com.codahale.metrics.annotation.Timed;
import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.core.ArchionUser;
import org.georgemalandrakis.archion.service.FileService;
import org.georgemalandrakis.archion.handlers.CloudHandler;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;


@Path("/upload")
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.MULTIPART_FORM_DATA})
public class UploadResource extends AbstractResource {
    FileService fileService;
    CloudHandler cloudHandler;
    ArchionRequest archionRequest;

    public UploadResource(FileService fileService, CloudHandler cloudHandler) {
        this.fileService = fileService;
        this.cloudHandler = cloudHandler;
        archionRequest = new ArchionRequest();
    }

    @GET
    public Response test() {
        return buildResponse(archionRequest, Response.Status.OK, "Hurray!");
    }

    @POST
    @Timed
    //originally @Valid  org.glassfish.jersey.media.multipart.FormDataMultiPart
    public Response create(@FormDataParam("req") ArchionRequest archionRequest,
                           @Valid @FormDataParam("purpose") String purpose,
                           @Valid @FormDataParam("filename") String filename,
                           @Valid @FormDataParam("file") File file) throws FileNotFoundException {
        ArchionUser us = new ArchionUser(); //TODO: implement authentication
        us.setId(UUID.randomUUID().toString());
        archionRequest.setUserObject(us);

        if (file == null) {
            return buildResponse(archionRequest, Response.Status.BAD_REQUEST, "You need to send a file.");
        }

        try {
            this.fileService.createNewFile(archionRequest, purpose, filename, file);
        } catch (Exception e) {
            e.printStackTrace();
            return buildResponse(archionRequest, Response.Status.INTERNAL_SERVER_ERROR, "Could not upload file.");
        }
        return buildResponse(archionRequest, Response.Status.OK, "Hurray!");


    }

}
