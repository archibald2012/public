/**
 * 
 */
package edu.hziee.common.jmx;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.Interpreter;
import edu.hziee.common.lang.PackageUtil;

public class JMXInvoker {

	private static final Logger	logger	= LoggerFactory.getLogger(JMXInvoker.class);

	private String				cmdLocations[];

	private String searchBsh(String cmd) {
		for (String location : this.cmdLocations) {
			try {
				String[] allRes = PackageUtil.getResourceInPackage(location);
				for (String res : allRes) {
					if (logger.isTraceEnabled()) {
						logger.trace("found resource: {}", res);
					}
					if (res.endsWith("." + cmd + ".bsh")) {
						String bshFullPath = StringUtils.substringBefore(res, cmd + ".bsh").replace('.', '/') + cmd
								+ ".bsh";

						if (logger.isDebugEnabled()) {
							logger.debug("found matched beanshell cmd [{}], impl: {}", cmd, bshFullPath);
						}

						return bshFullPath;
					}
				}
			} catch (IOException e) {
				logger.error("invalid search path {}", location, e);
			}
		}

		return null;
	}

	/**
	 * @param args
	 */
	public void executeWith(String[] args) {

		// create the Options
		Options options = new Options();
		OptionBuilder.withArgName("url");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("JMX Service URL string .");
		options.addOption(OptionBuilder.create("jmxurl"));

		OptionBuilder.withArgName("name");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("name of command, which create JMXClosure instance, BeanShell script impl.");
		options.addOption(OptionBuilder.create("cmd"));

		String cmd = null;
		String url = null;

		try {
			// create the command line parser
			CommandLineParser parser = new GnuParser();

			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("jmxurl")) {
				// print the value of block-size
				url = line.getOptionValue("jmxurl");
			}

			if (line.hasOption("cmd")) {
				// print the value of block-size
				cmd = line.getOptionValue("cmd");
			}

			if (null == url || null == cmd) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("jmxInvoker", options);
				return;
			}
		} catch (ParseException exp) {
			logger.error("Unexpected exception:" + exp.getMessage());
			return;
		}

		String bshFullPath = searchBsh(cmd);
		if (null == bshFullPath) {
			logger.error("can't found cmd[{}] impl file,just ignore.", cmd);
			return;
		}

		JMXConnector jmxc = null;
		MBeanServerConnection mbsc = null;

		try {
			JMXServiceURL serviceUrl = new JMXServiceURL(url);

			jmxc = JMXConnectorFactory.connect(serviceUrl, null);

			mbsc = jmxc.getMBeanServerConnection();

			Interpreter inter = new Interpreter();

			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(bshFullPath);

			JMXClosure closure = (JMXClosure) inter.eval(new InputStreamReader(is));
			if (null != closure) {
				closure.execute(mbsc);
			}
		} catch (Exception e) {
			logger.error("", e);
			System.out.println("Maybe server not running, Error in get status via jmx:" + e);
		} finally {
			if (null != jmxc) {
				try {
					jmxc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				jmxc = null;
			}
		}

	}

	/**
	 * @return the cmdLocations
	 */
	public String[] getCmdLocations() {
		return cmdLocations;
	}

	/**
	 * @param cmdLocations
	 *            the cmdLocations to set
	 */
	public void setCmdLocations(String[] cmdLocations) {
		this.cmdLocations = cmdLocations;
	}

}
