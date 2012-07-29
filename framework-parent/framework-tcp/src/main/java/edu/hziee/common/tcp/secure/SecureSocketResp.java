/**
 * 
 */
package edu.hziee.common.tcp.secure;

import edu.hziee.common.serialization.bytebean.annotation.ByteField;
import edu.hziee.common.serialization.protocol.annotation.SignalCode;
import edu.hziee.common.serialization.protocol.xip.AbstractXipSignal;
import edu.hziee.common.serialization.protocol.xip.XipResponse;

/**
 * @author Administrator
 * 
 */
@SignalCode(messageCode = 20000000)
public class SecureSocketResp extends AbstractXipSignal implements XipResponse {

	@ByteField(index = 0)
	private byte[]	key;

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

}
