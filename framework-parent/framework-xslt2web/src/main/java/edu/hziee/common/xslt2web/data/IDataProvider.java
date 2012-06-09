package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.sys.RegsCollection;

public interface IDataProvider {
	BaseDataSet createDataSet(RegsCollection regs, String source);
}
