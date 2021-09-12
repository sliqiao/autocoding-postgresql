package com.autocoding.org.apache.commons.lang3;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.junit.Test;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoIp2Test {
	@Test
	public void 香港IP测试() throws IOException, GeoIp2Exception {
		final String ip = "202.181.198.100";
		final String dbLocation = "I:\\GeoLite2\\GeoLite2-City.mmdb";

		final File database = new File(dbLocation);
		final DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

		final InetAddress ipAddress = InetAddress.getByName(ip);
		final CityResponse response = dbReader.city(ipAddress);

		final String countryName = response.getCountry().getName();
		final String cityName = response.getCity().getName();
		final String postal = response.getPostal().getCode();
		final String state = response.getLeastSpecificSubdivision().getName();
		GeoIp2Test.log.info("countryName:{},cityName:{},postal:{},state:{},", countryName, cityName,
				postal, state);
	}

	@Test
	public void 中国大陆IP测试() throws IOException, GeoIp2Exception {
		final String ip = "120.25.25.78";
		final String dbLocation = "I:\\GeoLite2\\GeoLite2-City.mmdb";

		final File database = new File(dbLocation);
		final DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

		final InetAddress ipAddress = InetAddress.getByName(ip);
		final CityResponse response = dbReader.city(ipAddress);

		final String countryName = response.getCountry().getName();
		final String cityName = response.getCity().getName();
		final String postal = response.getPostal().getCode();
		final String state = response.getLeastSpecificSubdivision().getName();
		GeoIp2Test.log.info("countryName:{},cityName:{},postal:{},state:{},", countryName, cityName,
				postal, state);
	}
}
