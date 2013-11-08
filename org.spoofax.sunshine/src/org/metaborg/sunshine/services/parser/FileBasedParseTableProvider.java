package org.metaborg.sunshine.services.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.metaborg.sunshine.CompilerException;
import org.metaborg.sunshine.Environment;
import org.metaborg.sunshine.parser.model.IParseTableProvider;
import org.spoofax.jsglr.client.ParseTable;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class FileBasedParseTableProvider implements IParseTableProvider {

	private final File file;
	private final boolean caching;

	private ParseTable table;

	public FileBasedParseTableProvider(File file) {
		this(file, true);
	}

	public FileBasedParseTableProvider(File file, boolean caching) {
		assert file != null;
		this.file = file;
		this.caching = caching;
	}

	@Override
	public ParseTable getParseTable() {
		if (this.table != null)
			return this.table;

		InputStream stream;
		ParseTable table;
		try {
			stream = new FileInputStream(file);
			table = Environment.INSTANCE().parseTableMgr.loadFromStream(stream);
		} catch (Exception e) {
			throw new CompilerException("Could not load parse table", e);
		}

		if (caching) {
			this.table = table;
		}
		return table;
	}

	public String toString() {
		return file.getAbsolutePath();
	};

}
