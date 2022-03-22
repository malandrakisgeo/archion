package org.georgemalandrakis.archion.resource;

import com.codahale.metrics.annotation.Timed;
import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.service.FileService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/download")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class DownloadResource extends AbstractResource {
	final FileService fileService;

	public DownloadResource(FileService fileService) {
		this.fileService = fileService;
	}


	@GET
	@Timed
	public Response tryit() {
		FileMetadata fileMetadata = fileService.getUpdatedMetadata("");
		/*FileMetadata fileMetadata = new FileMetadata();
		fileMetadata.setFileid("");
		fileMetadata.setLocalfilename("todo.zip"); */
		fileMetadata.setFileextension("");
		byte[] file;

		file = fileService.getFile(fileMetadata);

		HashMap<String, Object> header = this.createProperHeaders(fileMetadata.getFileextension(), String.valueOf(file.length));


		return buildResponse(null, Response.Status.OK, true, file,  header, null);

	}

	@GET
	@Path("/{fileId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response getfileInfo(ArchionRequest archionRequest) {

		//TODO: Implement
		if (archionRequest.getUserObject().getId().equals(String.valueOf(archionRequest.getInitialMetadata().getUserid()))) {
			return buildResponse(archionRequest.getResponseObject(), Response.Status.OK);
		}
		return buildResponse(archionRequest.getResponseObject(), Response.Status.UNAUTHORIZED);

	}


	@GET
	@Path("/{fileId}/download")
	@Produces({"application/pdf", "image/jpeg", "image/gif", "image/png", "application/octet-stream"})
	@Timed
	public Response downloadFile(ArchionRequest archionRequest) {

		byte[] file;
		FileMetadata fileMetadata = fileService.getUpdatedMetadata(archionRequest.getInitialMetadata().getFileid());

		file = fileService.getFile(fileMetadata);

		if (file!=null) {
			HashMap<String, Object> header = this.createProperHeaders(archionRequest.getInitialMetadata().getFileextension(), String.valueOf(file.length));

			return buildResponse(archionRequest.getResponseObject(), Response.Status.OK, true, file, header, null);
		}

		return buildResponse(archionRequest.getResponseObject(), Response.Status.UNAUTHORIZED, true, file,  null, null);
	}


	private HashMap<String, Object> createProperHeaders(String fileExtension, String fileLength){
		HashMap<String, Object> header = new HashMap<>();

		String contentType;
		switch (fileExtension.toLowerCase()) {
			case "pdf":
				contentType = "application/pdf";
				break;

			case "jpeg":
			case "jpg":
				contentType = "image/jpeg";
				break;

			default:
				contentType = "application/octet-stream";
		}

		header.put("Content-Length", fileLength);
		header.put("Content-Type", contentType);

		return header;
	}

}