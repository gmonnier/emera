package connection;

import com.illumina.basespace.ApiConfiguration;

import configuration.BaseSpaceContextManager;

public class BaseSpaceConfiguration implements ApiConfiguration {

	// Application development specific credentials
	private String clientSecret;

	private String clientID;

	// Illumina User specific toker
	private String accessToken;

	public BaseSpaceConfiguration() {
		clientSecret = BaseSpaceContextManager.getInstance().getConfig().getBsClientSecret();
		clientID = BaseSpaceContextManager.getInstance().getConfig().getBsClientID();
		accessToken = BaseSpaceContextManager.getInstance().getConfig().getBsAccessToken();
	}

	public BaseSpaceConfiguration(String clientSecret, String clientID, String accessToken) {
		this.clientSecret = clientSecret;
		this.clientID = clientID;
		this.accessToken = accessToken;
	}

	@Override
	public String getVersion() {
		return "v1pre3";
	}

	@Override
	public String getClientId() {
		return clientID;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	@Override
	public String getApiRootUri() {
		return "https://api.basespace.illumina.com";
	}

	@Override
	public String getAccessTokenUriFragment() {
		return "/oauthv2/token";
	}

	@Override
	public String getAuthorizationScope() {
		return "browse global";
	}

	@Override
	public String getProxyHost() {
		return null;
	}

	@Override
	public int getProxyPort() {
		return 0;
	}

	@Override
	public String getAuthorizationUriFragment() {
		return "/oauthv2/deviceauthorization";
	}

	@Override
	public String getAccessToken() {
		return accessToken;
	}

	@Override
	public int getReadTimeout() {
		return 0;
	}

	@Override
	public int getConnectionTimeout() {
		return 0;
	}

	@Override
	public String getStoreRootUri() {
		return null;
	}

}
