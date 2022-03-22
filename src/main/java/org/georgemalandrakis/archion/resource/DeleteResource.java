package org.georgemalandrakis.archion.resource;

import com.codahale.metrics.annotation.Timed;
import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.service.FileService;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/delete")
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.MULTIPART_FORM_DATA})
public class DeleteResource extends AbstractResource {
    FileService fileService;

    public DeleteResource(FileService fileService) {
        this.fileService = fileService;
    }

    @DELETE
    @Timed
    @Path("/{fileId}")
    public Response deletebyId(@FormDataParam("req") ArchionRequest archionRequest) {

        archionRequest = this.fileService.remove(archionRequest);

        if (!archionRequest.getResponseObject().hasError()) {
            return buildResponse(archionRequest.getResponseObject(), Response.Status.OK);
        }

        return buildResponse(archionRequest.getResponseObject(), Response.Status.NO_CONTENT);

    }

    @DELETE
    @Timed
    @Path("/all")
    public Response deleteAll(@FormDataParam("req") ArchionRequest archionRequest) {

        return null;
    }
}
