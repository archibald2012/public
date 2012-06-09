package edu.hziee.common.xslt2web.right;

import java.util.Hashtable;
import java.util.Observable;

import edu.hziee.common.xslt2web.sys.IParamBuilder;

public class ClassicDataRight extends EmptyDataRight {
	private Hashtable<Object, IDataRightUnit> dataRightUnits;
	private IDataRightUnit defaultRight;
	
	public ClassicDataRight() {
		dataRightUnits = new Hashtable<Object, IDataRightUnit>();
	}

	public final IDataRightUnit getDefaultRight() {
		return defaultRight;
	}

	public final IDataRightUnit getItem(Object type) {
		return dataRightUnits.get(type);
	}
	
	public final IDataRightUnit getDefaultRightUnit(Object type) {
		IDataRightUnit result = getItem(type);
		if (result == null) {
			result = this.defaultRight;
		}
		return result;
	}
	
	public final void add(IDataRightUnit unit) {
		dataRightUnits.put(unit.getType(), unit);
		if (defaultRight == null)
			defaultRight = unit;
	}

	@Override
	public void checkDelete(Object type, Object ownerId) {
		getDefaultRightUnit(type).checkDelete(ownerId);
	}

	@Override
	public void checkPublic(Object type, Object ownerId) {
		getDefaultRightUnit(type).checkPublic(ownerId);
	}

	@Override
	public void checkReadOnly(Object type, Object userId) {
		getDefaultRightUnit(type).checkReadOnly(userId);
	}

	@Override
	public void checkReadWrite(Object type, Object ownerId) {
		getDefaultRightUnit(type).checkReadWrite(ownerId);
	}

	@Override
	public IParamBuilder getPublicSql(Object type, String fieldName, Object userID) {
		return getDefaultRightUnit(type).getPublicSql(fieldName, userID);
	}

	@Override
	public void update(Observable o, Object arg) {
		for (IDataRightUnit unit : dataRightUnits.values()) {
			unit.initialize(arg);
		}
	}
	
}
