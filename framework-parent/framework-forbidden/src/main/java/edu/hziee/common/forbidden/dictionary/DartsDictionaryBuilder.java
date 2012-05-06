package edu.hziee.common.forbidden.dictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DartsDictionaryBuilder {

	public static String	dicFile				= "./bin/forbidden.dic";
	public static String	phraseFile		= "./dic/forbidden.txt";
	public static String	separateFile	= "./dic/separate.txt";

	public static void generateDartsDictionary(String dicFile, String phraseFile, boolean standard) throws Exception {
		List<String> phraseList = generatePhraseList(phraseFile, standard);
		Darts d = new Darts();
		d.build(phraseList);
		saveDirFile(dicFile, d);
	}

	public static void generateSkipDartsDictionary(String dicFile, String phraseFile, int skip, boolean standard)
			throws Exception {
		List<String> phraseList = generatePhraseList(phraseFile, standard);
		Darts d = new SkipDarts(skip);
		d.build(phraseList);
		saveDirFile(dicFile, d);
	}

	public static void generateSeparateDartsDictionary(String dicFile, String phraseFile, String separateFile,
			boolean standard) throws Exception {
		List<String> phraseList = generatePhraseList(phraseFile, standard);
		List<String> separateList = generatePhraseList(separateFile, standard);
		Darts d = new SeparateDarts(separateList);
		d.build(phraseList);
		saveDirFile(dicFile, d);
	}

	public static void generateSeparateSkipDartsDictionary(String dicFile, String phraseFile, String separateFile,
			int skip, boolean standard) throws Exception {
		List<String> phraseList = generatePhraseList(phraseFile, standard);
		List<String> separateList = generatePhraseList(separateFile, standard);
		Darts d = new SeparateSkipDarts(skip, separateList);
		d.build(phraseList);
		saveDirFile(dicFile, d);
	}

	private static List<String> generatePhraseList(String file, boolean standard) throws Exception {
		if (standard)
			DartsDictionaryStandardization.main(new String[] { file });
		Pattern p = Pattern.compile("\\\\u[0-9abcdefABCDEF]{4}");
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		String phrase;
		List<String> phraseList = new ArrayList<String>();
		while ((phrase = in.readLine()) != null) {
			phrase = phrase.trim();
			if (phrase.length() > 0 && !phrase.startsWith("#")) {
				if (p.matcher(phrase).matches())
					phrase = new String(new char[] { (char) Integer.parseInt(phrase.substring(2), 16) });
				phraseList.add(phrase);
			}
		}
		in.close();
		return phraseList;
	}

	private static void saveDirFile(String dicFile, Darts d) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dicFile));
		oos.writeObject(d);
		oos.close();
	}

	public static Darts getDartsDictionary(String dicFile) throws Exception {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(dicFile));
			return (Darts) ois.readObject();
		} finally {
			if (ois != null)
				ois.close();
		}
	}

	public static void main(String[] args) {
		try {
			if (args != null) {
				switch (args.length > 3 ? 3 : args.length) {
				case 3:
					separateFile = args[2].equals("/") ? separateFile : args[2];
				case 2:
					phraseFile = args[1].equals("/") ? phraseFile : args[1];
				case 1:
					dicFile = args[0].equals("/") ? dicFile : args[0];
					break;
				}
			}
			boolean standard = System.getProperty("s") != null;
			if (System.getProperty("mode") == null) {
				System.out.println(usage());
				System.exit(0);
			}
			String mode = System.getProperty("mode").toLowerCase();
			if (mode.equals("regular") || mode.equals("normal") || mode.equals("1")) {
				generateDartsDictionary(dicFile, phraseFile, standard);
				System.out.println("regular dic build successful");
			} else if (mode.equals("skip") || mode.equals("2")) {
				if (System.getProperty("skip") == null) {
					System.out.println(usage());
					System.exit(0);
				}
				try {
					int skip = Integer.parseInt(System.getProperty("skip"));
					if (skip <= 0)
						throw new NumberFormatException();
					generateSkipDartsDictionary(dicFile, phraseFile, skip, standard);
					System.out.println("skip dic build successful");
				} catch (NumberFormatException e) {
					System.out.println(usage());
					System.exit(0);
				}
			} else if (mode.equals("separate") || mode.equals("3")) {
				generateSeparateDartsDictionary(dicFile, phraseFile, separateFile, standard);
				System.out.println("separate dic build successful");
			} else if (mode.equals("separateskip") || mode.equals("skipseparate") || mode.equals("ss") || mode.equals("4")) {
				if (System.getProperty("skip") == null) {
					System.out.println(usage());
					System.exit(0);
				}
				try {
					int skip = Integer.parseInt(System.getProperty("skip"));
					if (skip <= 0)
						throw new NumberFormatException();
					generateSeparateSkipDartsDictionary(dicFile, phraseFile, separateFile, skip, standard);
					System.out.println("separateskip dic build successful");
				} catch (NumberFormatException e) {
					System.out.println(usage());
					System.exit(0);
				}

			} else {
				System.out.println(usage());
				System.exit(0);
			}
			System.out.println("dic build successful");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static String usage() {
		String s = "Program arguments: \"dicFile\" \"phraseFile\" \"separateFile\" [use \"/\" represent default]\n";
		s += "VM arguments: mode 1 [regular]     : -Dmode=1\n";
		s += "              mode 2 [skip]        : -Dmode=2 -Dskip=?(?>0)\n";
		s += "              mode 3 [separate]    : -Dmode=3\n";
		s += "              mode 4 [separateskip]: -Dmode=4 -Dskip=?(?>0)\n";
		s += "              text standard        : -Ds\n";
		return s;
	}

}
