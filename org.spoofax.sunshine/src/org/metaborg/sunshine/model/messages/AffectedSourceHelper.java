package org.metaborg.sunshine.model.messages;

import java.util.Arrays;

import org.metaborg.spoofax.core.messages.ISourceRegion;

public class AffectedSourceHelper {
	public static final char AFFECTED = '^';
	public static final char BLANK = ' ';
	public static final char TAB = '\t';
	public static final char NEWLINE = '\n';

	public static String affectedSourceText(ISourceRegion region,
			String sourceText, String indentation) {
		final String[] affectedRows = affectedRows(sourceText,
				region.startRow() + 1, region.endRow() + 1);

		if (affectedRows == null || affectedRows.length == 0)
			return TAB + "(code region unavailable)" + NEWLINE;

		final String[] damagedLines = weaveAffectedLines(affectedRows,
				region.startColumn() + 1, region.endColumn() + 1);
		final StringBuilder sb = new StringBuilder();
		for (String dl : damagedLines) {
			sb.append(indentation + dl + NEWLINE);
		}
		return sb.toString();
	}

	private static String[] affectedRows(String originText, int beginLine,
			int endLine) {
		if (originText.length() > 0 && beginLine > 0 && endLine > 0) {
			final String[] lines = originText.split("\\r?\\n");
			if (beginLine - 1 > lines.length)
				return new String[0];
			return Arrays.copyOfRange(lines, beginLine - 1, endLine);
		} else
			return new String[0];
	}

	private static String[] weaveAffectedLines(String[] lines, int beginColumn,
			int endColumn) {
		String[] affectedRows = new String[lines.length * 2];
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			affectedRows[i] = line;
			int beginOffset = i == 0 ? beginColumn - 1 : 0;
			int endOffset = i + 1 == lines.length ? endColumn - 1 : line
					.length();
			char[] affectedChars = line.toCharArray();
			for (int j = 0; j < affectedChars.length; j++) {
				if (beginOffset <= j && endOffset >= j) {
					affectedChars[j] = AFFECTED;
				} else {
					char dc = affectedChars[j];
					if (dc != TAB && dc != BLANK) {
						dc = BLANK;
					}
					affectedChars[j] = dc;
				}
			}
			affectedRows[i + 1] = new String(affectedChars);
		}
		return affectedRows;
	}
}
