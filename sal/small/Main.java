/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package sal.small;

import sal.util.ErrorStream;
import sal.util.RE;
import sal.util.Lexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.file.Paths;

import static sal.small.Scope.*;
import static sal.small.Token.EOF;
import static sal.util.ErrorStream.errorCount;

/**
 * Created by simon on 28/05/17.
 */
public class Main {


    /////////////////// Make getting file details look prettier //////////////////////

    // recording details used in SMALL program for file name


    /**
     * Attempt to open a file with name given as string.
     * @param fileName string containing name of input file
     * @return A BufferedReader if the file can be opened, null if not.
     */
    private static BufferedReader getInputReader(String fileName) {
        FileReader inputFile = null;
        try {
            inputFile = new FileReader(fileName);
        } catch (Exception e) {
            return null;
        }
        return new BufferedReader(inputFile);
    }

    /**
     * Determine the name of the output file, the class name derived from it and the output folder.
     * @param fileName base name of the file, possibly with folder/extension etc.
     * @return A suitable name for an output (.j) file.
     */
    private static String getOutputFileName(String fileName) {
        // check for an extension and replace it with .j suffix
        int lastPart = fileName.lastIndexOf(".");
        if(lastPart >= 0) fileName = fileName.substring(0, lastPart);
        // store the last part of the name as the java class name
        String fileSep = System.getProperty("file.separator");
        lastPart = fileName.lastIndexOf(fileSep);
        // put the name without extension as the class name
        String progName;
        if(lastPart < 0) {
            progName = fileName;
        } else {
            int split = lastPart+fileSep.length();
            progName = fileName.substring(split);
        }
        putGlobal("CLASS NAME", progName);
        return progName + ".j";
    }



    /**
     * Create an printStream based on the output file name.
     * @param fileName
     * @return
     */
    private static PrintStream getOutputStream(String fileName) {
        PrintStream outputStream = null;
        try {
            outputStream = new PrintStream(fileName);
        } catch (Exception e) {
            return null;
        }
        return outputStream;
    }

    public static void main(String[] args) {

        int argsLength = args.length;
        if (argsLength == 0) {
            System.err.println("Valid arguments are: [-output folder] (filename )+ ");
            System.exit(1);
        }

        String outputDir;
        int argIndex = 0;
        String arg0 = args[0];
        // check for an output directory parameter
        if(argsLength >= 2 && arg0.length() > 1 && "-output".startsWith( arg0.toLowerCase())) {
            // found output directory
            outputDir = args[1];
            argIndex = 2;   // start filenames at arg 3
        } else {
            // not specified, use current working directory
            outputDir = System.getProperty("user.dir");
        }

		// check it has a trailing separator
        String fileSep = System.getProperty("file.separator");
        if(! outputDir.endsWith(fileSep)) outputDir += fileSep;
        

        // process each file in turn
        while(argIndex < argsLength) {
            String fileName = args[argIndex++];

            BufferedReader inputReader = getInputReader(fileName);
            if (inputReader == null) {
                System.err.printf("No file called %s\n", fileName);
                continue;   // go onto next file
            }

            // initialise the error count
            errorCount(0);
            ErrorStream.errorSource(fileName);  // to include file name in error messages
            Token.startLexer(inputReader);      // connect the scanner to the input file
            // parse the program
            // get access to the 'global' context
            Tree<Token> tree = Parse.program();
            if(errorCount() != 0) {
                System.out.printf("%d errors while parsing %s.  Code generation not attempted\n", errorCount(), fileName);
                continue;
            }
            String outputFile = getOutputFileName(fileName);
            System.out.printf("Compiling %s to %s\n", fileName, outputDir + outputFile);
            PrintStream outputStream = getOutputStream(outputDir + outputFile);
            if (outputStream == null) {
                System.err.printf("Couldn\'t create output file %s : Skipping code generation\n", outputFile);
            } else {
                // set the output stream for code generation
                CodeGen.writeProgram(outputStream, tree);// generate code
                // then close the output stream
                outputStream.close();
                if(errorCount() != 0) {
                    System.out.printf("%d errors during code generation\n", errorCount());
                    System.exit(1);
                }
                // no errors run the Jasmin assembler to generate a .class file.
                System.out.printf("Generated: %s\n", outputFile);
                String jasminArgs[] = new String[]{outputDir+outputFile};
                jasmin.Main.main(jasminArgs);
                
            }
        }
    }
}
