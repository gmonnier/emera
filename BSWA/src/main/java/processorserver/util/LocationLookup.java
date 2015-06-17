package processorserver.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;

public class LocationLookup {

	public static final File lookupFile = new File("conf/geoloc/GeoLite2-City.mmdb");

	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	public static ClientLocation getLocation(InetAddress ipAddress) {

		if (!lookupFile.exists()) {
			LOG.error("Lookup file for client geolocalisation does not exist " + lookupFile.getAbsolutePath());
			return null;
		}

		ClientLocation clientLoc = null;

		try {

			clientLoc = new ClientLocation();
			DatabaseReader reader = new DatabaseReader.Builder(lookupFile).build();
			CityResponse response = reader.city(ipAddress);

			City city = response.getCity();
			Location location = response.getLocation();

			Country country = response.getCountry();

			clientLoc.setLongitude(location.getLongitude());
			clientLoc.setLatitude(location.getLatitude());
			clientLoc.setCityName(city.getName());
			clientLoc.setCountryName(country.getName());
			clientLoc.setExternalIP(ipAddress.getHostAddress());

			LOG.error("Location extracted from database for " + ipAddress.getHostAddress());

		} catch (Throwable e) {
			if (e instanceof AddressNotFoundException) {
				LOG.warn("Adress is not in the database");

				URL externalIPService;
				BufferedReader in = null;
				try {
					externalIPService = new URL("http://checkip.amazonaws.com");
					in = new BufferedReader(new InputStreamReader(externalIPService.openStream()));

					String ip = in.readLine();
					
					InetAddress address = InetAddress.getByName(ip);
					
					// We found a different external IP adress
					if (!ipAddress.getHostAddress().equals(address.getHostAddress())) {
						LOG.warn("Try to get external IP location (if any) for " + address.getHostAddress());
						return getLocation(address);
					}
				} catch (MalformedURLException e3) {
					LOG.error("Unable to retrieve extarnal IP adress, URL is not correct");
				} catch (IOException e2) {
					LOG.error("IOException while trying to access external IP adress service.");
				} finally {
					try {
						if (in != null)
							in.close();
					} catch (IOException e1) {
						LOG.error("Unable to close ip service reader stream");
					}
				}
			} else {
				LOG.error("An exception occured while trying to geolocalized ip", e);
			}
		}

		return clientLoc;

	}

	public static ClientLocation getLocation(String distantID) {
		try {
			InetAddress adress = InetAddress.getByName(distantID);
			return getLocation(adress);
		} catch (UnknownHostException e) {
			LOG.error("Unable to find location of adress : " + distantID, e);
		}
		return null;
	}
}
