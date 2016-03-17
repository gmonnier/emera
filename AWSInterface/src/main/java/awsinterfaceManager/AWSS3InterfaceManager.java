package awsinterfaceManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gmo.logger.Log4JLogger;

import configuration.AWSContextManager;

public class AWSS3InterfaceManager {

	private AmazonS3Client s3Client;

	private static class AWSS3InterfaceManagerHolder {
		public final static AWSS3InterfaceManager instance = new AWSS3InterfaceManager();
	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private AWSS3InterfaceManager() {
		LOG.info("Instantiate AWS S3 Interface manager");

		System.setProperty("aws.accessKeyId", AWSContextManager.getInstance().getConfig().getAwsAccessKeyID());
		System.setProperty("aws.secretKey", AWSContextManager.getInstance().getConfig().getAwsSecretAccessKey());

		this.s3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
		s3Client.setRegion(Region.getRegion(Regions.US_EAST_1));

	}

	public static synchronized AWSS3InterfaceManager getInstance() {
		return AWSS3InterfaceManagerHolder.instance;
	}

	public boolean isBucketValid(String bucketName) {
		return s3Client.doesBucketExist(bucketName);
	}

	public List<String> listUsersRepositories(String bucketName) {
		LOG.debug("List S3 Users results directories");

		List<String> usersRepositories = new ArrayList<String>();
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withDelimiter("/");
		ObjectListing objectListing;

		do {
			objectListing = s3Client.listObjects(listObjectsRequest);
			listObjectsRequest.setMarker(objectListing.getNextMarker());
			usersRepositories.addAll(objectListing.getCommonPrefixes());
		} while (objectListing.isTruncated());

		return usersRepositories;
	}

	public List<String> listDirectories(String bucketName, String prefix) {

		List<String> directories = new ArrayList<String>();
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix).withDelimiter("/");
		ObjectListing objectListing;

		do {
			objectListing = s3Client.listObjects(listObjectsRequest);
			listObjectsRequest.setMarker(objectListing.getNextMarker());
			directories.addAll(objectListing.getCommonPrefixes());
		} while (objectListing.isTruncated());

		return directories;

	}

	public List<Object[]> listFiles(String bucketName, String prefix) {
		List<Object[]> files = new ArrayList<Object[]>();
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix).withDelimiter("/");
		ObjectListing objectListing;

		do {
			objectListing = s3Client.listObjects(listObjectsRequest);
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				LOG.debug("\t\t--- " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
				files.add(new Object[]{objectSummary.getKey(), objectSummary.getSize(), objectSummary.getLastModified().getTime()});
			}
			listObjectsRequest.setMarker(objectListing.getNextMarker());
		} while (objectListing.isTruncated());

		return files;
	}

	public InputStream getObjectInputStream(String bucketName, String key) {
		S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));
		return object.getObjectContent();
	}

}
