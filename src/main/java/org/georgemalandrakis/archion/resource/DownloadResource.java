package org.georgemalandrakis.archion.resource;

import com.codahale.metrics.annotation.Timed;
import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.model.UserFile;
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
		UserFile userFile = fileService.retrieveFileMetadata(new ArchionRequest(), "");
		/*UserFile userFile = new UserFile();
		userFile.setFileid("");
		userFile.setLocalfilename("todo.zip"); */
		userFile.setFileextension("");
		byte[] file;

		file = fileService.getFile(userFile);

		HashMap<String, Object> header = this.createProperHeaders(userFile.getFileextension(), String.valueOf(file.length));


		return buildResponse(new ArchionRequest(), Response.Status.OK, true, file,  header, null);

	}

	@GET
	@Path("/{fileId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response getfileInfo(ArchionRequest archionRequest, @PathParam("fileId") String fileId) {
		UserFile userFile = fileService.retrieveFileMetadata(archionRequest, fileId);

		if (archionRequest.getUserObject().getId().equals(String.valueOf(userFile.getUserid()))) {
			return buildResponse(archionRequest, Response.Status.OK, userFile);
		}
		return buildResponse(archionRequest, Response.Status.UNAUTHORIZED, userFile);

	}


	@GET
	@Path("/{fileId}/download")
	@Produces({"application/pdf", "image/jpeg", "image/gif", "image/png", "application/octet-stream"})
	@Timed
	public Response downloadFile(ArchionRequest archionRequest, @PathParam("fileId") String fileId) {

		UserFile userFile = fileService.retrieveFileMetadata(archionRequest, fileId);
		byte[] file;

		file = fileService.getFile(userFile);

		HashMap<String, Object> header = this.createProperHeaders(userFile.getFileextension(), String.valueOf(file.length));

		if (archionRequest.getUserObject().getId().equals(String.valueOf(userFile.getUserid()))) {
			return buildResponse(archionRequest, Response.Status.OK, true, file, header, null);
		}

		return buildResponse(archionRequest, Response.Status.UNAUTHORIZED, true, file,  header, null);
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