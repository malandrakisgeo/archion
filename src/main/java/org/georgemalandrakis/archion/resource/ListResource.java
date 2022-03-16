package org.georgemalandrakis.archion.resource;

import com.codahale.metrics.annotation.Timed;
import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.model.UserFile;
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
		List<UserFile> fileList;

		fileList = fileService.retrieveList(archionRequest, fileType);


		///filetypes: emailAttachment,uploadedByUser . . . . . .

		if (!fileList.isEmpty()) {
			return buildResponse(archionRequest, Response.Status.OK, fileList);
		}

		return buildResponse(archionRequest, Response.Status.NO_CONTENT);
	}

	@GET
	@Path("/exportfiles")
	@Timed
	public Response exportFiles(ArchionRequest archionRequest, List<String> fileIds) {
		ZipOutputStream zipFile = null;
		UserFile userFile;
		int count = 0;

		try {
			for (String id : fileIds) {
				userFile = fileService.retrieveFileMetadata(archionRequest, id);
				if (userFile.getUserid().equals(archionRequest.getUserObject().getId())) {
					count++;
					zipFile.putNextEntry(new ZipEntry(userFile.getOriginalfilename()));
					zipFile.write(fileService.getFile(userFile));
				} else {
					archionRequest.getResponseObject().addError("No rights", "You have no rights to download one or more of the requested files");
				}
			}
			zipFile.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}

		if (count !=0) {
			return buildResponse(archionRequest, Response.Status.OK, zipFile);
		}

		return buildResponse(archionRequest, Response.Status.NO_CONTENT);
	}

}


