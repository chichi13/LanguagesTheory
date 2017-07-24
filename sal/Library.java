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

package sal;

/*
 * Created by simon on 30/06/17.
 */

import java.util.Scanner;

public class Library {

        private static Scanner scanInput = new Scanner(System.in);

        public static void print(String s) {
            System.out.print(s);
        }

        public static void print(int n) {
            System.out.print(n);
        }

        public static int readInt() {
            return scanInput.nextInt();
        }

        public static String readStr() {
            return scanInput.nextLine();
        }

        public static int abs(int n) { return java.lang.Math.abs(n); }

        public static int max(int n, int m) { return java.lang.Math.max(n, m); }

        public static int min(int n, int m) { return java.lang.Math.min(n, m); }
        
        public static String concat(String a, String b) { return a + b; }

		public static String toStr(String format, int n) { return String.format(format, n); }

		public static String toStr(int n) { return Integer.toString(n); }

		public static int toInt(String s) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return Integer.MIN_VALUE;
			}
		}

		public static String left(String s, int n) {
			if(n <= 0) return "";
			if(n > s.length()) return s;	// whole string
			return s.substring(0, n);
		}

		public static String right(String s, int n) {
			if(n <= 0) return "";
			int start = s.length() - n;
			if(n < 0) n = 0;	// whole string
			return s.substring(n);
		}
		
		public static int strCmp(String s1, String s2) 
		{
			return s1.compareTo(s2);
		}
	 
		public static int len(String s) {
			return s.length();
		}

}
