package edu.hziee.common.forbidden.dictionary;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SeparateDarts extends Darts {

	private String[]		separateWordArray;
	protected String[]	separateState;

	public SeparateDarts() {
	}

	public SeparateDarts(List<String> separateWordList) {
		this(separateWordList == null ? new String[0] : separateWordList.toArray(new String[separateWordList.size()]));
	}

	public SeparateDarts(String... separateWordArray) {
		if (separateWordArray.length == 0)
			throw new IllegalArgumentException("separateWordArray can't be empty");
		this.separateWordArray = separateWordArray;
	}

	@Override
	public void build(String... phraseArray) {
		if (phraseArray.length == 0)
			throw new IllegalArgumentException("phraseArray can't be empty");
		if (separateWordArray == null || separateWordArray.length == 0)
			throw new IllegalArgumentException("separateWordArray can't be empty");
		int maxPhraseLength = 0;
		Set<String> phrases = new TreeSet<String>();
		Set<String> words = new TreeSet<String>();
		Map<String, String> separatePhrases = new HashMap<String, String>();
		for (String separateWord : separateWordArray) {
			if (separateWord == null || separateWord.length() < 3 || separateWord.startsWith("#"))
				continue;
			phrases.add(separateWord.substring(1));
			separatePhrases.put(separateWord.substring(1), separateWord.substring(0, 1));
			for (int i = 1; i < separateWord.length(); i++) {
				words.add(separateWord.substring(i, i + 1));
			}
		}
		for (String phrase : phraseArray) {
			if (phrase == null || phrase.length() < 1 || phrase.startsWith("#"))
				continue;
			phrase = rebuildPhrase(phrase, separatePhrases);
			phrases.add(phrase);
			maxPhraseLength = maxPhraseLength > phrase.length() ? maxPhraseLength : phrase.length();
			for (int i = 0; i < phrase.length(); i++) {
				words.add(phrase.substring(i, i + 1));
			}
		}
		initVariable();
		int codingIndex = 1;
		for (String word : words) {
			coding.put(word, codingIndex++);
		}
		create(phrases, maxPhraseLength);
		resetSeparatePharseState(separatePhrases);
	}

	private String rebuildPhrase(String phrase, Map<String, String> separatePhrases) {
		if (separatePhrases == null || separatePhrases.isEmpty()) {
			return phrase;
		}
		for (String separatePhrase : separatePhrases.keySet()) {
			if (phrase.indexOf(separatePhrase) >= 0) {
				phrase = phrase.replaceAll(separatePhrase, separatePhrases.get(separatePhrase));
			}
		}
		return phrase;
	}

	protected void initVariable() {
		super.initVariable();
		separateState = null;
	}

	private void resetSeparatePharseState(Map<String, String> separatePhrases) {
		for (int i = 0; i < base.length; i++) {
			if (base[i] < 0 && separatePhrases.containsKey(state[i])) {
				separateState[i] = separatePhrases.get(state[i]);
			}
			if (separateState[i] == null)
				separateState[i] = "";
		}
	}

	protected void extendSize(int size) {
		super.extendSize(size);
		if (separateState == null) {
			separateState = new String[size];
		} else {
			String[] temp = new String[separateState.length + size];
			System.arraycopy(separateState, 0, temp, 0, separateState.length);
			separateState = temp;
		}
	}

	@Override
	protected void resize(int size) {
		super.resize(size);
		String[] temp = new String[size];
		System.arraycopy(separateState, 0, temp, 0, size);
		separateState = temp;
	}

	protected int[] search0(String sentence, int pos) {
		return super.search(sentence, pos);
	}

	@Override
	public int[] search(String sentence, int pos) {
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
			for (int i = pos + 1; i < sentence.length(); i++) {
				String s = sentence.substring(i, i + 1);
				if (coding.containsKey(s)) {
					p = (b > 0 ? b : -b) + coding.get(s);
					if (p > check.length) {
						if (firstWordIndices.containsKey(s)) {
							int[] sp = search0(sentence, i);
							if (sp[0] > 0) {
								if (separateState[sp[0] - 1] == null || "".equals(separateState[sp[0] - 1])) {
									if (m >= 0)
										return new int[] { sub, m + 1 };
									else
										return new int[] { sub, pos };
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
									o = p;
									b = base[p - 1];
									if (b < 0) {
										m = i;
										sub = p;
									}
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
					} else if (m >= 0) {
						return new int[] { sub, m + 1 };
					} else {
						if (firstWordIndices.containsKey(s)) {
							int[] sp = search0(sentence, i);
							if (sp[0] > 0) {
								if (separateState[sp[0] - 1] == null || "".equals(separateState[sp[0] - 1])) {
									if (m >= 0)
										return new int[] { sub, m + 1 };
									else
										return new int[] { sub, pos };
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
									o = p;
									b = base[p - 1];
									if (b < 0) {
										m = i;
										sub = p;
									}
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
						}
						return new int[] { sub, pos };
					}
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
		separateState = (String[]) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(separateState);
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			for (int i = 0; i < separateState.length; i++) {
				if (!separateState[i].equals(((SeparateDarts) obj).separateState[i]))
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		if (base == null)
			return super.toString();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < base.length; i++) {
			sb.append("index=").append(i + 1).append("; base=").append(base[i]).append("; check=").append(check[i])
					.append("; state=").append(state[i]).append("; separateState=").append(separateState[i]).append("\n");
		}
		return new String(sb);
	}

}
