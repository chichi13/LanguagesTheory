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

import java.util.Collection;
import java.util.Objects;

/**
 * Created by simon on 22/05/17.
 */
public class Fail {

    public static class LogicError extends RuntimeException {

        public LogicError(String... messages) {
            super(String.join(" ",messages));
        }
    }

    public static void  failIf(boolean condition, String... message) {
        if(condition) throw new LogicError(message);
    }

    public static void  failIfNot(boolean condition, String... message) {
        failIf(! condition, message);
    }

    public static void  failNull(Object o, String... message) {
        failIf(o == null, message);
    }

    public static void  failNotNull(Object o, String... message) {
        failIf(o != null, message);
    }

    public static void  failNotIn(int n1, int val, int n2, String... message) {
        failIf(val < n1 || n2 <= val, message);
    }

    public static void  failIn(int n1, int val, int n2, String... message) {
        failIf(n1 <= val && val <= n2, message);
    }

    public static void  failEmpty(String s, String... message) {
        failIf((s == null) || (s.length() == 0), message);
    }

    public static <T> void  failEmpty(Collection<T> clt, String... message) {
        failIf(clt == null || clt.isEmpty(), message);
    }

    public static <T> void  failEmpty(T[] array, String... message) {
        failIf((array == null) || (array.length == 0), message);
    }

    public static <T> T safe(T val) {
        failNull(val, "Null pointer in \"safe()\"");
        return val;
    }



}
