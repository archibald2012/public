/**
 * 
 */
package edu.hziee.common.serialization.kv;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.FieldUtil;
import edu.hziee.common.lang.SimpleCache;
import edu.hziee.common.serialization.kv.annotation.KeyValueAttribute;

/**
 * @author archie
 * 
 */
public class KVUtils {

  @SuppressWarnings("unused")
  private static final Logger                   logger        = LoggerFactory.getLogger(KVUtils.class);

  private static SimpleCache<Class<?>, Field[]> kvFieldsCache = new SimpleCache<Class<?>, Field[]>();

  public static Field[] getKVFieldsOf(final Class<?> kvType) {
    return kvFieldsCache.get(kvType, new Callable<Field[]>() {

      public Field[] call() throws Exception {
        return FieldUtil.getAnnotationFieldsOf(kvType, KeyValueAttribute.class);
      }
    });
  }
}
