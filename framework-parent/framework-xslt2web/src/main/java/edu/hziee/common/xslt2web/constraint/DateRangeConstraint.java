package edu.hziee.common.xslt2web.constraint;

import java.text.ParseException;
import java.util.Date;

import edu.hziee.common.xslt2web.sysutil.DateUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class DateRangeConstraint extends BaseConstraint {
	private String message;
	private Date low;
	private Date high;

	public DateRangeConstraint(String fieldName, String displayName, Date low,
			Date high) {
		super(fieldName, displayName);
		this.low = low;
		this.high = high;
		setErrorMsg();
	}

	private void setErrorMsg() {
		if (DateUtil.MIN_DATE.equals(low) && DateUtil.MAX_DATE.equals(high))
			message = String.format("%s��ֵӦ����%s��%s֮��", getDisplayName(),
					DateUtil.toDate(low), DateUtil.toDate(high));
		else if (DateUtil.MIN_DATE.equals(low))
			message = String.format("%s��ֵӦ��С��%s", getDisplayName(), DateUtil
					.toDate(high));
		else if (DateUtil.MAX_DATE.equals(high))
			message = String.format("%s��ֵӦ�ô���%s", getDisplayName(), DateUtil
					.toDate(low));
		else
			message = String.format("%s��ֵӦ����%s��%s֮��", getDisplayName(),
					DateUtil.toDate(low), DateUtil.toDate(high));
	}

	@Override
	public boolean checkError(String value, int position) {
		if (StringUtil.isEmpty(value))
			return true;
		Date temp;
		try {
			temp = DateUtil.parseDate(value);
			return low.compareTo(temp) <= 0 && high.compareTo(temp) >= 0;
		} catch (ParseException e) {
			return false;
		}
	}

	@Override
	public String getJavaScript() {
		return "";
	}

	@Override
	public BaseErrorObject newErrorObject(int position) {
		return new BaseErrorObject(getFieldName(), message, position);
	}

}
