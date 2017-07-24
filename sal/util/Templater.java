
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

import java.io.*;

/**
 * Created by simon on 27/05/17.
 */
public class Templater {

    // Yet another simple template engine
    // text formats - by line
    // (~   to ~) (on one line)
    // (~name~)
    // on finding name, look it up map of providers
    // pass Printstream to to provider via method pass
    // provider writes to printstream

    protected PrintStream printStream;

    public Templater(PrintStream ps){
        this.printStream = ps;
    }

	public void print(Object val) {
			this.printStream.print(val);
	}
	
	public void printf(String format, Object... values) {
			this.printStream.printf(format, values);
	}
		
    public void toString(String key) { this.printStream.print(key); }

    public void render(String line) { // buffered reader, printstream,

        //String linex;
        //while((line = input.readLine()) != null )
            int startIndex = 0;
            int lineEnd = line.length();
            while (startIndex < lineEnd) {
                int next = line.indexOf("(~", startIndex);
                //System.out.printf("** Found (~ at %d\n", next);
                if (next < 0) {
                    this.printStream.append(line, startIndex, lineEnd);
                    break; // line read
                }

                // copy to here
                this.printStream.append(line, startIndex, next);
                next += 2;  // where rest of line begins
                // since lines end with \n, ~ can't be last char line
                if (line.charAt(next) == '~') {
                    this.printStream.append(line, next-2, next); // user really wanted (~
                    startIndex = next + 1;
                    //System.out.printf("** ignoring (~\n", next);
                    continue;
                }
                // token beginning at next  (~fugdwugd~)
                int rest = line.indexOf("~)", next);
                if (rest < 0) {
                    this.printStream.append(line, next, lineEnd);
                    break;
                }
                String key = line.substring(next, rest).trim();
                startIndex = rest + 2;
                this.toString(key);

        }
   }


}
