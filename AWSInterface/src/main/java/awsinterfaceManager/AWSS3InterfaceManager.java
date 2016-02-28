package awsinterfaceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.s3.AmazonS3Client;
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
		s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));

	}

	public static synchronized AWSS3InterfaceManager getInstance() {
		return AWSS3InterfaceManagerHolder.instance;
	}

	
}
