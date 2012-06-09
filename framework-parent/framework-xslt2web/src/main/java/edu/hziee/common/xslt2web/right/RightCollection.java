package edu.hziee.common.xslt2web.right;

public final class RightCollection {
	private LoginObservable login;
	private ILoginRight loginRight;
	private IDataRight dataRight;
	private IFunctionRight functionRight;
	
	public RightCollection() {
		login = new LoginObservable();
		setLoginRight(new EmptyLoginRight());
		setDataRight(new EmptyDataRight());
		setFunctionRight(new EmptyFunctionRight());
	}

	public final ILoginRight getLoginRight() {
		return loginRight;
	}

	public final void setLoginRight(ILoginRight loginRight) {
		if (loginRight != this.loginRight) 
			this.loginRight = loginRight;
	}

	public final IDataRight getDataRight() {
		return dataRight;
	}

	public final void setDataRight(IDataRight dataRight) {
		if (dataRight != this.dataRight) {
			if (this.dataRight != null)
				login.deleteObserver(this.dataRight);
			if (dataRight != null)
				login.addObserver(dataRight);
			this.dataRight = dataRight;
		}
	}

	public final IFunctionRight getFunctionRight() {
		return functionRight;
	}

	public final void setFunctionRight(IFunctionRight functionRight) {
		if (functionRight != this.functionRight) {
			if (this.functionRight != null)
				login.deleteObserver(this.functionRight);
			if (functionRight != null)
				login.addObserver(functionRight);
			this.functionRight = functionRight;
		}
	}

}
