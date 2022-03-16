package org.georgemalandrakis.archion.resource;

import org.georgemalandrakis.archion.core.*;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractResource {

	public Response buildResponse(ArchionRequest archionRequest, Response.Status status) {
		return buildResponse(archionRequest, status, false, null, null, null);
	}

	public Response buildResponse(ArchionRequest archionRequest, Response.Status status, Object entity) {
		return buildResponse(archionRequest, status, false, entity,  null, null);
	}

	public Response buildResponse(ArchionRequest archionRequest, Response.Status status, Boolean data, Object entity,  HashMap<String, Object> header, String mediaType) {
		ArchionResponse archionResponse = archionRequest.getResponseObject();

		Response.ResponseBuilder responseBuilder = Response.status(status);
		responseBuilder.type(mediaType);


		//Data
		if (entity != null && !data) {
			archionResponse.setData(entity);
		}

		if (data) {
			responseBuilder = responseBuilder.entity(entity);
		} else {
			responseBuilder = responseBuilder.entity(archionResponse);
		}

		//Check header
		if (header != null) {
			for (Map.Entry<String, Object> entry : header.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				responseBuilder.header(key, value);
			}
		}

		return responseBuilder.build();
	}

}
