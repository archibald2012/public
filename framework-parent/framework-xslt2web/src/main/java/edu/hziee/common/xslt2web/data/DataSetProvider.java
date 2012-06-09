package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.exception.ToolkitException;
import edu.hziee.common.xslt2web.sys.RegsCollection;

public class DataSetProvider implements IDataProvider {
	public static final IDataProvider Instance = new DataSetProvider();

	private DataSetProvider() {
	}

	public BaseDataSet createDataSet(RegsCollection regs, String source) {
		DataSetRegCategory category = (DataSetRegCategory) regs
				.get(DataSetRegCategory.REG_NAME);
		if (category == null)
			throw new ToolkitException("");
		BaseDataSet result = category.newInstance(source);
		if (result == null)
			throw new ToolkitException("");
		return result;
	}

}
