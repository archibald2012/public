/**
 * 
 */
package edu.hziee.common.serialization.kv.codec;

import edu.hziee.common.serialization.kv.context.DecContext;
import edu.hziee.common.serialization.kv.context.DecContextFactory;
import edu.hziee.common.serialization.kv.context.EncContext;
import edu.hziee.common.serialization.kv.context.EncContextFactory;

/**
 * @author archie
 * 
 */
public interface KVCodec {

  DecContextFactory getDecContextFactory();

  Object decode(DecContext ctx);
  
  EncContextFactory getEncContextFactory();
  
  String encode(EncContext ctx);
}
