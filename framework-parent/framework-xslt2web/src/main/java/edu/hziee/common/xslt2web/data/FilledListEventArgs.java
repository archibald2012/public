package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.sys.EventArgs;
import edu.hziee.common.xslt2web.sys.QueryParamCondition;

public class FilledListEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;

	private boolean isPost;
	private AbstractXmlTableResolver listView;
	private QueryParamCondition sqlCon;
	private String order;
	private int pageNumber;
	private int pageSize;
	private int count;
	private DataSet postDataSet;

	public FilledListEventArgs(Object source) {
		super(source);
	}

	public final boolean isPost() {
		return isPost;
	}

	public final AbstractXmlTableResolver getListView() {
		return listView;
	}

	public final QueryParamCondition getSqlCon() {
		return sqlCon;
	}

	public final String getOrder() {
		return order;
	}

	public final int getPageNumber() {
		return pageNumber;
	}

	public final int getPageSize() {
		return pageSize;
	}

	public final int getCount() {
		return count;
	}

	public final DataSet getPostDataSet() {
		return postDataSet;
	}

	void setProperties(AbstractXmlTableResolver listView,
			QueryParamCondition sqlCon, String order, int pageNumber,
			int pageSize, int count, boolean isPost, DataSet postDataSet) {
		this.isPost = isPost;
		this.listView = listView;
		this.sqlCon = sqlCon;
		this.order = order;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.count = count;
		this.postDataSet = postDataSet;
	}

	public static FilledListEventArgs getArgs(Object source,
			AbstractXmlTableResolver listView, QueryParamCondition sqlCon,
			String order, int pageNumber, int pageSize, int count,
			boolean isPost, DataSet postDataSet) {
		FilledListEventArgs result = new FilledListEventArgs(source);
		result.setProperties(listView, sqlCon, order, pageNumber, pageSize,
				count, isPost, postDataSet);
		return result;

	}
}
