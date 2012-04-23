/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    BeanCodecUtil.java
 * Creator:     Archibald.Wang
 * Create-Date: 2011-4-1 上午11:26:51
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.codec.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.ArrayUtils;

import edu.hziee.common.lang.SimpleCache;
import edu.hziee.common.serialization.bytebean.field.ByteFieldDesc;
import edu.hziee.common.serialization.bytebean.field.Field2Desc;

/**
 * TODO
 * 
 * @author Archibald.Wang
 * @version $Id: BeanCodecUtil.java 14 2012-01-10 11:54:14Z archie $
 */
public class BeanCodecUtil {
	private Field2Desc field2Desc;
	private SimpleCache<Class<?>, List<ByteFieldDesc>> descesCache = new SimpleCache<Class<?>, List<ByteFieldDesc>>();

	public BeanCodecUtil(Field2Desc field2Desc) {
		this.field2Desc = field2Desc;
	}

	public List<ByteFieldDesc> getFieldDesces(final Class<?> clazz) {
		return descesCache.get(clazz, new Callable<List<ByteFieldDesc>>() {

			public List<ByteFieldDesc> call() {
				List<ByteFieldDesc> ret;

				Field[] fields = null;

				Class<?> itr = clazz;
				while (!itr.equals(Object.class)) {
					fields = (Field[]) ArrayUtils.addAll(
							itr.getDeclaredFields(), fields);
					itr = itr.getSuperclass();
				}

				ret = new ArrayList<ByteFieldDesc>(fields.length);
				for (Field field : fields) {
					ByteFieldDesc desc = field2Desc.genDesc(field);
					if (null != desc) {
						ret.add(desc);
					}
				}

				Collections.sort(ret, ByteFieldDesc.comparator);
				return ret;
			}
		});
	}
}
