package edu.odu.cs.cwm.documents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Miscellaneous utility routines.
 * 
 * @author zeil
 *
 */
public final class Utils {

	/**
	 * Given a message consisting of several lines, displays a selected line
	 * in context, highlighting an indicated column position.
	 * 
	 * @param message   a message of one or more lines
	 * @param lineNumber a line number containing a column of interest
	 * @param column  a column number, or -1 if the column is irrelevant
	 * @return  a display consisting of 2 lines before the indicated
	 *           line, the indicated line (highlighted) split at the indicated
	 *           column, and two lines after the indicated line.
	 */
	public static String extractContext (
			final String message, 
			final int lineNumber, 
			final int column) {
		StringBuilder result = new StringBuilder();
		BufferedReader rdr = new BufferedReader(new StringReader(message));
		final int contextWidth = 2;
		int lineNum = 0;
		while (lineNum < lineNumber + contextWidth + 1) {
			String line;
			try {
				line = rdr.readLine();
			} catch (IOException e) {
				line = null;
			}
			if (line == null) {
				break;
			}
			if (lineNum == lineNumber) {
				result.append("*** ");
				result.append(line);
				result.append("\n");
				if (column >= 0 && column < line.length()) {
					result.append("*** ");
					for (int i = 0; i < column; ++i) {
						result.append(' ');
					}
					result.append("^\n");
				}
				
			} else if ((lineNum >= lineNumber - contextWidth)
					&& (lineNum <= lineNumber + contextWidth)) {
				result.append("    ");
				result.append(line);
				result.append("\n");
			}
			++lineNum;
		}
		
		
		return result.toString();
	}
	
	
	/**
	 * Uncallable constructor.
	 */
	private Utils() {
		
	}
}
