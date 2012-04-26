
package edu.hziee.common.lang;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: AppInfo.java 14 2012-01-10 11:54:14Z archie $
 */
public class AppInfo {
	private static final Logger logger = LoggerFactory.getLogger(AppInfo.class);

	private String appVersion;
	private String specificationTitle;
	private String specificationVersion;
	private String specificationVendor;
	private String implementationTitle;
	private String implementationVersion;
	private String implementationVendor;

	public void setJarLocation(String location) {
		JarFile jar;
		try {
			jar = new JarFile(location);
			Manifest man = jar.getManifest();
			Attributes attrs = man.getMainAttributes();
			appVersion = attrs.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
			this.specificationTitle = attrs
					.getValue(Attributes.Name.SPECIFICATION_TITLE);
			this.specificationVersion = attrs
					.getValue(Attributes.Name.SPECIFICATION_VERSION);
			this.specificationVendor = attrs
					.getValue(Attributes.Name.SPECIFICATION_VENDOR);
			this.implementationTitle = attrs
					.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
			this.implementationVersion = attrs
					.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
			this.implementationVendor = attrs
					.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);

			logger.info("Specification-Title=[{}]", this.specificationTitle);
			logger.info("Specification-Version=[{}]", this.specificationVersion);
			logger.info("Specification-Vendor=[{}]", this.specificationVendor);
			logger.info("Implementation-Title=[{}]", this.implementationTitle);
			logger.info("Implementation-Version=[{}]",
					this.implementationVersion);
			logger.info("Implementation-Vendor=[{}]", this.implementationVendor);
		} catch (IOException e) {
			logger.error("setJarLocation:", e);
		}

	}

	/**
	 * @return the appVersion
	 */
	public String getAppVersion() {
		return appVersion;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * @return the specificationTitle
	 */
	public String getSpecificationTitle() {
		return specificationTitle;
	}

	/**
	 * @return the specificationVersion
	 */
	public String getSpecificationVersion() {
		return specificationVersion;
	}

	/**
	 * @return the specificationVendor
	 */
	public String getSpecificationVendor() {
		return specificationVendor;
	}

	/**
	 * @return the implementationTitle
	 */
	public String getImplementationTitle() {
		return implementationTitle;
	}

	/**
	 * @return the implementationVersion
	 */
	public String getImplementationVersion() {
		return implementationVersion;
	}

	/**
	 * @return the implementationVendor
	 */
	public String getImplementationVendor() {
		return implementationVendor;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public static void main(String[] args) {
		System.out.println(new AppInfo().getAppVersion());
	}
}
