package awsinterfaceManager;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.amazonaws.services.ec2.model.Instance;
import com.gmo.logger.Log4JLogger;

public class AWSInstancesStateWatcher implements Runnable {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private final static int WAIT_FOR_TRANSITION_INTERVAL = 4000;

	private IAWSInstanceStateChanged listener;

	private Map<String, String> mapExpectedStates;

	private Thread thread;

	public AWSInstancesStateWatcher(IAWSInstanceStateChanged listener, Map<String, String> mapExpectedStates) {
		this.listener = listener;
		this.mapExpectedStates = mapExpectedStates;

		thread = new Thread(this);
		thread.start();
		thread.setName("AWSInstancesStateWatcher");
	}

	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}

	public void run() {

		while (!mapExpectedStates.isEmpty()) {

			AWSInterfaceManager.getInstance().updateInstances();

			List<Instance> listInstances = AWSInterfaceManager.getInstance().getAllInstances();
			for (Iterator<Instance> iterator = listInstances.iterator(); iterator.hasNext();) {
				Instance instance = (Instance) iterator.next();
				if (mapExpectedStates.get(instance.getInstanceId()) != null) {
					String expectedState = mapExpectedStates.get(instance.getInstanceId());
					if (expectedState.equals(instance.getState().getName())) {
						// Notify listener that state of this instance has been
						// successfully reached
						boolean terminate = listener.expectedStateReached(instance.getInstanceId(), expectedState);
						if (terminate) {
							break;
						}
					}
				}
			}

			try {
				Thread.sleep(WAIT_FOR_TRANSITION_INTERVAL);
			} catch (InterruptedException e) {
				listener.expectedStateUnableToBeReached(null);
			}
		}

		LOG.debug("Exit thread watcher for AWS Instances state change");

	}

}
