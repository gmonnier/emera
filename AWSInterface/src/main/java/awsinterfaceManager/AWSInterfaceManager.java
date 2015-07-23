package awsinterfaceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

import configuration.AWSContextManager;

public class AWSInterfaceManager {

	private AmazonEC2Client ec2Client;

	private List<Instance> instances;

	// associate watcher manager
	private AWSWatcherManager watcherManager;
	
	// Specify wether connection has been successfully done to AWS
	private boolean initialized;

	private static class AnalysisManagerHolder {
		public final static AWSInterfaceManager instance = new AWSInterfaceManager();
	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private AWSInterfaceManager() {
		LOG.info("Instantiate AWS Interface manager");
		System.setProperty("aws.accessKeyId", AWSContextManager.getInstance().getConfig().getAwsAccessKeyID());
		System.setProperty("aws.secretKey", AWSContextManager.getInstance().getConfig().getAwsSecretAccessKey());

		this.ec2Client = new AmazonEC2Client(new DefaultAWSCredentialsProviderChain());
		ec2Client.setRegion(Region.getRegion(Regions.US_WEST_2));

		instances = Collections.synchronizedList(new ArrayList<Instance>());
		watcherManager = new AWSWatcherManager();
		try {
			updateInstances();
			initialized = true;
		} catch (com.amazonaws.AmazonServiceException e) {
			initialized = false;
		}
		
	}

	public static synchronized AWSInterfaceManager getInstance() {
		return AnalysisManagerHolder.instance;
	}

	public synchronized void updateInstances() {
		LOG.info("Update AWS instances model");
		instances.clear();
		DescribeInstancesResult res = ec2Client.describeInstances();
		List<Reservation> listRes = res.getReservations();
		for (Iterator<Reservation> iterator = listRes.iterator(); iterator.hasNext();) {
			Reservation reservation = (Reservation) iterator.next();
			instances.addAll(reservation.getInstances());
		}
	}

	public Instance getInstanceWithID(String id) {
		for (Iterator<Instance> iterator = instances.iterator(); iterator.hasNext();) {
			Instance instance = (Instance) iterator.next();
			if (instance.getInstanceId().equals(id)) {
				return instance;
			}
		}
		return null;
	}

	public List<Instance> getAllInstances() {
		return instances;
	}

	public synchronized void startAllInstances() {
		for (Iterator<Instance> iterator = instances.iterator(); iterator.hasNext();) {
			
			Instance instance = (Instance) iterator.next();
			if(instance.getState().getName().equals("running")) {
				// instance is already running...
				continue;
			}

			StartInstancesRequest startInstanceReq = new StartInstancesRequest();
			startInstanceReq.getInstanceIds().add(instance.getInstanceId());
			try {
				LOG.info(instance.getInstanceId() + "   request to be started");
				ec2Client.startInstances(startInstanceReq);
				watcherManager.watchForState(instance.getInstanceId(), "running");
			} catch (AmazonClientException ace) {
				LOG.warn("Unable to start EC2 instance : " + ace.getMessage());
			}

		}
	}

	public synchronized void stopAllInstances() {
		// stop instances
		for (Iterator<Instance> iterator = instances.iterator(); iterator.hasNext();) {
			Instance instance = (Instance) iterator.next();
			if(instance.getState().getName().equals("stopped")) {
				// instance is already stopped...
				continue;
			}

			StopInstancesRequest stopInstanceReq = new StopInstancesRequest();
			stopInstanceReq.getInstanceIds().add(instance.getInstanceId());

			try {
				LOG.info(instance.getInstanceId() + "   request to be stopped");
				ec2Client.stopInstances(stopInstanceReq);
				watcherManager.watchForState(instance.getInstanceId(), "stopped");
			} catch (AmazonClientException ace) {
				LOG.warn("Unable to stop EC2 instance : " + ace.getMessage());
			}

		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	
}
