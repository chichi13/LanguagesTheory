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

import java.io.*;

/**
 * Created by simon on 22/05/17.
 */
public class Buffered {
    // simple read/write from/to keyboard/screen or files

    public static BufferedReader fromStream(InputStream is) {
        return new BufferedReader(new InputStreamReader(is));
    }

    public static BufferedReader fromStream() {
        return fromStream(System.in);
    }

    public static BufferedReader fromFile(String fname) {
        try {
            return new BufferedReader(new FileReader(fname));
        } catch (FileNotFoundException ex) {
            return null;    // not good practice!
        }
    }

    public static BufferedReader fromString(String s) {
        return new BufferedReader(new StringReader(s));
    }

    public static BufferedWriter toStream(OutputStream out) {
        return new BufferedWriter(new OutputStreamWriter(out));
    }

    public static BufferedWriter toStream() {
        return new BufferedWriter(new OutputStreamWriter(System.out));
    }

    public static BufferedWriter toFile(String fname) {
        try {
            return new BufferedWriter(new FileWriter(fname));
        } catch (java.io.IOException ex) {
            return null;    // not good practice!
        }
    }

    public static BufferedWriter  toAString() {
        return new BufferedWriter(new StringWriter());
    }


}
