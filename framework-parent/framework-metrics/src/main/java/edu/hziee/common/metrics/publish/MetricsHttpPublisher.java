/**
 * 
 */
package edu.hziee.common.metrics.publish;

import java.io.IOException;
import java.io.StringWriter;
import java.net.ConnectException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.metrics.model.Record;

/**
 * @author Administrator
 * 
 */
public class MetricsHttpPublisher implements MetricsPublisher {

	private static final Logger	logger					= LoggerFactory.getLogger(MetricsHttpPublisher.class);

	private static final String	PUBLISH_URL_LOG	= "log";

	private String							publishUrl;

	private HttpClient					httpClient;

	private Marshaller					xmlMarshaller;

	public MetricsHttpPublisher() {
		httpClient = new HttpClient();
		try {
			JAXBContext context = JAXBContext.newInstance(Record.class);
			xmlMarshaller = context.createMarshaller();
		} catch (JAXBException e) {
			String error = "Failed to initialize JAXB marshaller: " + e.getMessage();
			throw new RuntimeException(error, e);
		}
	}

	@Override
	public boolean publish(Record record) {
		boolean succeed = false;

		try {

			if (xmlMarshaller != null) {
				StringWriter writer = new StringWriter();
				xmlMarshaller.marshal(record, writer);

				if (PUBLISH_URL_LOG.equals(publishUrl)) {
					logger.info(writer.toString());
					succeed = true;
				} else {
					succeed = postContent(writer.toString());
				}
			} else {
				logger.warn("Invalid XML marshaller object!");
			}
		} catch (JAXBException e) {
			// log a warning message, and skip this record
			logger.warn("Cannot convert record to XML", e);
		}
		return succeed;
	}

	/**
	 * Post the content to the HTTP server.
	 * 
	 * @param content
	 * @return
	 */
	private boolean postContent(final String content) {

		boolean succeed = false;

		try {
			PostMethod post = new PostMethod(publishUrl);

			NameValuePair[] parametersBody = { new NameValuePair("record", content) };
			post.setRequestBody(parametersBody);

			int status = httpClient.executeMethod(post);

			if (status == HttpStatus.SC_OK) {
				succeed = true;
			} else {
				logger.debug("Receive agent response " + post.getResponseBodyAsString());
			}

		} catch (ConnectException ce) {
			logger.warn("Cannot connect to agent", ce);
		} catch (HttpException he) {
			logger.warn("Publisher got HTTP exception", he);
		} catch (IOException ioe) {
			logger.warn("Publisher got IO exception", ioe);
		}
		return succeed;
	}

	public void setPublishUrl(String publishUrl) {
		this.publishUrl = publishUrl;
	}

}
