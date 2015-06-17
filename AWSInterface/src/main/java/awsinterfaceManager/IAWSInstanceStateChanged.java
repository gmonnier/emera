package awsinterfaceManager;


public interface IAWSInstanceStateChanged {

	public boolean expectedStateReached(String string, String state);
	
	public void expectedStateUnableToBeReached(String string);
	
}
