package edu.hziee.common.forbidden;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.forbidden.criterion.CharStandardization;
import edu.hziee.common.forbidden.decorate.TextDecorator;
import edu.hziee.common.forbidden.dictionary.Darts;

public class KeywordFilter {

	private static final Logger	logger	= LoggerFactory.getLogger(KeywordFilter.class);

	private TextDecorator				decorator;

	public TextDecorator getTextDecorator() {
		return decorator;
	}

	public KeywordFilter(String dicFile) throws Exception {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(this.getClass().getResourceAsStream(dicFile));
			Darts darts = (Darts) ois.readObject();
			decorator = new TextDecorator(darts);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
		}

	}

	public void replaceDarts(Darts darts) {
		decorator = new TextDecorator(darts);
	}

	public boolean containKeyword(String content) {
		return decorator.containKeyword(content, true);
	}

	public String filterText(String content) {
		return decorator.decorateText(content, true);
	}

	public String standardiseText(String content) {
		return CharStandardization.compositeTextConvert(content, true, true, false, false, false, true, true);
	}

}
