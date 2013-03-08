/**
 * 
 */
package org.spoofax.sunshine;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.spoofax.sunshine.framework.language.AdHocJarBasedLanguage;
import org.spoofax.sunshine.framework.messages.IMessage;
import org.spoofax.sunshine.framework.services.AnalysisService;
import org.spoofax.sunshine.framework.services.LanguageService;
import org.spoofax.sunshine.framework.services.MessageService;
import org.spoofax.sunshine.framework.services.QueableAnalysisService;

/**
 * @author Vlad Vergu
 * 
 */
public class Sunshine {

	private final static String LANG_JAR = "--lang-jar";
	private final static String LANG_TBL = "--lang-tbl";
	private final static String PROJ_DIR = "--proj-dir";
	private final static String TRG_FILE = "--targets";
	private final static String TRG_ALL = "--all";

	private static final String[] extens = new String[] { "cs" };
	private static final String langname = "CSharp";
	private static final String observer_fun = "editor-analyze";

	private boolean all_targets;
	private final List<String> language_jars = new LinkedList<String>();;
	private String language_tbl;
	private final List<String> file_targets = new LinkedList<String>();
	private String project_dir;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Sunshine front = new Sunshine();
		front.parseArgs(args);
		front.initialize();
		boolean success = front.analyzeFiles();
		if (success) {
			System.exit(0);
		} else {
			System.exit(1);
		}
	}

	private boolean analyzeFiles() {
		final Collection<File> files = new LinkedList<File>();
		for (String fn : file_targets) {
			files.add(new File(fn));
		}
		long sTime = System.currentTimeMillis();
//		AnalysisService.INSTANCE().analyze(files);
		QueableAnalysisService.INSTANCE().enqueueAnalysis(files);
		QueableAnalysisService.INSTANCE().analyzeQueue();
		long eTime = System.currentTimeMillis();
		Collection<IMessage> msgs = MessageService.INSTANCE().getMessages();
		for (IMessage msg : msgs) {
			System.err.println(msg);
		}
		double duration = (eTime - sTime) / 1000.0;
		System.err.println("Done with " + msgs.size() + " messages in " + duration + " seconds");
		return msgs.size() == 0;
	}

	private void initialize() {
		Environment.INSTANCE().setProjectDir(new File(project_dir));
		final Collection<File> jars = new LinkedList<File>();
		for (String fn : language_jars) {
			jars.add(new File(fn));
		}
		final AdHocJarBasedLanguage lang = new AdHocJarBasedLanguage(langname, extens, "Start", new File(language_tbl),
				observer_fun, jars.toArray(new File[0]));
		LanguageService.INSTANCE().registerLanguage(lang);
	}

	private void parseArgs(String[] args) throws IllegalArgumentException {
		boolean lang_jar_next = false;
		boolean lang_tbl_next = false;
		boolean trg_file_next = false;
		boolean proj_dir_next = false;

		List<String> lang_jars = new LinkedList<String>();
		String lang_tbl = null;
		String projdir = null;
		List<String> targets = new LinkedList<String>();

		for (String a : args) {
			if (a.equals(LANG_JAR)) {
				lang_jar_next = true;
				lang_tbl_next = false;
				trg_file_next = false;
				proj_dir_next = false;
			} else if (a.equals(LANG_TBL)) {
				lang_jar_next = false;
				lang_tbl_next = true;
				trg_file_next = false;
				proj_dir_next = false;
			} else if (a.equals(TRG_FILE)) {
				lang_jar_next = false;
				lang_tbl_next = false;
				trg_file_next = true;
			} else if (a.equals(PROJ_DIR)) {
				lang_jar_next = false;
				lang_tbl_next = false;
				trg_file_next = false;
				proj_dir_next = true;
			} else if (a.equals(TRG_ALL)) {
				lang_jar_next = false;
				lang_tbl_next = false;
				trg_file_next = false;
				proj_dir_next = false;
				this.all_targets = true;
			} else {
				if (lang_jar_next) {
					lang_jars.add(a);
				} else if (lang_tbl_next) {
					lang_tbl = a;
				} else if (trg_file_next) {
					targets.add(a);
				} else if (proj_dir_next) {
					projdir = a;
				}
			}
		}
		if (lang_jars.isEmpty()) {
			throw new IllegalArgumentException("Missing --lang-jar argument");
		} else if (lang_tbl == null) {
			throw new IllegalArgumentException("Missing --lang-tbl argument");
		} else if (targets.isEmpty() && !all_targets) {
			throw new IllegalArgumentException("Missing target files");
		} else if (projdir == null) {
			throw new IllegalArgumentException("Missing --proj-dir argument");
		}

		this.language_jars.addAll(lang_jars);
		this.language_tbl = lang_tbl;
		this.file_targets.addAll(targets);
		this.project_dir = projdir;

		if (this.all_targets) {
			this.file_targets.clear();
			final File project_dirf = new File(project_dir);
			Iterator<File> files = FileUtils.iterateFiles(project_dirf, extens, true);
			while(files.hasNext()){
				final String f = project_dirf.toURI().relativize(files.next().toURI()).toString();
//				this.file_targets.add(files.next().getPath());
				this.file_targets.add(f);
			}
		}

		System.out.println("Parameters:");
		System.out.println("\t JARS: " + this.language_jars);
		System.out.println("\t TBL: " + this.language_tbl);
		System.out.println("\t PROJ: " + this.project_dir);
		System.out.println("\t ALL? " + all_targets);
		System.out.println("\t FILES: " + this.file_targets);
		System.out.println("---------------------------------");
	}
	/*
	 * 
	 * General:
	 * 
	 * PretenderFront [LANGUAGE-OPTS] [PROJECT] [FILE_TO_RUN_AGAINST]
	 * 
	 * Language argument: --lang-jar [foobar.jar, foobar2.jar] --lang-tbl [foobar.tbl] --proj-dir
	 * ../../proj --targets [file1,file2]
	 */

}
