/**
 * 
 */
package edu.odu.cs.cwm.macroproc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Transforms a text file via application of macros.  Supported commands are
 * 
 * #ifdef macroName    or  #if macroName
 * #else
 * #endif
 * 
 * #include (filename)
 * 
 * The above are all analogous to the familiar C/C++ pre-processor 
 * 
 * #define (macroName) (argslist) (macrobody)
 * 
 * defines a macro with the given name, a possibly empty, comma-separated arguments list, and
 * a body. In this command (and in the #include, above), the ( ) may be any matching pair of: (), [], 
 * {}, or <>.  The macro body may span multiple lines and the line feeds are considered part of the
 * body.
 * 
 * Examples of macro definitions:
 *   #define (authorName) () (Steven Zeil)
 *   #define (slide) (title) <
 *   \begin{slide}{title}
 *   >
 *   #define (picture)(file,sizepct) {<img src="file" style="max-width: sizepct%"/>}
 * 
 * @author zeil
 *
 */
public class MacroProcessor {
	
	private String commandPrefix = "#";
	private ArrayList<Macro> macros;
	private HashSet<String> macroNames;
	private StringBuffer accumulated;
	
	private class InputState {
		public char matching;
		public boolean suppressed;
		public Macro incompleteMacro;
		
		public InputState (char m, boolean suppr) {
			matching = m;
			suppressed = suppr;
			incompleteMacro = null;
		}
	}

	private ArrayList<InputState> stack;
	
	/**
	 * Defines a new macro processor with no currently defined macros,
	 * using # as the command prefix
	 */
	public MacroProcessor () {
		macros = new ArrayList<Macro>();
		macroNames = new HashSet<String>();
		stack = new ArrayList<>();
		stack.add(new InputState(' ', false));
		accumulated = new StringBuffer();
	}

	/**
	 * Defines a new macro processor with no currently defined macros
	 * @param commandPrefix  character to use as the command prefix
	 */
	public MacroProcessor (String commandPrefix) {
		macros = new ArrayList<Macro>();
		macroNames = new HashSet<String>();
		this.commandPrefix = commandPrefix;
		stack = new ArrayList<>();
		stack.add(new InputState(' ', false));
		accumulated = new StringBuffer();
	}
	
	/**
	 * Add a new macro to the processor state.
	 * @param macro macro
	 */
	public void defineMacro (Macro macro) {
		//System.err.println ("Defining " + macro);
		macros.add(macro);
		macroNames.add(macro.name);
	}
	
	/**
	 * Process a single line of input, including commands and macro expansions
	 * 
	 * @param line input line
	 * @return processed input or null if line generates no input
	 */
	private String processLine (String line) {
		InputState topState = stack.get(stack.size()-1);
		if (topState.matching != ' ') {
			int pos = line.indexOf(topState.matching);
			if (pos < 0) {
				topState.incompleteMacro.body = topState.incompleteMacro.body + "\n" + line;
				return null;
			} else {
				topState.incompleteMacro.body = topState.incompleteMacro.body + "\n" + line.substring(0, pos);
				stack.remove(stack.size()-1);
				return null;
			}
		}
		
		int pos = 0;
		while (pos < line.length() && line.charAt(pos) == ' ')
			++pos;
		String trimmedString = line.substring(pos);
		if (trimmedString.startsWith(commandPrefix)) {
			String accumulation = accumulated.toString();
			String result = (accumulation.length() > 0) ? processMacros(accumulation) : null;
			accumulated = new StringBuffer();
			
			if (trimmedString.startsWith(commandPrefix + "include")) {
				return ((result == null) ? "" : result) + processInclude(trimmedString);
			} else if (trimmedString.startsWith(commandPrefix + "ifdef")) {
			    processIfDef (trimmedString, "ifdef");
				return result;
			} else if (trimmedString.startsWith(commandPrefix + "if")) {
			    processIfDef (trimmedString, "if");
				return result;
			} else if (trimmedString.startsWith(commandPrefix + "else")) {
				processElse (trimmedString);
				return result;
			} else if (trimmedString.startsWith(commandPrefix + "endif")) {
				processEndif (trimmedString);
				return result;
			} else if (trimmedString.startsWith(commandPrefix + "define")) {
				processDefine (trimmedString);
				return result;
			} else {
				accumulated.append(accumulation);
				accumulated.append(line);
				accumulated.append(System.lineSeparator());
			}
		} else if (!topState.suppressed) {
			accumulated.append(line);
			accumulated.append(System.lineSeparator());
		}
		return null;
/*
		if (!topState.suppressed){
			return processMacros (line);
		} else {
			return null;
		}
		*/
	}
	
	private String flush()
	{
		String accumulation = accumulated.toString();
		accumulated = new StringBuffer();
		return (accumulation.length() > 0) ? processMacros(accumulation) : "";
	}
	
	
	private String processMacros(String line) {
		String result = line;
		for (Macro m: macros) {
			result = m.apply(result);
		}
		return result;
	}

	private void processDefine(String defineCommandStart) {
		InputState topState = stack.get(stack.size()-1);
		if (!topState.suppressed) {
			int start = commandPrefix.length() + "define".length();
			ParseResult pr = parseEnclosure(defineCommandStart, start);
			if (pr == null)
				return;
			String name = pr.selectedString;
			pr = parseEnclosure(defineCommandStart, pr.stoppingPosition+1);
			if (pr == null)
				return;
			String[] args = (pr.selectedString.length() > 0) ? pr.selectedString.split(",") : new String[0];
			start = pr.stoppingPosition+1;
			pr = parseEnclosure(defineCommandStart, start);
			if (pr != null) {
				Macro m = new Macro(name, Arrays.asList(args), pr.selectedString);
				defineMacro(m);
			} else {
				InputState state = new InputState(' ', true);
				char opener = ' ';
				while (start < defineCommandStart.length() && opener == ' ') {
					char c = defineCommandStart.charAt(start);
					if (c == '(' || c == '[' || c == '{' || c == '<')
						opener = c;
					++start;
				}
				if (start > defineCommandStart.length()) {
					return;
				}
				char closer = ' ';
				switch (opener) {
				case '(': closer = ')'; break;
				case '[': closer = ']'; break;
				case '{': closer = '}'; break;
				case '<': closer = '>'; break;
				}
				Macro m = new Macro (name, Arrays.asList(args), defineCommandStart.substring(start));
				defineMacro(m);
				state.matching = closer;
				state.incompleteMacro = m;
				stack.add(state);
			}
			
		}
	}

	private void processEndif(String endifCommand) {
		if (stack.size() > 1)
			stack.remove(stack.size()-1);
	}

	private void processElse(String elseCommand) {
		if (stack.size() > 1) {
			InputState topState = stack.get(stack.size()-1);
			InputState priorState = stack.get(stack.size()-2);
			stack.remove(stack.size()-1);
			stack.add (new InputState(' ', priorState.suppressed || !topState.suppressed));
		}
	}

    private void processIfDef(String ifDefCommand, String ifdefLexeme) {
	    InputState topState = stack.get(stack.size()-1);
	    if (topState.suppressed) {
		// Doesn't matter if the condition is true or not
		stack.add (new InputState(' ', true));
	    } else {
		int start = commandPrefix.length() + ifdefLexeme.length();
		while (start < ifDefCommand.length() && ifDefCommand.charAt(start) == ' ')
		    ++start;
		int stop = start;
		while (stop < ifDefCommand.length() && ifDefCommand.charAt(stop) != ' ')
		    ++stop;
		if (start >= ifDefCommand.length() || stop == start) {
		    stack.add (new InputState(' ', true));
		} else {
		    String macroName = ifDefCommand.substring(start, stop);
		    stack.add (new InputState(' ', !macroNames.contains(macroName)));
		}
	    }
	}
    
	private String processInclude(String includeCommand) {
		InputState topState = stack.get(stack.size()-1);
		if (!topState.suppressed) {
			ArrayList<InputState> savedStack = stack;
			stack = new ArrayList<>();
			stack.add(new InputState(' ', false));
			String fileName;
			try {
			fileName = parseEnclosure(includeCommand, 
				commandPrefix.length() + "include".length()).selectedString;
			} catch (Exception e) {
			    return "**Error " + includeCommand 
				+ "\n: " + commandPrefix + "\n**";
			}
			File input = new File(fileName);
			if (input.exists()) {
				String result = process(input);
				stack = savedStack;
				return result;
			} else {
				return "\n\n** Missing file: " + fileName + " **\n\n";
			}
		} else
			return null;
	}

	public class ParseResult {
		String selectedString;
		int stoppingPosition;
		
		public ParseResult (String sel, int stop)
		{
			selectedString = sel;
			stoppingPosition = stop;
		}
	}
	/**
	 * Scan forward to next non-black character. Should be one of: ([{<.
	 * Extract the string between that and the appropriate closing character.
	 * 
	 * @param includeCommand
	 * @param startingAt
	 * @return enclosed string or null if one cannot be found
	 */
	private ParseResult parseEnclosure(String includeCommand, int startingAt) {
		int start = startingAt;
		char opener = ' ';
		while (start < includeCommand.length() && includeCommand.charAt(start) == ' ') {
			++start;
		}
		if (start < includeCommand.length())
			opener = includeCommand.charAt(start);
		char closer = ' ';
		switch (opener) {
		case '(': closer = ')'; break;
		case '{': closer = '}'; break;
		case '[': closer = ']'; break;
		case '<': closer = '>'; break;
		}
		if (closer == ' ')
			return null;
		int stop = start;
		while (stop < includeCommand.length() && includeCommand.charAt(stop) != closer) {
			++stop;
		}
		if (stop < includeCommand.length())
			return new ParseResult(includeCommand.substring(start+1, stop), stop);
		else
			return null;
	}

	public String process (BufferedReader input) throws IOException {
		StringBuffer results = new StringBuffer();
		String line = input.readLine();
		while (line != null) {
			String processed = processLine(line);
			if (processed != null) {
				results.append(processed);
				results.append(System.lineSeparator());
			}
			line = input.readLine();
		}
		results.append(flush());
		String result = results.toString();
		return result;
	}

	
	public String process (String inputString) {
		String results = "";
		BufferedReader input = null;
		try {
			input = new BufferedReader(new StringReader (inputString));
			results = process (input);
		} catch (IOException e) {
			System.err.println ("**Unexpected I/O error in " + inputString + ": " + e);
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException e) {
				System.err.println ("**Unexpected I/O error: " + e);
			}
		}
		return results;
	}

	public String process (File inputFile) {
		String results = "";
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader (inputFile));
			results = process (input);
		} catch (FileNotFoundException e) {
			System.err.println ("**Could not open " + inputFile.getAbsolutePath());
		} catch (IOException e) {
			System.err.println ("**Unexpected I/O error in " + inputFile.getAbsolutePath() + ": " + e);
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException e) {
				System.err.println ("**Unexpected I/O error in " + inputFile.getAbsolutePath() + ": " + e);
			}
		}
		return results;
	}
	

	/**
	 * Driver for macro processor.  Accepts args:
	 *   -c?       changes the macro prefix string (default is '#')
	 *   -Dmacro   defines a macro name
	 *   -iFile    processes File, ignoring output but keeping any macro definitions
	 *   -oFile    directs output to the indicated file
	 *   fileName  input file name
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String inputFileName = null;
		String outputFileName = null;
		MacroProcessor processor = new MacroProcessor();
		for (int i = 0; i < args.length; ++i) {
			if (args[i].startsWith("-c")) {
				String prefix = args[i].substring(2);
				processor = new MacroProcessor(prefix);
			} else if (args[i].startsWith("-D")) {
				String macroName = args[i].substring(2);
				processor.defineMacro(new Macro(macroName, ""));
			} else if (args[i].startsWith("-o")) {
				String fileName = args[i].substring(2);
				outputFileName = fileName;
			} else if (args[i].startsWith("-i")) {
				String fileName = args[i].substring(2);
				File input = new File(fileName);
				processor.process(input);
			} else {
				inputFileName = args[i];
			}
		}
		
		BufferedReader mainInput;
		if (inputFileName != null) {
			mainInput = new BufferedReader(new FileReader(inputFileName));
		} else {
			mainInput = new BufferedReader(new InputStreamReader(System.in));
		}
		BufferedWriter mainOutput;	
		if (outputFileName != null) {
			mainOutput = new BufferedWriter(new FileWriter(outputFileName));
		} else {
			mainOutput = new BufferedWriter(new OutputStreamWriter(System.out));
		}
		transformText (mainInput, mainOutput, processor);
		if (outputFileName != null)
			mainOutput.close();
		if (inputFileName != null)
			mainInput.close();
	}

	private static void transformText(BufferedReader input,
			BufferedWriter output, MacroProcessor processor) throws IOException {
		String line = input.readLine();
		while (line != null) {
			String processed = processor.processLine(line);
			if (processed != null) {
				output.write(processed);
				output.write(System.lineSeparator());
			}
			line = input.readLine();
		}
		output.write(processor.flush());
		output.write(System.lineSeparator());
	}

}
