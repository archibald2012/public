/**
 * 
 */
package edu.hziee.common.lock.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import edu.hziee.common.lock.LockResult;
import edu.hziee.common.lock.LockStatus;
import edu.hziee.common.lock.LockUpdater;

/**
 * @author Administrator
 * 
 */
public class DatabaseLockUpdater implements LockUpdater {

	private static final String	RESOURCE_QUERY					= "select * from lock_resource where lock_id = ?";

	private static final String	RESOURCE_UPDATE					= "UPDATE lock_resource SET master_instance = ?, master_time = now(), updated_time = now() WHERE lock_id = ?";

	private static final String	RESOURCE_UPDATE_RELEASE	= "UPDATE lock_resource SET master_instance = null, master_time = now(), updated_time = now() WHERE lock_id = ? and master_instance = ?";

	private JdbcTemplate				jdbcTemplate;

	@Override
	public LockResult acquireLock(final String resName, final String lockInstance) {

		if (jdbcTemplate.queryForInt(RESOURCE_QUERY, new Object[] { resName }) != 1) {
			throw new RuntimeException("missing lockId " + resName);
		}

		LockResult result = new LockResult();
		result.setLockedId(resName);

		int count = (Integer) jdbcTemplate.execute(RESOURCE_UPDATE, new PreparedStatementCallback() {

			@Override
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setString(0, lockInstance);
				ps.setString(1, resName);
				return ps.executeUpdate();
			}
		});
		result.setStatus(count == 1 ? LockStatus.LOCK_ACQUIRED : LockStatus.LOCK_BLOCKED);
		return result;
	}

	@Override
	public LockResult releaseLock(final String resName, final String lockInstance) {

		if (jdbcTemplate.queryForInt(RESOURCE_QUERY, new Object[] { resName }) != 1) {
			throw new RuntimeException("missing lockId " + resName);
		}

		LockResult result = new LockResult();
		result.setLockedId(resName);

		int count = (Integer) jdbcTemplate.execute(RESOURCE_UPDATE_RELEASE, new PreparedStatementCallback() {

			@Override
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setString(0, resName);
				ps.setString(1, lockInstance);
				return ps.executeUpdate();
			}
		});
		result.setStatus(count == 1 ? LockStatus.LOCK_RELEASED : LockStatus.LOCK_BLOCKED);
		return result;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
