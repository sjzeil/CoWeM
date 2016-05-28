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
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * defines a macro with the given name, a possibly empty, comma-separated
 * arguments list, and a body. In this command (and in the #include, above),
 * the ( ) may be any matching pair of: (), [],  {}, or &lt;&gt;.
 * The macro body may span multiple lines and the line feeds are considered
 * part of the body.
 * 
 * Examples of macro definitions:
 *   #define (authorName) () (Steven Zeil)
 *   #define (slide) (title) {
 *   \begin{slide}{title}
 *   }
 *   #define (picture)(file,sizepct) {&lt;img src="file"
 *            style="max-width: sizepct%"/&gt;}
 * 
 * @author zeil
 *
 */
public class MacroProcessor {

	/**
	 * Character used to introduce a basic macro command.
	 */
	private String commandPrefix = "#";
	
	/**
	 * The macros defined in this processor.
	 */
	private List<Macro> macros;
	
	/**
	 * Names of all defined macros.
	 */
	private Set<String> macroNames;
	
	/**
	 * Partially processed text.
	 */
	@SuppressWarnings("PMD.AvoidStringBufferField")
	private StringBuffer accumulated;


	/**
	 * Stack of states during document processing.
	 */
	private List<InputState> stack;
	
	
	/**
     * For logging error messages.
     */
    private static Logger logger 
       = LoggerFactory.getLogger(MacroProcessor.class);


    /**
     * Describes a stackable state during macro processing.
     * 
     * @author zeil
     */
    private class InputState {
        // CHECKSTYLE IGNORE VisibilityModifierCheck FOR NEXT 20 LINES
        /**
         * Indicates a character that opened a macro parameter list
         * or macro body that needs to be matched to close that construct.
         */
        public char matching;
        
        /**
         * Is copying of text to output suppresed due to a failed #if test? 
         */
        public boolean suppressed;
        
        /**
         * Holds a macro that we are currnetly paring.
         */
        public Macro incompleteMacro;

        /**
         * Create a state.
         * @param matching0 matching character
         * @param suppr true to suppress copying of text to output
         */
        InputState (final char matching0, final boolean suppr) {
            matching = matching0;
            suppressed = suppr;
            incompleteMacro = null;
        }

    }

    
    
    /**
	 * Defines a new macro processor with no currently defined macros,
	 * using # as the command prefix.
	 */
	public MacroProcessor () {
		macros = new ArrayList<Macro>();
		macroNames = new HashSet<String>();
		stack = new ArrayList<>();
		stack.add(new InputState(' ', false));
		accumulated = new StringBuffer();
	}

	/**
	 * Defines a new macro processor with no currently defined macros.
	 * @param commandPrefix0  character to use as the command prefix
	 */
	public MacroProcessor (final String commandPrefix0) {
		macros = new ArrayList<Macro>();
		macroNames = new HashSet<String>();
		this.commandPrefix = commandPrefix0;
		stack = new ArrayList<>();
		stack.add(new InputState(' ', false));
		accumulated = new StringBuffer();
	}

	/**
	 * Add a new macro to the processor state.
	 * @param macro macro
	 */
	public final void defineMacro (final Macro macro) {
		//System.err.println ("Defining " + macro);
		macros.add(macro);
		macroNames.add(macro.getName());
	}

	/**
	 * Process a single line of input, including commands and macro expansions.
	 * 
	 * @param line input line
	 * @return processed input or null if line generates no input
	 */
	private String processLine (final String line) {
		InputState topState = stack.get(stack.size() - 1);
		if (topState.matching != ' ') {
			int pos = line.indexOf(topState.matching);
			if (pos < 0) {
				topState.incompleteMacro.setBody(
				        topState.incompleteMacro.getBody() + "\n" + line);
				return null;
			} else {
				topState.incompleteMacro.setBody(
				        topState.incompleteMacro.getBody() 
                + "\n" + line.substring(0, pos));
				stack.remove(stack.size() - 1);
				return null;
			}
		}

		int pos = 0;
		while (pos < line.length() && line.charAt(pos) == ' ') {
			++pos;
		}
		String trimmedString = line.substring(pos);
		if (trimmedString.startsWith(commandPrefix)) {
			String accumulation = accumulated.toString();
			String result = null;
			if (accumulation.length() > 0) { 
				result = processMacros(accumulation);
			}
			accumulated = new StringBuffer();

			if (trimmedString.startsWith(commandPrefix + "include")) {
				if (result == null) {
					result = "";
				}
				return result + processInclude(trimmedString);
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

	/**
	 * Flush accumulated output to the output, clearing the accumulated buffer.
	 * @return String to be flushed
	 */
	private String flush() {
		String accumulation = accumulated.toString();
		accumulated = new StringBuffer();
		if (accumulation.length() > 0) {
			return processMacros(accumulation);
		} else {
			return "";
		}
	}


	/**
	 * Process any macros found in a line of text.
	 * @param line Input text
	 * @return output from macro processing.
	 */
	private String processMacros(final String line) {
		String result = line;
		for (Macro m: macros) {
			result = m.apply(result);
		}
		return result;
	}

	/**
	 * Parse and process a #define command.
	 * @param defineCommandStart the opening phrase of the command
	 */
	private void processDefine(final String defineCommandStart) {
		InputState topState = stack.get(stack.size() - 1);
		if (!topState.suppressed) {
			int start = commandPrefix.length() + "define".length();
			ParseResult pr = parseEnclosure(defineCommandStart, start);
			if (pr == null) {
				return;
			}
			String name = pr.getSelectedString();
			pr = parseEnclosure(defineCommandStart, 
			        pr.getStoppingPosition() + 1);
			if (pr == null) {
				return;
			}
			// CHECKSTYLE IGNORE AvoidInlineConditionals FOR NEXT 2 LINES
			String[] args = (pr.getSelectedString().length() > 0) 
			        ? pr.getSelectedString().split(",") : new String[0];
			start = pr.getStoppingPosition() + 1;
			pr = parseEnclosure(defineCommandStart, start);
			if (pr != null) {
				Macro m = new Macro(name, Arrays.asList(args),
				        pr.getSelectedString());
				defineMacro(m);
			} else {
				InputState state = new InputState(' ', true);
				char opener = ' ';
				while (start < defineCommandStart.length() && opener == ' ') {
					char c = defineCommandStart.charAt(start);
					if (c == '(' || c == '[' || c == '{' || c == '<') {
						opener = c;
					}
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
				default:
				}
				Macro m = new Macro (name, Arrays.asList(args), 
				        defineCommandStart.substring(start));
				defineMacro(m);
				state.matching = closer;
				state.incompleteMacro = m;
				stack.add(state);
			}

		}
	}

	/**
	 * Parse and process an #endif command.
	 * @param endifCommand  Lexeme of the command.
	 */
	private void processEndif( final String endifCommand) {
		if (stack.size() > 1) {
			stack.remove(stack.size() - 1);
		}
	}

	/**
	 * Parse and process an #else command.
	 * @param elseCommand  Lexeme of the command.
	 */
	private void processElse(final String elseCommand) {
		if (stack.size() > 1) {
			InputState topState = stack.get(stack.size() - 1);
			InputState priorState = stack.get(stack.size() - 2);
			stack.remove(stack.size() - 1);
			stack.add (new InputState(' ', 
			        priorState.suppressed || !topState.suppressed));
		}
	}

    /**
     * Parse and process an #ifdef command.
     * @param ifDefCommand  Lexeme of the command.
     * @param ifdefLexeme Lexeme of the opening word of the command/
     */
	private void processIfDef(final String ifDefCommand, 
	                          final String ifdefLexeme) {
		InputState topState = stack.get(stack.size() - 1);
		if (topState.suppressed) {
			// Doesn't matter if the condition is true or not
			stack.add (new InputState(' ', true));
		} else {
			int start = commandPrefix.length() + ifdefLexeme.length();
			while (start < ifDefCommand.length() 
			        && ifDefCommand.charAt(start) == ' ') {
				++start;
			}
			int stop = start;
			while (stop < ifDefCommand.length() 
			        && ifDefCommand.charAt(stop) != ' ') {
				++stop;
			}
			if (start >= ifDefCommand.length() || stop == start) {
				stack.add (new InputState(' ', true));
			} else {
				String macroName = ifDefCommand.substring(start, stop);
				stack.add (new InputState(' ', 
				        !macroNames.contains(macroName)));
			}
		}
	}

    /**
     * Parse and process an #include command.
     * @param includeCommand  Lexeme of the command.
     * @return String to be inserted in place of the command
     */
	private String processInclude(final String includeCommand) {
		InputState topState = stack.get(stack.size() - 1);
		if (!topState.suppressed) {
			List<InputState> savedStack = stack;
			stack = new ArrayList<>();
			stack.add(new InputState(' ', false));
			String fileName;
			try {
				fileName = parseEnclosure(includeCommand, 
					commandPrefix.length() 
					  + "include".length()).getSelectedString();
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
		} else {
			return null;
		}
	}

	/**
	 * A description of an attempted command parse.
	 * 
	 * @author zeil
	 *
	 */
	public class ParseResult {
	    
	    /**
	     * A parsed construct.
	     */
		private String selectedString;
		
		/**
		 * Position at which the recognized construct ended.
		 */
		private int stoppingPosition;

		/**
		 * Create a parse result.
		 * @param sel the selected string
		 * @param stop the stopping position
		 */
		public ParseResult (final String sel, final int stop) {
			selectedString = sel;
			stoppingPosition = stop;
		}

        /**
         * @return the selectedString
         */
        final String getSelectedString() {
            return selectedString;
        }

        /**
         * @param selectedString0 the selectedString to set
         */
        final void setSelectedString(final String selectedString0) {
            this.selectedString = selectedString0;
        }

        /**
         * @return the stoppingPosition
         */
        final int getStoppingPosition() {
            return stoppingPosition;
        }

        /**
         * @param stoppingPosition0 the stoppingPosition to set
         */
        final void setStoppingPosition(final int stoppingPosition0) {
            this.stoppingPosition = stoppingPosition0;
        }
	}
	
	
	/**
	 * Scan forward to next non-black character. Should be one of: ([{<.
	 * Extract the string between that and the appropriate closing character.
	 * 
	 * @param commandString command that is being parsed
	 * @param startingAt position within string to start parsing
	 * @return enclosed string or null if one cannot be found
	 */
	private ParseResult parseEnclosure(final String commandString, 
	                                   final int startingAt) {
		int start = startingAt;
		char opener = ' ';
		while (start < commandString.length() 
		        && commandString.charAt(start) == ' ') {
			++start;
		}
		if (start < commandString.length()) {
			opener = commandString.charAt(start);
		}
		char closer = ' ';
		switch (opener) {
		case '(': closer = ')'; break;
		case '{': closer = '}'; break;
		case '[': closer = ']'; break;
		case '<': closer = '>'; break;
		default:
		}
		if (closer == ' ') {
			return null;
		}
		int stop = start;
		while (stop < commandString.length() 
		        && commandString.charAt(stop) != closer) {
			++stop;
		}
		if (stop < commandString.length()) {
			return new ParseResult(commandString.substring(start + 1, stop), 
			                       stop);
		} else {
			return null;
		}
	}

	/**
	 * Process a block of text obtained from a reader. Processing can both
	 * alter the text (macro substitution) and affect the state of the
	 * processor by defining new macros.
	 * @param input  source of text to be processed
	 * @return processed text
	 * @throws IOException on failure of reader
	 */
	public final String process (final BufferedReader input)
	        throws IOException {
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
		return results.toString();
	}

	/**
	 * Process a block of text. Processing can both
     * alter the text (macro substitution) and affect the state of the
     * processor by defining new macros.
     * 
	 * @param inputString text to process
	 * @return processed text
	 */
	public final String process (final String inputString) {
		String results = "";
		BufferedReader input = null;
		try {
			input = new BufferedReader(new StringReader (inputString));
			results = process (input);
		} catch (IOException e) {
		    logger.error("**Unexpected I/O error in " + inputString, e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
			    logger.error("**Unexpected I/O error", e);
			}
		}
		return results;
	}

    /**
     * Process a block of text obtained from a file. Processing can both
     * alter the text (macro substitution) and affect the state of the
     * processor by defining new macros.
     * 
     * @param inputFile text to process
     * @return processed text
     */
	public final String process (final File inputFile) {
		String results = "";
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader (inputFile));
			results = process (input);
		} catch (FileNotFoundException e) {
			logger.error ("**Could not open " + inputFile.getAbsolutePath(), e);
		} catch (IOException e) {
			logger.error ("**Unexpected I/O error in " 
		         + inputFile.getAbsolutePath(), e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				logger.error ("**Unexpected I/O error in " 
				    + inputFile.getAbsolutePath(), e);
			}
		}
		return results;
	}


	/**
	 * Driver for macro processor.  Accepts args:
	 *   -c?       changes the macro prefix string (default is '#')
	 *   -Dmacro   defines a macro name
	 *   -iFile    processes File, ignoring output but keeping any 
	 *               macro definitions
	 *   -oFile    directs output to the indicated file
	 *   fileName  input file name
	 * @param args Command line parameters as described above
	 * @throws IOException on input failure
	 */
	public static void main(final String[] args) throws IOException {
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
			mainOutput = new BufferedWriter(
					new OutputStreamWriter(System.out));
		}
		transformText (mainInput, mainOutput, processor);
		if (outputFileName != null) {
			mainOutput.close();
		}
		if (inputFileName != null) {
			mainInput.close();
		}
	}

	/**
	 * Transforms text using a macro processor.
	 * @param input  input text source
	 * @param output output text destination
	 * @param processor the macro processor to use
	 * @throws IOException on input failure
	 */
	private static void transformText(final BufferedReader input,
			final BufferedWriter output, final MacroProcessor processor)
					throws IOException {
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
