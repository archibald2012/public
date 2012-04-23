/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    DirSnapshot.java
 * Creator:     wangqi
 * Create-Date: 2011-9-14 上午10:54:50
 *******************************************************************************/
package edu.hziee.common.dir;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DirSnapshot.java 14 2012-01-10 11:54:14Z archie $
 */
public class DirSnapshot {
	private static final Logger logger = LoggerFactory
			.getLogger(DirSnapshot.class);

	private Map<String, Long> fileInfos = new HashMap<String, Long>();

	public DirSnapshot(String dirName, final String[] includeSuffixs) {
		File dir = new File(dirName);
		if (dir.isDirectory()) {
			//           lastModified = dir.lastModified();

			File[] includes;
			if (null != includeSuffixs) {
				includes = dir.listFiles(new FilenameFilter() {

					public boolean accept(File dir, String name) {
						for (String suffix : includeSuffixs) {
							if (name.endsWith(suffix)) {
								return true;
							}
						}
						return false;
					}
				});
			} else {
				includes = dir.listFiles();
			}

			for (File file : includes) {
				fileInfos.put(file.getAbsolutePath(),
						new Long(file.lastModified()));
			}
		} else {
			logger.error("DirSnapshot: [" + dirName + "] is !NOT! directory.");
			throw new RuntimeException("[" + dirName + "] is !NOT! directory.");
		}
	}

	public Map<String, Long> getFileInfos() {
		return fileInfos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileInfos == null) ? 0 : fileInfos.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DirSnapshot))
			return false;
		final DirSnapshot other = (DirSnapshot) obj;

		if (fileInfos == null) {
			if (other.fileInfos != null)
				return false;
		} else if (!fileInfos.equals(other.fileInfos))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
