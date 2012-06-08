/**
 * 
 */
package edu.hziee.common.jmx;

import java.io.FileReader;
import java.io.IOException;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.Interpreter;

public class JMXStatusMain {

	private static final Logger	logger	= LoggerFactory.getLogger(JMXStatusMain.class);

	public static void main(String[] args) {

		// create the Options
		Options options = new Options();
		OptionBuilder.withArgName("url");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("JMX Service URL string .");
		options.addOption(OptionBuilder.create("jmxurl"));

		OptionBuilder.withArgName("filename");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("BeanShell script filename which create JMXClosure instance.");
		options.addOption(OptionBuilder.create("script"));

		String bsh = null;
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

			if (line.hasOption("script")) {
				// print the value of block-size
				bsh = line.getOptionValue("script");
			}

			if (null == url || null == bsh) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("status", options);
				System.exit(-1);
			}
		} catch (ParseException exp) {
			logger.error("Unexpected exception:" + exp.getMessage(), exp);
			System.exit(-1);
		}

		JMXConnector jmxc = null;
		MBeanServerConnection mbsc = null;

		try {
			JMXServiceURL serviceUrl = new JMXServiceURL(url);

			jmxc = JMXConnectorFactory.connect(serviceUrl, null);

			mbsc = jmxc.getMBeanServerConnection();

			Interpreter inter = new Interpreter();
			JMXClosure closure = (JMXClosure) inter.eval(new FileReader(bsh));
			if (null != closure) {
				closure.execute(mbsc);
			}
		} catch (Exception e) {
			logger.error("Maybe server not running, Error in get status via jmx:", e);
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
}
