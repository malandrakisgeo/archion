package org.georgemalandrakis.archion.handlers;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;

import java.io.*;

public class CloudHandler {
	private String amazonaccesskey;
	private String amazonsecretkey;

	private String bucketName = "myFiles";

	private Regions clientRegion = Regions.EU_NORTH_1;

	private AWSCredentials credentials;
	private AmazonS3 sClient;


	public CloudHandler(ConnectionManager connectionObject) {
		amazonaccesskey = connectionObject.getAmazonAccesskey();
		amazonsecretkey = connectionObject.getAmazonSecretkey();
		credentials = new BasicAWSCredentials(amazonaccesskey, amazonsecretkey);
		sClient = AmazonS3ClientBuilder.standard().withRegion(clientRegion).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		System.out.println("Running!");
		System.out.println(amazonaccesskey);
	}


	public byte[] downloadFile(String fileid) throws Exception {
		S3Object fullObject = sClient.getObject(new GetObjectRequest(bucketName, fileid));
		return IOUtils.toByteArray(fullObject.getObjectContent());
	}


	public FileMetadata uploadFile(InputStream inputStream, FileMetadata fileMetadata) {
		ObjectMetadata metadata = new ObjectMetadata();

		if (inputStream != null) {
			try {
				//sClient.putObject(bucketName, filename, inputStream, metadata); //TODO: Uncomment
				fileMetadata.setPhase(FileProcedurePhase.CLOUD_SERVICE_STORED);

				return fileMetadata;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return fileMetadata;
	}


	public FileMetadata removeFile(FileMetadata fileMetadata) {
		if (fileMetadata != null) {
			try {
				sClient.deleteObject(bucketName,fileMetadata.getFileid());
				fileMetadata.setPhase(FileProcedurePhase.CLOUD_SERVICE_REMOVED);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return fileMetadata;
	}




}
