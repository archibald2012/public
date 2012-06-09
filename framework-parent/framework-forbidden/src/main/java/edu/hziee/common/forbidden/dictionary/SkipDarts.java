package edu.hziee.common.forbidden.dictionary;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SkipDarts extends Darts {

	private int	skip;

	public SkipDarts() {
	}

	public SkipDarts(int skip) {
		setSkip(skip);
	}

	public void setSkip(int skip) {
		if (skip < 0)
			throw new IllegalArgumentException("skip can't less than 0");
		this.skip = skip;
	}

	@Override
	public int[] search(String sentence, int pos) {
		if (skip == 0)
			return super.search(sentence, pos);
		int sub = 0;
		int m = -1;
		String f = sentence.substring(pos, pos + 1);
		if (firstWordIndices.containsKey(f)) {
			int o = firstWordIndices.get(f);
			int p = o;
			int b = base[p - 1];
			if (b < 0) {
				m = pos;
				sub = p;
			}
			int remain = skip;
			for (int i = pos + 1; i < sentence.length(); i++) {
				String s = sentence.substring(i, i + 1);
				if (coding.containsKey(s)) {
					p = (b > 0 ? b : -b) + coding.get(s);
					if (p > check.length) {
						if (m >= 0)
							return new int[] { sub, m + 1 };
						else
							return new int[] { sub, pos };
					}
					if (check[p - 1] == o) {
						remain = skip;
						o = p;
						b = base[p - 1];
						if (b < 0) {
							m = i;
							sub = p;
						}
					} else if (remain > 0) {
						remain--;
					} else if (m >= 0) {
						return new int[] { sub, m + 1 };
					} else {
						return new int[] { sub, pos };
					}
				} else if (remain > 0) {
					remain--;
				} else if (m >= 0) {
					return new int[] { sub, m + 1 };
				} else {
					return new int[] { sub, pos };
				}
			}
			if (m >= 0)
				return new int[] { sub, m + 1 };
			else
				return new int[] { sub, pos };
		} else {
			return new int[] { sub, pos };
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		skip = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(skip);
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return skip == ((SkipDarts) obj).skip;
		return false;
	}

	@Override
	public String toString() {
		return "skip=" + skip + "\n" + super.toString();
	}

}
