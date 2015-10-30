package com.kyip.app;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

public class MainApplication {
	public static void main(String[] args) throws Exception {
		String jetty_home = System.getProperty("jetty.home", "..");

		// Security.setProperty("ssl.SocketFactory.provider", "com.ibm.jsse2.SSLSocketFactoryImpl");
		// Security.setProperty("ssl.ServerSocketFactory.provider", "com.ibm.jsse2.SSLServerSocketFactoryImpl");

		// read properties file
		String configFilePath = System.getProperty("configFilePath");
		System.out.println("reading properties file:" + configFilePath);
		InputStream in = new BufferedInputStream(new FileInputStream(configFilePath));
		Properties p = new Properties();
		p.load(in);

		String warPath = (String) p.get("cas.war.path");
		String keystorePath = (String) p.get("keystore.fullPath");
		String truststorePath = (String) p.get("trustStore.fullPath");
		String keystorePassword = (String) p.get("keystore.password");
		String truststorePassword = (String) p.get("trustStore.password");
		String keyManagerPassword = (String) p.get("keymanager.password");
		String sslPortStr = (String) p.get("ssl.port");
		String portStr = (String) p.get("port");
		String useSslStr = (String) p.get("useSsl");
		int sslPort = Integer.valueOf(sslPortStr);
		int port = Integer.valueOf(portStr);
		if (StringUtils.isEmpty(truststorePath)) {
			truststorePath = keystorePath;
		}

		boolean useSSL = new Boolean(useSslStr);
		Server server = new Server();
		if (useSSL) {
			// set SSL
			HttpConfiguration https = new HttpConfiguration();
			https.addCustomizer(new SecureRequestCustomizer());

			SslContextFactory sslContextFactory = new SslContextFactory(keystorePath);
			System.setProperty("javax.net.ssl.trustStore", truststorePath);
			sslContextFactory.setKeyStorePassword(keystorePassword);
			sslContextFactory.setKeyManagerPassword(keyManagerPassword);
			sslContextFactory.setTrustStorePassword(truststorePassword);

			ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
			sslConnector.setPort(sslPort);

			server.setConnectors(new Connector[] { new ServerConnector(server), sslConnector });
		} else {
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(port);
			server.addConnector(connector);
		}

		// set webapp
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar(warPath);
		webapp.setContextPath("/abc"); // set context to access the webapp
		server.setHandler(webapp);

		server.start();
		server.join();
	}

}