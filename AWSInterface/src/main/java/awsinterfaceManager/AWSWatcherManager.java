package awsinterfaceManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import ssh.SSHClientExecutor;

import com.gmo.logger.Log4JLogger;

public class AWSWatcherManager implements IAWSInstanceStateChanged {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private Map<String, String> mapInstanceState;

	// The thread that will update the model and check for all values
	private AWSInstancesStateWatcher statesWatcher;

	public AWSWatcherManager() {
		mapInstanceState = Collections.synchronizedMap(new HashMap<String, String>());
	}

	public void watchForState(String instID, String expectingState) {
		if (mapInstanceState.get(instID) == null) {
			mapInstanceState.put(instID, expectingState);
			if (statesWatcher == null || !statesWatcher.isRunning()) {
				statesWatcher = new AWSInstancesStateWatcher(this, mapInstanceState);
			}
		} else {
			String expState = mapInstanceState.get(instID);
			LOG.warn("This instance is already waiting to reach a new state : " + expState + " . update expected state to : " + expectingState);
			mapInstanceState.put(instID, expectingState);
		}
	}

	public boolean expectedStateReached(String instID, String state) {
		LOG.debug(instID + " successfully reached expected state : " + state);
		mapInstanceState.remove(instID);
		
		if(state.equals("running")) {
			
			String publicIp = AWSInterfaceManager.getInstance().getInstanceWithID(instID).getPublicIpAddress();
			SSHClientExecutor sshClient = new SSHClientExecutor(publicIp);
			sshClient.startRemoteClient();
		}
		
		if(mapInstanceState.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public void expectedStateUnableToBeReached(String instID) {
		LOG.warn("Unable to reach expected state for instance " + instID);
		mapInstanceState.remove(instID);
	}
}
