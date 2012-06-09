package edu.hziee.common.test.db;

import static java.lang.String.format;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Constants;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: XDataSetTestExecutionListener.java 14 2012-01-10 11:54:14Z
 *          archie $
 */
public class XDataSetTestExecutionListener extends
		AbstractTestExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(XDataSetTestExecutionListener.class);

	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	private static final String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

	private final Map<Method, List<DatasetConfig>> datasetConfigCache = Collections
			.synchronizedMap(new IdentityHashMap<Method, List<DatasetConfig>>());

	private final static Constants databaseOperations = new Constants(
			DatabaseOperation.class);

	/**
	 * 
	 */
	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		List<DatasetConfig> datasetConfigs = getDatasetConfigs(testContext);
		if (datasetConfigs == null || datasetConfigs.size() == 0)
			return;

		datasetConfigCache.put(testContext.getTestMethod(), datasetConfigs);

		for (DatasetConfig datasetConfig : datasetConfigs) {
			if (logger.isInfoEnabled()) {
				logger.info(format(
						"Loading dataset from class path resource  '%s' using operation '%s' with dataSourceName '%s'.",
						datasetConfig.getLocation(),
						datasetConfig.getSetupOperation(),
						datasetConfig.getDsName()));
			}
			datasetConfig.getDatabaseTester().onSetup();
		}
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		List<DatasetConfig> datasetConfigs = datasetConfigCache.get(testContext
				.getTestMethod());
		if (datasetConfigs == null || datasetConfigs.size() == 0)
			return;

		for (DatasetConfig datasetConfig : datasetConfigs) {
			if (logger.isInfoEnabled()) {
				logger.info(format("Tearing down dataset using operation '%s'",
						datasetConfig.getTeardownOperation()));
			}

			datasetConfig.getDatabaseTester().onTearDown();
			if (!datasetConfig.isTransactional()) {
				try {
					datasetConfig.getDatabaseTester().getConnection()
							.getConnection().close();
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}

		datasetConfigCache.remove(testContext.getTestMethod());
	}

	protected List<DatasetConfig> getDatasetConfigs(TestContext testContext)
			throws Exception {
		XDataSet annotation = findAnnotation(testContext.getTestClass(),
				testContext.getTestMethod());
		if (annotation == null)
			return null;

		String[] locations = determineLocations(testContext, annotation);

		String[] dsNames = determineDsNames(testContext, annotation);

		if (dsNames.length > 1 && locations.length != dsNames.length) {
			String errorMsg = format(
					"dsNames number '%s' does'nt matchs the locations number '%s'.",
					dsNames.length, locations.length);
			logger.error(errorMsg);
			throw new IllegalStateException(errorMsg);
		}

		List<DatasetConfig> datasetConfigs = Collections
				.synchronizedList(new LinkedList<DatasetConfig>());

		for (int i = 0; i < locations.length; i++) {
			String location = locations[i];
			String dsName = dsNames.length == 1 ? dsNames[0] : dsNames[i];

			// build dataSet begin
			ReplacementDataSet dataSet;

			if (location.endsWith(".xls")) {
				dataSet = new ReplacementDataSet(new XlsDataSet(
						new DefaultResourceLoader().getResource(location)
								.getInputStream()));
			} else if (location.endsWith(".xml")) {

				dataSet = new ReplacementDataSet(new FlatXmlDataSet(
						new DefaultResourceLoader().getResource(location)
								.getInputStream()));
			} else {
				String errorMsg = format(
						"Unsupported file type,file '%s' must be xls or xml.",
						location);
				logger.error(errorMsg);
				throw new IllegalStateException(errorMsg);
			}
			dataSet.addReplacementObject("[NULL]", null);
			// build dataSet end

			DataSource dataSource = (DataSource) testContext
					.getApplicationContext().getBean(dsName);
			Connection connection = DataSourceUtils.getConnection(dataSource);

			// build databaseTester start
			IDatabaseConnection Iconn = getDatabaseConnection(dataSource,
					connection);

			IDatabaseTester databaseTester = new DefaultDatabaseTester(Iconn);
			databaseTester.setDataSet(dataSet);
			databaseTester
					.setSetUpOperation((DatabaseOperation) databaseOperations
							.asObject(annotation.setupOperation()));
			databaseTester
					.setTearDownOperation((DatabaseOperation) databaseOperations
							.asObject(annotation.teardownOperation()));

			// build databaseTester end

			boolean transactional = DataSourceUtils.isConnectionTransactional(
					connection, dataSource);
			DatasetConfig datasetConfig = new DatasetConfig(databaseTester,
					transactional).location(location).dsName(dsName)
					.setupOperation(annotation.setupOperation())
					.teardownOperation(annotation.teardownOperation());

			datasetConfigs.add(datasetConfig);
		}

		return datasetConfigs;
	}

	private String[] determineLocations(TestContext testContext,
			XDataSet annotation) {
		Class<?> testClass = testContext.getTestClass();
		String fileType = annotation.fileType();
		String[] locations = annotation.locations();
		if (locations.length == 0) {// user undefined,using default location
			String location = CLASSPATH_URL_PREFIX
					+ ClassUtils.getQualifiedName(testClass).replace('.', '/')
					+ "." + fileType;
			locations = new String[] { location };
		} else {
			for (int i = 0; i < locations.length; i++) {
				String location = locations[i];
				if (!location.contains(CLASSPATH_URL_PREFIX)) {
					if (!location.startsWith("/")) {
						location = ClassUtils.getPackageName(testClass)
								.replace('.', '/') + "/" + location;
					}
					locations[i] = CLASSPATH_URL_PREFIX + location;
				}
			}
		}
		return locations;
	}

	private String[] determineDsNames(TestContext testContext,
			XDataSet annotation) {
		String[] dsNames = annotation.dsNames();
		if (dsNames.length == 0) {
			// user undefined,look up default dataSource
			String[] defaultDsNames = testContext.getApplicationContext()
					.getBeanNamesForType(DataSource.class);
			if (defaultDsNames.length != 1) {
				String errorMsg = "A single,unambiguous DataSource must be defined.";
				logger.error(errorMsg);
				throw new IllegalStateException(errorMsg);
			}
			dsNames = new String[] { defaultDsNames[0] };
		}

		return dsNames;
	}

	private DatabaseConnection getDatabaseConnection(DataSource dataSource,
			Connection connection) throws DatabaseUnitException {
		return new DatabaseConnection(connection) {
			public void close() throws SQLException {

			}
		};
	}

	private XDataSet findAnnotation(Class<?> testClass, Method testMethod) {
		XDataSet annotation = (XDataSet) AnnotationUtils.findAnnotation(
				testMethod, XDataSet.class);
		if (annotation == null) {
			annotation = (XDataSet) AnnotationUtils.findAnnotation(testClass,
					XDataSet.class);
		}
		return annotation;
	}
}
