/**
 * 
 */
package edu.hziee.common.lang;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author Administrator
 * 
 */
public class SystemUtil {

	private static final Logger		logger									= LoggerFactory.getLogger(SystemUtil.class);

	private static final long			MILLIS_TO_SECONDS				= 1000L;
	private static final String		DEFAULT_LOCAL_IPADDRESS	= "127.0.0.1";
	private static final String		DEFAULT_LOCAL_HOSTNAME	= "localhost";
	private static final String		DEFAULT_PID							= "0";
	private static final String		DEFAULT_USER_NAME				= "user";
	private static final Pattern	DASH_PATTERN						= Pattern.compile("-");

	private SystemUtil() {
	}

	public static long millisToSeconds(long millisTime) {
		return millisTime / MILLIS_TO_SECONDS;
	}

	public static List<String> splitString(String text, String delimiter) {
		List<String> list = new ArrayList<String>();
		int from = 0;
		int index;
		while ((index = text.indexOf(delimiter, from)) >= 0) {
			list.add(text.substring(from, index));
			from = ++index;
		}
		list.add(text.substring(from).trim());
		return list;
	}

	public static void mergeProperties(Properties target, Properties source) {
		Set<Entry<Object, Object>> entrySet = source.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			target.put(entry.getKey(), entry.getValue());
		}
	}

	public static Properties getSubProperties(Properties properties, String basePath) {
		Properties subset = new Properties();
		int length = basePath.length();

		Set<Entry<Object, Object>> entrySet = properties.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			String key = entry.getKey().toString();
			if (key.startsWith(basePath)) {
				key = key.substring(length);
				if (key.length() > 0) {
					subset.put(key, entry.getValue());
				}
			}
		}

		return subset;
	}

	public static boolean isResourceExist(String resourceName) {
		InputStream inputStream = null;
		try {
			inputStream = getClassLoader().getResourceAsStream(resourceName);
			return (inputStream == null) ? false : true;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	public static Properties retrieveFileProperties(InputStream inputStream) {
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
		} catch (InvalidPropertiesFormatException e) {
			String error = "Invalid properties format with error " + e.getMessage();
			logger.error(error, e);
			throw new RuntimeException(error, e);
		} catch (IOException e) {
			String error = "Unable to load properties with error " + e.getMessage();
			logger.error(error, e);
			throw new RuntimeException(error, e);
		}

		return properties;
	}

	public static Properties retrieveXmlProperties(InputStream inputStream) {
		Properties properties = new Properties();
		try {
			properties.loadFromXML(inputStream);
		} catch (InvalidPropertiesFormatException e) {
			String error = "Invalid XML properties format with error " + e.getMessage();
			logger.error(error, e);
			throw new RuntimeException(error, e);
		} catch (IOException e) {
			String error = "Unable to load XML properties with error " + e.getMessage();
			logger.error(error, e);
			throw new RuntimeException(error, e);
		}

		return properties;
	}

	public static Properties retrieveSystemProperties() {
		return System.getProperties();
	}

	public static Document retrieveXmlDocument(InputStream inputStream) {
		Document document = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = builder.parse(inputStream);
		} catch (Exception e) {
			String error = "Unable to parse XML configuration with error " + e.getMessage();
			logger.error(error, e);
			throw new RuntimeException(error, e);
		}

		return document;
	}

	public static String getDocumentPath(Node node) {
		StringBuilder builder = new StringBuilder(node.getNodeName());

		if (node instanceof Attr) {
			node = ((Attr) node).getOwnerElement();
			builder.insert(0, node.getNodeName() + ".");
		}

		while (node.getParentNode() != null) {
			node = node.getParentNode();
			if (node instanceof Document) {
				break;
			}
			builder.insert(0, node.getNodeName() + ".");
		}
		return builder.toString();
	}

	public static InputStream getInputStream(String filename) {
		InputStream inputStream = getClassLoader().getResourceAsStream(filename);
		if (inputStream == null) {
			String error = "Unable to locate file " + filename;
			logger.error(error);
			throw new RuntimeException(error);
		}
		return inputStream;
	}

	public static ClassLoader getClassLoader() {
		ClassLoader loader = SystemUtil.class.getClassLoader();
		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}
		return loader;
	}

	public static InputStream getInputStream(URL url) {
		try {
			URLConnection urlConnection = url.openConnection();
			InputStream inputStream = urlConnection.getInputStream();
			if (inputStream == null) {
				String error = "Unable to locate URL " + url;
				logger.error(error);
				throw new RuntimeException(error);
			}
			return inputStream;
		} catch (IOException e) {
			String error = "Unable to retrieve file with error " + e.getMessage();
			logger.error(error, e);
			throw new RuntimeException(error, e);
		}
	}

	public static void logProperties(Properties properties) {
		Set<Entry<Object, Object>> entrySet = properties.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			logger.debug("property " + entry.getKey() + " value " + entry.getValue());
		}
	}

	public static Schema loadSchema(String filename) {
		InputStream inputStream = null;

		try {
			inputStream = SystemUtil.getInputStream(filename);
			StreamSource source = new StreamSource(inputStream);

			// create schema
			SchemaFactory xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = xsdFactory.newSchema(source);
			return schema;
		} catch (SAXException e) {
			String error = "Unable to schema " + filename + " with error " + e.getMessage();
			logger.error(error);
			throw new RuntimeException(error, e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ignore) {

				}
			}
		}
	}

	public static String createGuid() {
		return DASH_PATTERN.matcher(UUID.randomUUID().toString()).replaceAll("");
	}

	private static String					ipAddress;
	private static ReentrantLock	ipAddressLock	= new ReentrantLock();

	public static String getIpAddress() {
		if (ipAddress == null) {
			ipAddressLock.lock();
			try {
				if (ipAddress == null) {
					try {
						InetAddress address = InetAddress.getLocalHost();
						ipAddress = address.getHostAddress();
					} catch (UnknownHostException e) {
						logger.warn("Unable to get hostAddress with error " + e.getMessage(), e);
						ipAddress = DEFAULT_LOCAL_IPADDRESS;
					}
				}
			} finally {
				ipAddressLock.unlock();
			}
		}
		return ipAddress;
	}

	private static String					pid;
	private static ReentrantLock	pidLock	= new ReentrantLock();

	public static String getPid() {
		if (pid == null) {
			pidLock.lock();
			try {
				if (pid == null) {
					try {
						pid = DEFAULT_PID;
						RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
						if (runtime != null) {
							String name = runtime.getName();
							int index = name.indexOf('@');
							if (index > 0) {
								pid = name.substring(0, index);
							}
						}
					} catch (Exception e) {
						logger.warn("Unable to get runtimeName with error " + e.getMessage(), e);
					}
				}
			} finally {
				pidLock.unlock();
			}
		}
		return pid;
	}

	private static String					hostName;
	private static ReentrantLock	hostNameLock	= new ReentrantLock();

	public static String getHostName() {
		if (hostName == null) {
			hostNameLock.lock();
			try {
				if (hostName == null) {
					try {
						InetAddress address = InetAddress.getLocalHost();
						hostName = address.getHostName();
					} catch (UnknownHostException e) {
						logger.warn("Unable to resolve hostname with error " + e.getMessage(), e);
						hostName = DEFAULT_LOCAL_HOSTNAME;
					}
				}
			} finally {
				hostNameLock.unlock();
			}
		}
		return hostName;
	}

	private static String					userName;
	private static ReentrantLock	userNameLock	= new ReentrantLock();

	public static String getUserName() {
		if (userName == null) {
			userNameLock.lock();
			try {
				userName = System.getProperty("user.name");
				if (userName == null) {
					userName = DEFAULT_USER_NAME;
				}
			} finally {
				userNameLock.unlock();
			}
		}
		return userName;
	}
}
