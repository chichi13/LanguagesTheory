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

package sal.util;

import java.io.PrintStream;

/**
 * Created by simon on 02/06/17.
 */
public class ErrorStream {

    private static PrintStream err = System.err;

   private static boolean stackTrace = false;

    public static void errorStream(PrintStream ps) {
        err = ps;
    }

    public static void stackTrace(boolean b) {
        stackTrace = b;
    }

    private static String errorSource = "";

    public static void errorSource(String errSource) {
        errorSource = (errSource == null) ? "" : errSource;
    }

    private static int errorCount = 0;

    public static void errorCount(int n) { errorCount = n; }

    public static void countError() { errorCount++; }

    public static int  errorCount() { return errorCount; }


    public static void log(int lineNumber) {
        if(errorSource.length() != 0)
            err.printf("%s : ", errorSource);
        if(lineNumber > 0)
            err.printf("(line %d) ", lineNumber);
        countError();
    }

    public static void log(int lineNumber, String format, Object... msg) {
        log(lineNumber);
        err.printf(format, msg);
    }

    public static void log(String format, Object... msg) {
        log(-1, format, msg);
    }

    public static void log(CharView view, String format, Object... msg) {
        log(-1, view, format, msg);
    }

    public static void log(int lineNumber, Throwable exception) {
        countError();
        if(lineNumber > 0) {
            err.printf("At about line %d ", lineNumber);
        }
        err.printf("Uncaught Exception Thrown: %s\n", exception.getMessage() );
        if(stackTrace) exception.printStackTrace(err);
    }

    public static void log(Throwable exception) {
        log(-1, exception);
    }

    static private final String SPACES    = "          ";
    static private final String HIGHLIGHT = "^^^^^^^^^^";


    private static void fill(int n, String chars) {
        int len = chars.length();
        while(n >= len) {
           err.append(chars);
           n -= len;
        }
        char ch = chars.charAt(0);
        while(n >= 0) {
            err.append(ch);
            n -= 1;
        }
    }

    public static void log(int lineNumber, CharView view, String format, Object... msg) {
        // first print the underlying file buffer
        CharSequence buffer = view.sequence();
        err.append(buffer);
        int length = buffer.length();
        if((length != 0) && (buffer.charAt(length-1) != '\n')) err.append('\n');
        // now use the view to highlight the error
        int start = view.getBeginIndex();
        fill(start, SPACES);
        fill(view.length(), HIGHLIGHT);
        err.println();
        err.print("Error ");
        if(lineNumber > 0) err.printf("Line %d, ", lineNumber);
        err.printf("Column %d: ", start);
        err.printf(format, msg);
        countError();
    }



}
