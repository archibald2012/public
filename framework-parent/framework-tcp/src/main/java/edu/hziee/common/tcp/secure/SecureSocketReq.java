/**
 * 
 */
package edu.hziee.common.tcp.secure;

import edu.hziee.common.serialization.bytebean.annotation.ByteField;
import edu.hziee.common.serialization.protocol.annotation.SignalCode;
import edu.hziee.common.serialization.protocol.xip.AbstractXipSignal;
import edu.hziee.common.serialization.protocol.xip.XipRequest;

/**
 * @author Administrator
 * 
 */
@SignalCode(messageCode = 100000)
public class SecureSocketReq extends AbstractXipSignal implements XipRequest {

	@ByteField(index = 0)
	private byte[]	clientPublicKey;

	@ByteField(index = 1)
	private byte[]	sign;

	public byte[] getSign() {
		return sign;
	}

	public void setSign(byte[] sign) {
		this.sign = sign;
	}

	public byte[] getClientPublicKey() {
		return clientPublicKey;
	}

	public void setClientPublicKey(byte[] clientPublicKey) {
		this.clientPublicKey = clientPublicKey;
	}

}
