package edu.hziee.common.xslt2web.data;

import java.util.HashMap;

public class HandledCollection {
	private HashMap<AbstractXmlTableResolver, Boolean> resovlers;

	public HandledCollection() {
		resovlers = new HashMap<AbstractXmlTableResolver, Boolean>();
	}

	final void addResolvers(AbstractXmlTableResolver... resolvers) {
		for (AbstractXmlTableResolver resolver : resolvers)
			this.resovlers.put(resolver, false);
	}

	public final boolean getItem(AbstractXmlTableResolver resolver) {
		Boolean result = resovlers.get(resolver);
		return result == null ? false : result;
	}
	
	public final void setItem(AbstractXmlTableResolver resolver, boolean value) {
		//Debug.Assert(fResolvers.Contains(resolver), "Resolver必须存在于对应的DataSet中");
		resovlers.put(resolver, value);
	}
}
