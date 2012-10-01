package edu.hziee.common.metrics.publish.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;

import edu.hziee.common.lang.NameValue;
import edu.hziee.common.metrics.model.Aggregation;
import edu.hziee.common.metrics.model.Bucket;
import edu.hziee.common.metrics.model.CollectorUsage;
import edu.hziee.common.metrics.model.HeapUsage;
import edu.hziee.common.metrics.model.Measurement;
import edu.hziee.common.metrics.model.Record;
import edu.hziee.common.metrics.model.ResourceUsage;
import edu.hziee.common.metrics.model.ThreadUsage;

/**
 * For saving performance measurements and aggregations into database.
 * 
 * @author Administrator
 * 
 */
public class DefaultMetricsDao {

	private static final String	INSERT_METRICS_RECORD					= "";

	private static final String	INSERT_MEASUREMENT						= "";

	private static final String	INSERT_MEASUREMENT_METRICS		= "";

	private static final String	INSERT_AGGREGATION						= "";

	private static final String	INSERT_AGGREGATION_BUCKET			= "";

	private static final String	INSERT_USAGE_RUNTIME					= "";

	private static final String	INSERT_USAGE_THREAD						= "";

	private static final String	INSERT_USAGE_HEAP							= "";

	private static final String	INSERT_USAGE_COLLECTOR				= "";

	private static final String	LOAD_METRICS_BUCKET_BY_AGGRE	= "";

	private static final String	LOAD_DISTINCT_HOST						= "";

	private static final String	LOAD_AGGREGATIONS_BY_HOST			= "";

	private JdbcTemplate				jdbcTemplate;

	public int saveRecord(final Record record) {
		int count = jdbcTemplate.update(INSERT_METRICS_RECORD, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, record.getId());
				ps.setString(2, record.getApplicationName());
				ps.setString(3, record.getDomain());
				ps.setString(4, record.getHost());
				ps.setString(5, record.getFrameworkName());
				ps.setString(6, record.getVersion());
				ps.setString(7, record.getUser());
				ps.setString(8, record.getPid());
				ps.setString(9, record.getAggregationRanges());
			}
		});
		if (count != 0) {
			saveMeasurements(record.getId(), record.getMeasurementList());
			saveAggregations(record.getId(), record.getAggregationList());
			saveRuntimeUsages(record.getId(), record.getUsageList());
		}
		return count;
	}

	/**
	 * Use batch mechanism to save all measurements.
	 * 
	 * @param recordId
	 * @param measurementList
	 * @return
	 */
	public int saveMeasurements(final String recordId, final List<Measurement> measurementList) {
		int count = 0;

		if (!measurementList.isEmpty()) {
			int batchSize = 500;
			int[][] counts = jdbcTemplate.batchUpdate(INSERT_MEASUREMENT, measurementList, batchSize,
					new ParameterizedPreparedStatementSetter<Measurement>() {

						@Override
						public void setValues(PreparedStatement ps, Measurement argument) throws SQLException {
							ps.setString(1, recordId);
							ps.setString(2, argument.getId());
							ps.setString(3, argument.getParentId());
							ps.setString(4, argument.getCorrelationId());
							ps.setString(5, argument.getCorrelationRequester());
							ps.setString(6, argument.getComponentName());
							ps.setString(7, argument.getFunctionName());
							ps.setString(8, argument.getThreadName());
							ps.setString(9, argument.getUser());
							ps.setTimestamp(10, new Timestamp(argument.getTimestamp().getTime()));
							ps.setLong(11, argument.getDuration());
							ps.setLong(12, argument.getWorkUnits());
							ps.setLong(13, argument.getCreateOrder());
							ps.setBoolean(14, argument.getFailStatus());
						}

					});

			if (counts != null) {
				for (int[] result : counts) {
					for (int rowAffected : result) {
						count += rowAffected;
					}
				}

				List<MeasurementMetrics> metricsList = new ArrayList<MeasurementMetrics>();
				for (Measurement measurement : measurementList) {
					for (NameValue metrics : measurement.getMetricsList()) {
						metricsList.add(new MeasurementMetrics(measurement.getId(), metrics));
					}
				}
				saveMeasurementMetrics(metricsList);
			}
		}
		return count;
	}

	private int saveMeasurementMetrics(final List<MeasurementMetrics> metricsList) {
		int count = 0;
		if (!metricsList.isEmpty()) {
			int batchSize = 500;
			int[][] counts = jdbcTemplate.batchUpdate(INSERT_MEASUREMENT_METRICS, metricsList, batchSize,
					new ParameterizedPreparedStatementSetter<MeasurementMetrics>() {

						@Override
						public void setValues(PreparedStatement ps, MeasurementMetrics argument) throws SQLException {
							ps.setString(1, argument.getMeasurementId());
							ps.setString(2, argument.getMetrics().getName());
							ps.setString(3, argument.getMetrics().getValue());
						}

					});
			if (counts != null) {
				for (int[] result : counts) {
					for (int rowAffected : result) {
						count += rowAffected;
					}
				}
			}
		}
		return count;
	}

	public int saveAggregations(final String recordId, final List<Aggregation> aggregationList) {
		int count = 0;
		if (!aggregationList.isEmpty()) {
			int batchSize = 500;
			int[][] counts = jdbcTemplate.batchUpdate(INSERT_AGGREGATION, aggregationList, batchSize,
					new ParameterizedPreparedStatementSetter<Aggregation>() {

						@Override
						public void setValues(PreparedStatement ps, Aggregation argument) throws SQLException {
							ps.setString(1, recordId);
							ps.setString(2, argument.getId());
							ps.setString(3, argument.getComponentName());
							ps.setString(4, argument.getFunctionName());
							ps.setTimestamp(5, new Timestamp(argument.getStartTime().getTime()));
							ps.setLong(6, argument.getDuration());
							ps.setLong(7, argument.getMaximum());
							ps.setLong(8, argument.getMinimum());
							ps.setDouble(9, argument.getAverage());
							ps.setLong(10, argument.getUnitMaximum());
							ps.setLong(11, argument.getUnitMinimum());
							ps.setDouble(12, argument.getUnitAverage());
							ps.setLong(13, argument.getCount());
						}

					});
			if (counts != null) {
				for (int[] result : counts) {
					for (int rowAffected : result) {
						count += rowAffected;
					}
				}

				List<AggregationBucket> bucketList = new ArrayList<AggregationBucket>();
				for (Aggregation aggregation : aggregationList) {
					for (Bucket bucket : aggregation.getBucketList()) {
						bucketList.add(new AggregationBucket(aggregation.getId(), bucket));
					}
				}
				saveAggregationBuckets(bucketList);
			}

		}
		return count;
	}

	private int saveAggregationBuckets(final List<AggregationBucket> bucketList) {
		int count = 0;
		if (!bucketList.isEmpty()) {
			int batchSize = 500;
			int[][] counts = jdbcTemplate.batchUpdate(INSERT_AGGREGATION_BUCKET, bucketList, batchSize,
					new ParameterizedPreparedStatementSetter<AggregationBucket>() {

						@Override
						public void setValues(PreparedStatement ps, AggregationBucket argument) throws SQLException {
							ps.setString(1, argument.getAggregationId());
							ps.setLong(2, argument.getBucket().getStartRange());
							ps.setLong(3, argument.getBucket().getCount());
							ps.setLong(4, argument.getBucket().getUnitCount());
						}

					});
			if (counts != null) {
				for (int[] result : counts) {
					for (int rowAffected : result) {
						count += rowAffected;
					}
				}
			}

		}
		return count;
	}

	public int saveRuntimeUsages(final String recordId, final List<ResourceUsage> usageList) {
		int count = 0;

		if (!usageList.isEmpty()) {
			int batchSize = 500;
			int[][] counts = jdbcTemplate.batchUpdate(INSERT_USAGE_RUNTIME, usageList, batchSize,
					new ParameterizedPreparedStatementSetter<ResourceUsage>() {

						@Override
						public void setValues(PreparedStatement ps, ResourceUsage argument) throws SQLException {
							ps.setString(1, recordId);
							ps.setString(2, argument.getUsageId());
							ps.setTimestamp(3, new Timestamp(argument.getCheckTime().getTime()));
							ps.setLong(4, argument.getProcessorCount());
							ps.setLong(5, argument.getThreadCount());
							ps.setLong(6, argument.getUpTime());
							ps.setLong(7, argument.getCpuTime());
							ps.setLong(8, argument.getUserTime());
							ps.setLong(9, argument.getHeapMax());
							ps.setLong(10, argument.getHeapUsed());
							ps.setLong(11, argument.getNonHeapMax());
							ps.setLong(12, argument.getNonHeapUsed());
						}

					});
			if (counts != null) {
				for (int[] result : counts) {
					for (int rowAffected : result) {
						count += rowAffected;
					}
				}

				List<ThreadUsage> threadUsageList = new ArrayList<ThreadUsage>();
				List<HeapUsage> heapUsageList = new ArrayList<HeapUsage>();
				List<CollectorUsage> collectorUsageList = new ArrayList<CollectorUsage>();

				for (ResourceUsage usage : usageList) {
					// make sure all entries have the resource IDs
					List<ThreadUsage> threadList = usage.getThreadList();
					if (!threadList.isEmpty()) {
						for (ThreadUsage threadUsage : threadList) {
							threadUsage.setUsageId(usage.getUsageId());
						}
						threadUsageList.addAll(threadList);
					}
					List<HeapUsage> heapList = usage.getHeapList();
					if (!heapList.isEmpty()) {
						for (HeapUsage heapUsage : heapList) {
							heapUsage.setUsageId(usage.getUsageId());
						}
						heapUsageList.addAll(heapList);
					}
					List<CollectorUsage> collectorList = usage.getCollectorList();
					if (!collectorList.isEmpty()) {
						for (CollectorUsage collectorUsage : collectorList) {
							collectorUsage.setUsageId(usage.getUsageId());
						}
						collectorUsageList.addAll(collectorList);
					}
				}

				if (!threadUsageList.isEmpty()) {
					saveRuntimeThreadUsage(threadUsageList);
				}

				if (!heapUsageList.isEmpty()) {
					saveRuntimeHeapUsage(heapUsageList);
				}

				if (!collectorUsageList.isEmpty()) {
					saveRuntimeCollectorUsage(collectorUsageList);
				}
			}
		}
		return count;
	}

	private int saveRuntimeThreadUsage(final List<ThreadUsage> threadUsageList) {
		int count = 0;
		if (!threadUsageList.isEmpty()) {
			int batchSize = 500;
			int[][] counts = jdbcTemplate.batchUpdate(INSERT_USAGE_THREAD, threadUsageList, batchSize,
					new ParameterizedPreparedStatementSetter<ThreadUsage>() {

						@Override
						public void setValues(PreparedStatement ps, ThreadUsage argument) throws SQLException {
							ps.setString(1, argument.getUsageId());
							ps.setString(2, argument.getName());
							ps.setString(3, argument.getState());
							ps.setLong(4, argument.getCpuTime());
							ps.setLong(5, argument.getUserTime());
						}

					});
			if (counts != null) {
				for (int[] result : counts) {
					for (int rowAffected : result) {
						count += rowAffected;
					}
				}
			}
		}
		return count;
	}

	private int saveRuntimeHeapUsage(final List<HeapUsage> heapUsageList) {
		int count = 0;
		if (!heapUsageList.isEmpty()) {
			int batchSize = 500;
			int[][] counts = jdbcTemplate.batchUpdate(INSERT_USAGE_HEAP, heapUsageList, batchSize,
					new ParameterizedPreparedStatementSetter<HeapUsage>() {

						@Override
						public void setValues(PreparedStatement ps, HeapUsage argument) throws SQLException {
							ps.setString(1, argument.getUsageId());
							ps.setString(2, argument.getName());
							ps.setLong(3, argument.getMax());
							ps.setLong(4, argument.getUsed());
						}

					});
			if (counts != null) {
				for (int[] result : counts) {
					for (int rowAffected : result) {
						count += rowAffected;
					}
				}
			}
		}
		return count;
	}

	private int saveRuntimeCollectorUsage(final List<CollectorUsage> collectorUsageList) {
		int count = 0;
		if (!collectorUsageList.isEmpty()) {
			int batchSize = 500;
			int[][] counts = jdbcTemplate.batchUpdate(INSERT_USAGE_COLLECTOR, collectorUsageList, batchSize,
					new ParameterizedPreparedStatementSetter<CollectorUsage>() {

						@Override
						public void setValues(PreparedStatement ps, CollectorUsage argument) throws SQLException {
							ps.setString(1, argument.getUsageId());
							ps.setString(2, argument.getName());
							ps.setLong(3, argument.getCount());
							ps.setLong(4, argument.getTime());
						}

					});
			if (counts != null) {
				for (int[] result : counts) {
					for (int rowAffected : result) {
						count += rowAffected;
					}
				}
			}
		}
		return count;
	}

	public List<Bucket> getBucketsByAggregationId(String aggregationId) {
		List<Bucket> result = jdbcTemplate.queryForList(LOAD_METRICS_BUCKET_BY_AGGRE, Bucket.class, aggregationId);
		return result == null ? new ArrayList<Bucket>() : result;
	}

	public List<Record> getDistinctHost() {
		List<Record> result = jdbcTemplate.queryForList(LOAD_DISTINCT_HOST, Record.class);
		return result == null ? new ArrayList<Record>() : result;
	}

	public List<Aggregation> getAggregationsByHost(Record hostInfo, Date startDate, Date endDate) {
		List<Aggregation> result = jdbcTemplate.queryForList(
				LOAD_AGGREGATIONS_BY_HOST,
				Aggregation.class,
				new Object[] { hostInfo.getApplicationName(), hostInfo.getDomain(), hostInfo.getHost(),
						hostInfo.getFrameworkName(), hostInfo.getVersion(), hostInfo.getUser(), hostInfo.getPid(), startDate,
						endDate });
		return result == null ? new ArrayList<Aggregation>() : result;
	}
}

class MeasurementMetrics {
	private String		measurementId;
	private NameValue	metrics;

	public MeasurementMetrics(String measurementId, NameValue metrics) {
		this.measurementId = measurementId;
		this.metrics = metrics;
	}

	public String getMeasurementId() {
		return measurementId;
	}

	public void setMeasurementId(String measurementId) {
		this.measurementId = measurementId;
	}

	public NameValue getMetrics() {
		return metrics;
	}

	public void setMetrics(NameValue metrics) {
		this.metrics = metrics;
	}

}

class AggregationBucket {
	private String	aggregationId;
	private Bucket	bucket;

	public AggregationBucket(String aggregationId, Bucket bucket) {
		this.aggregationId = aggregationId;
		this.bucket = bucket;
	}

	public String getAggregationId() {
		return aggregationId;
	}

	public void setAggregationId(String aggregationId) {
		this.aggregationId = aggregationId;
	}

	public Bucket getBucket() {
		return bucket;
	}

	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}

}
