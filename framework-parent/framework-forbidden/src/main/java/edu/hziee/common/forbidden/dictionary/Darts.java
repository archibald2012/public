package edu.hziee.common.forbidden.dictionary;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;


public class Darts implements Externalizable {

	protected int[] base;
	protected int[] check;
	protected String[] state;
	protected Map<String, Integer> coding;
	protected Map<String, Integer> firstWordIndices;

	public void build(List<String> phraseList) {
		if (phraseList == null)
			throw new IllegalArgumentException("phraseList can't be null");
		build(phraseList.toArray(new String[phraseList.size()]));
	}

	public void build(String... phraseArray) {
		if (phraseArray == null || phraseArray.length == 0)
			throw new IllegalArgumentException("phraseArray can't be empty");
		int maxPhraseLength = 0;
		Set<String> phrases = new TreeSet<String>();
		Set<String> words = new TreeSet<String>();
		for (String phrase : phraseArray) {
			if (phrase == null || phrase.length() < 1 || phrase.startsWith("#"))
				continue;
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
	}

	protected void initVariable() {
		base = null;
		check = null;
		state = null;
		coding = new HashMap<String, Integer>();
		firstWordIndices = new HashMap<String, Integer>();
	}

	protected void create(Set<String> phrases, int maxPhraseLength) {
		Map<String, Integer> stateBaseIndices = new HashMap<String, Integer>(phrases.size() * 3);
		extendSize(coding.size());
		int pos = 0;
		for (String phrase : phrases) {
			String c = phrase.substring(0, 1);
			if (!firstWordIndices.containsKey(c)) {
				base[pos] = 1;
				check[pos] = 0;
				state[pos] = phrase.substring(0, 1);
				stateBaseIndices.put(state[pos], pos);
				firstWordIndices.put(c, ++pos);
			}
		}
		for (int i = 1; i < maxPhraseLength; i++) {
			for (String phrase : phrases) {
				if (phrase.length() <= i || stateBaseIndices.containsKey(phrase.substring(0, i + 1)))
					continue;
				String c = phrase.substring(i, i + 1);
				int prevBaseIndex = stateBaseIndices.get(phrase.substring(0, i));
				int prevBaseValue = base[prevBaseIndex];
				int expectBaseIndex = pos;
				int expectPrevBaseValue = expectBaseIndex - coding.get(c) + 1;
				if (prevBaseValue < expectPrevBaseValue) {
					base[prevBaseIndex] = expectPrevBaseValue;
					if (base.length <= pos)
						extendSize(base.length);
					base[pos] = 1;
					check[pos] = prevBaseIndex + 1;
					state[pos] = phrase.substring(0, i + 1);
					stateBaseIndices.put(state[pos], pos++);
				} else {
					pos = prevBaseValue + coding.get(c) - 1;
					if (base.length <= pos)
						extendSize(base.length);
					base[pos] = 1;
					check[pos] = prevBaseIndex + 1;
					state[pos] = phrase.substring(0, i + 1);
					stateBaseIndices.put(state[pos], pos++);
				}
			}
		}
		resize(pos);
		for (int i = 0; i < base.length; i++) {
			if (state[i] == null) {
				state[i] = "";
				continue;
			}
			if (base[i] > 0 && phrases.contains(state[i])) {
				base[i] *= -1;
			}
		}
	}

	protected void extendSize(int i) {
		if (base == null) {
			base = new int[i];
		} else {
			int[] temp = new int[base.length + i];
			System.arraycopy(base, 0, temp, 0, base.length);
			base = temp;
		}
		if (check == null) {
			check = new int[i];
		} else {
			int[] temp = new int[check.length + i];
			System.arraycopy(check, 0, temp, 0, check.length);
			check = temp;
		}
		if (state == null) {
			state = new String[i];
		} else {
			String[] temp = new String[state.length + i];
			System.arraycopy(state, 0, temp, 0, state.length);
			state = temp;
		}
	}

	protected void resize(int size) {
		int[] tempBase = new int[size];
		System.arraycopy(base, 0, tempBase, 0, size);
		base = tempBase;
		int[] tempCheck = new int[size];
		System.arraycopy(check, 0, tempCheck, 0, size);
		check = tempCheck;
		String[] tempState = new String[size];
		System.arraycopy(state, 0, tempState, 0, size);
		state = tempState;
	}

	protected int getBaseIndex(String phrase) {
		int result = -1;
		if (phrase != null && phrase.length() > 0)
			for (int i = 0; i < state.length; i++) {
				if (state[i].equals(phrase)) {
					result = i;
					break;
				}
			}
		return result;
	}

	public int[] search(String sentence) {
		return search(sentence, 0);
	}

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
						if (m >= 0)
							return new int[] { sub, m + 1 };
						else
							return new int[] { sub, pos };
					}
					if (check[p - 1] == o) {
						o = p;
						b = base[p - 1];
						if (b < 0) {
							m = i;
							sub = p;
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
			if (m >= 0)
				return new int[] { sub, m + 1 };
			else
				return new int[] { sub, pos };
		} else {
			return new int[] { sub, pos };
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		base = (int[]) in.readObject();
		check = (int[]) in.readObject();
		state = (String[]) in.readObject();
		coding = (Map<String, Integer>) in.readObject();
		firstWordIndices = (Map<String, Integer>) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(base);
		out.writeObject(check);
		out.writeObject(state);
		out.writeObject(coding);
		out.writeObject(firstWordIndices);
	}

	@Override
	public boolean equals(Object obj) {
		if (base == null)
			return super.equals(obj);
		if (!(obj instanceof Darts))
			return false;
		Darts d = (Darts) obj;
		if (base.length != d.base.length || check.length != d.check.length || state.length != d.state.length
				|| coding.size() != d.coding.size() || firstWordIndices.size() != d.firstWordIndices.size())
			return false;
		for (int i = 0; i < base.length; i++) {
			if (base[i] != d.base[i])
				return false;
		}
		for (int i = 0; i < check.length; i++) {
			if (check[i] != d.check[i])
				return false;
		}
		for (int i = 0; i < state.length; i++) {
			if (!state[i].equals(d.state[i]))
				return false;
		}
		for (Iterator<Entry<String, Integer>> ite1 = coding.entrySet().iterator(), ite2 = d.coding.entrySet()
				.iterator(); ite1.hasNext();) {
			Entry<String, Integer> e1 = ite1.next();
			Entry<String, Integer> e2 = ite2.next();
			if (!e1.getKey().equals(e2.getKey()) || !e1.getValue().equals(e2.getValue()))
				return false;
		}
		for (Iterator<Entry<String, Integer>> ite1 = firstWordIndices.entrySet().iterator(), ite2 = d.firstWordIndices
				.entrySet().iterator(); ite1.hasNext();) {
			Entry<String, Integer> e1 = ite1.next();
			Entry<String, Integer> e2 = ite2.next();
			if (!e1.getKey().equals(e2.getKey()) || !e1.getValue().equals(e2.getValue()))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (base == null)
			return super.toString();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < base.length; i++) {
			sb.append("index=").append(i + 1).append("; base=").append(base[i]).append("; check=").append(check[i])
					.append("; state=").append(state[i]).append("\n");
		}
		return new String(sb);
	}

}
