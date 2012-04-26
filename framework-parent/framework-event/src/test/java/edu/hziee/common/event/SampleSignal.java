/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    SampleBean.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午03:46:48
 *******************************************************************************/
package edu.hziee.common.event;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.hziee.common.serialization.bytebean.annotation.ByteField;
import edu.hziee.common.serialization.protocol.annotation.SignalCode;
import edu.hziee.common.serialization.protocol.xip.AbstractXipSignal;
import edu.hziee.common.serialization.protocol.xip.XipRequest;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: SampleSignal.java 14 2012-01-10 11:54:14Z archie $
 */
@SignalCode(messageCode = 0x121)
public class SampleSignal extends AbstractXipSignal implements XipRequest {

  @ByteField(index = 0)
  private int intField;

  public int getIntField() {
    return intField;
  }

  public void setIntField(int intField) {
    this.intField = intField;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public boolean equals(Object other) {
    return EqualsBuilder.reflectionEquals(this, other);
  }
}
