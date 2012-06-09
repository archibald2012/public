package edu.hziee.common.forbidden.dictionary;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class SeparateSkipDarts extends SeparateDarts {

	private int	skip;

	public SeparateSkipDarts() {
	}

	public SeparateSkipDarts(int skip, List<String> separateWordList) {
		this(skip, separateWordList == null ? new String[0] : separateWordList.toArray(new String[separateWordList.size()]));
	}

	public SeparateSkipDarts(int skip, String... separateWordArray) {
		super(separateWordArray);
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
						if (firstWordIndices.containsKey(s)) {
							int[] sp = search0(sentence, i);
							if (sp[0] > 0) {
								if (separateState[sp[0] - 1] == null || "".equals(separateState[sp[0] - 1])) {
									if (remain > 0) {
										remain--;
										continue;
									} else if (m >= 0) {
										return new int[] { sub, m + 1 };
									} else {
										return new int[] { sub, pos };
									}
								}
								p = (b > 0 ? b : -b) + coding.get(separateState[sp[0] - 1]);
								if (p > check.length) {
									if (m >= 0)
										return new int[] { sub, m + 1 };
									else
										return new int[] { sub, pos };
								}
								i = sp[1] - 1;
								if (check[p - 1] == o) {
									remain = skip;
									o = p;
									b = base[p - 1];
									if (b < 0) {
										m = i;
										sub = p;
									}
									continue;
								} else if (remain > 0) {
									remain--;
									continue;
								} else if (m >= 0) {
									return new int[] { sub, m + 1 };
								} else {
									return new int[] { sub, pos };
								}
							} else if (m >= 0) {
								return new int[] { sub, m + 1 };
							} else {
								return new int[] { sub, pos };
							}
						} else if (remain > 0) {
							remain--;
							continue;
						} else if (m >= 0) {
							return new int[] { sub, m + 1 };
						} else {
							return new int[] { sub, pos };
						}
					}
					if (check[p - 1] == o) {
						o = p;
						b = base[p - 1];
						if (b < 0) {
							if (separateState[p - 1] != null && !"".equals(separateState[p - 1])) {
								if (remain < skip) {
									if (m >= 0) {
										return new int[] { sub, m + 1 };
									} else {
										return new int[] { sub, pos };
									}
								}
								s = separateState[p - 1];
								if (firstWordIndices.containsKey(s)) {
									o = firstWordIndices.get(s);
									p = o;
									b = base[p - 1];
								}
							} else {
								m = i;
								sub = p;
							}
						}
						remain = skip;
					} else {
						if (m == -1) {
							if (firstWordIndices.containsKey(s)) {
								int[] sp = search0(sentence, i);
								if (sp[0] > 0) {
									if (separateState[sp[0] - 1] == null || "".equals(separateState[sp[0] - 1])) {
										if (remain > 0) {
											remain--;
											continue;
										} else if (m >= 0) {
											return new int[] { sub, m + 1 };
										} else {
											return new int[] { sub, pos };
										}
									}
									p = (b > 0 ? b : -b) + coding.get(separateState[sp[0] - 1]);
									if (p > check.length) {
										if (m >= 0)
											return new int[] { sub, m + 1 };
										else
											return new int[] { sub, pos };
									}
									i = sp[1] - 1;
									if (check[p - 1] == o) {
										remain = skip;
										o = p;
										b = base[p - 1];
										if (b < 0) {
											m = i;
											sub = p;
										}
										continue;
									} else if (remain > 0) {
										remain--;
										continue;
									} else if (m >= 0) {
										return new int[] { sub, m + 1 };
									} else {
										return new int[] { sub, pos };
									}
								} else if (m >= 0) {
									return new int[] { sub, m + 1 };
								} else {
									return new int[] { sub, pos };
								}
							} else if (remain > 0) {
								remain--;
								continue;
							} else {
								return new int[] { sub, pos };
							}
						} else if (remain > 0) {
							remain--;
							continue;
						} else if (m >= 0) {
							return new int[] { sub, m + 1 };
						}
					}
				} else if (remain > 0) {
					remain--;
					continue;
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
		if (super.equals(obj) && skip == ((SeparateSkipDarts) obj).skip) {
			for (int i = 0; i < separateState.length; i++) {
				if (!separateState[i].equals(((SeparateSkipDarts) obj).separateState[i]))
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "skip=" + skip + "\n" + super.toString();
	}

}
