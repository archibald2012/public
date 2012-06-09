package edu.hziee.common.forbidden.decorate;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import edu.hziee.common.forbidden.criterion.CharStandardization;
import edu.hziee.common.forbidden.dictionary.Darts;

public class TextDecorator {

	private Darts							darts;
	private DecoratorHandler	handler;
	private boolean						needT2S;
	private boolean						needDBC;
	private boolean						ignoreCase;
	private boolean						filterNoneHanLetter;
	private boolean						convertSynonymy;
	private boolean						filterSymbol;
	private boolean						keepLastSymbol;

	private ReadWriteLock			lock											= new ReentrantReadWriteLock();
	private Lock							readLock									= lock.readLock();
	private Lock							writeLock									= lock.writeLock();

	public static String[]		DEFAULT_DECORATE_CONTENT	= { "*", "**", "***", "****", "*****", "******", "*******",
			"********", "*********", "**********"					};

	public TextDecorator() {
		this.needT2S = true;
		this.needDBC = true;
		this.ignoreCase = false;
		this.filterNoneHanLetter = false;
		this.convertSynonymy = false;
		this.filterSymbol = true;
		this.keepLastSymbol = true;
	}

	public TextDecorator(Darts darts) {
		this(darts, new DecoratorHandler() {
			@Override
			public String decorate(String filterContent) {
				return DEFAULT_DECORATE_CONTENT[(filterContent.length() - 1) % DEFAULT_DECORATE_CONTENT.length];
			}

			@Override
			public String getReplaceText() {
				return null;
			}

			@Override
			public int getUpperLimit() {
				return 0;
			}
		});
	}

	public TextDecorator(Darts darts, DecoratorHandler handler) {
		this();
		this.darts = darts;
		this.handler = handler;
		CharStandardization.isSeperatorSymbol(' ');
	}

	public Darts getDarts() {
		return darts;
	}

	public void setDarts(Darts darts) {
		writeLock.lock();
		try {
			this.darts = darts;
		} finally {
			writeLock.unlock();
		}
	}

	public DecoratorHandler getHandler() {
		return handler;
	}

	public void setHandler(DecoratorHandler handler) {
		this.handler = handler;
	}

	public boolean isNeedT2S() {
		return needT2S;
	}

	public void setNeedT2S(boolean needT2S) {
		this.needT2S = needT2S;
	}

	public boolean isNeedDBC() {
		return needDBC;
	}

	public void setNeedDBC(boolean needDBC) {
		this.needDBC = needDBC;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public boolean isFilterNoneHanLetter() {
		return filterNoneHanLetter;
	}

	public void setFilterNoneHanLetter(boolean filterNoneHanLetter) {
		this.filterNoneHanLetter = filterNoneHanLetter;
	}

	public boolean isConvertSynonymy() {
		return convertSynonymy;
	}

	public void setConvertSynonymy(boolean convertSynonymy) {
		this.convertSynonymy = convertSynonymy;
	}

	public boolean isFilterSymbol() {
		return filterSymbol;
	}

	public void setFilterSymbol(boolean filterSymbol) {
		this.filterSymbol = filterSymbol;
	}

	public boolean isKeepLastSymbol() {
		return keepLastSymbol;
	}

	public void setKeepLastSymbol(boolean keepLastSymbol) {
		this.keepLastSymbol = keepLastSymbol;
	}

	public boolean containKeyword(String content) {
		return containKeyword(content, true);
	}

	public boolean containKeyword(String content, boolean matchIgnoreCase) {
		boolean hasKeyword = false;
		content = CharStandardization.compositeTextConvert(content, needT2S, needDBC, ignoreCase || matchIgnoreCase,
				filterNoneHanLetter, convertSynonymy, filterSymbol, keepLastSymbol);
		readLock.lock();
		try {
			int p = 0;
			while (p < content.length()) {
				int[] r = darts.search(content, p);
				if (r[0] == 0) {
					p++;
				} else {
					hasKeyword = true;
					break;
				}
			}
		} finally {
			readLock.unlock();
		}
		return hasKeyword;
	}

	public String decorateText(String content) {
		return decorateText(content, true);
	}

	public String decorateText(String content, boolean matchIgnoreCase) {
		return decorateText(content, matchIgnoreCase, handler);
	}

	public String decorateText(String content, boolean matchIgnoreCase, DecoratorHandler handler) {
		String matchContent = content;
		content = CharStandardization.compositeTextConvert(content, needT2S, needDBC, ignoreCase, filterNoneHanLetter,
				convertSynonymy, filterSymbol, keepLastSymbol);
		if (!ignoreCase && matchIgnoreCase) {
			matchContent = CharStandardization.compositeTextConvert(matchContent, needT2S, needDBC, matchIgnoreCase,
					filterNoneHanLetter, convertSynonymy, filterSymbol, keepLastSymbol);
		} else {
			matchContent = content;
		}
		if (darts == null)
			return content;
		StringBuilder sb = new StringBuilder(content.length());
		readLock.lock();
		try {
			int n = 0;
			int p = 0;
			while (p < content.length()) {
				int[] r = darts.search(matchContent, p);
				if (r[0] == 0) {
					sb.append(content.substring(p, p + 1));
					p++;
				} else {
					if (handler.getUpperLimit() > 0 && ++n >= handler.getUpperLimit())
						return handler.getReplaceText();
					sb.append(handler.decorate(content.substring(p, r[1])));
					p = r[1];
				}
			}
		} finally {
			readLock.unlock();
		}
		return new String(sb);
	}

}
