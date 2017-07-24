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

/**
 * Created by simon on 22/05/17.
 */



import java.util.regex.Pattern;

/**
 * <b>RE</b> provides a collection of static values and methods for manipulating
 * Java regular expressions (REs). The intention is to make it easier to 'read'
 * an RE - something which is often made more difficult both by the syntax of
 * the RE itself and the need to escape characters.
 * <p>
 * To take a simple example, a tab character an RE is written as <i>\t</i>, to
 * include it in a Java String literal we write "\\t". A backslash character
 * itself is <i>\\</i> in an RE and "\\\\" as a Java literal. In addition,
 * spaces are significant in REs and much of the notation is designed to use a
 * minimal number of characters - often not obvious one.
 * <p>
 * A common solution to this is to write complex REs from the bottom up using
 * final String values. For example a numeric literal in hex, decimal, or octal,
 * could be written as:
 * <pre>
 * final String DEC     = "[1-9][0-9]*";
 * final String OCTAL   = "0[0-7]*";
 * final String HEX     = "(#|0[xX])[0-9a-fA-F]+";
 * final String NUMBER  = DEC+"|"+OCTAL+"|"HEX;
 * </pre> The package RE provides useful named values for standard patterns and
 * some methods for combining them. Here is the above as a single expression
 * <pre>
 * final String NUMBER = oneOf( "[1-9]"+any(dec),
 *                             "0"+any(oct),
 *                              one("#", "0[xX]")+some(hex)
 *                          );
 * </pre> The methods are based on the following:<ul>
 * <li> The concatenation operator (+) is used to show sequences within an RE.
 * <li> {@link #oneOf(java.lang.String[]) oneOf} can be used to group alternatives.
 * Each argument becomes an RE. ',' being replaced by '|' in the result.
 * </ul>
 *
 * @author simon
 */
public class RE {

    /** Escape (the first character of) a string.
     *
     * @param c Any string, the first character of which will be 'escaped' by prefixing with BS
     * @return The escaped string
     */
    public final static String esc(String c) { return '\\'+c; }

    /**
     * Generate any character as UNICODE escape sequence.
     *
     * @param charVal The character to escape.
     * @return The RE encoding of the escaped character.
     */
    public static String esc(char charVal) {
        return String.format("\\u%1$04x", (int) charVal);
    }


    /**
     * Create an RE where escaped characters loose their usual significance.
     * @param str RE to quote 'as is'.
     * @return RE string enclosed in \\Q and \\E.
     */
    public static String asis(String str) {
        return String.format("\\Q%s\\E", str);
    }

    /**
     * matches a backslash (\) character.
     */
    public final static String BS = "\\\\";
    /**
     * matches a tab (\t) character
     */
    public final static String TAB = "\\t";
    /**
     * matches a newline (\n)character
     */
    public final static String NL = "\\n";
    /**
     * matches a return (\r) character
     */
    public final static String RTN = "\\r";
    /**
     * matches a form feed (\f) character
     */
    public final static String FF = "\\f";
    /**
     * matches a bell (\a) character
     */
    public final static String BELL = "\\a";
    /**
     * matches an escape (\e) character
     */
    public final static String ESC = "\\e";
    /**
     * matches any single character
     */
    public final static String WILD = ".";
    /**
     * matches '.'
     */
    public final static String DOT = "\\.";

    /**
     * matches '+'
     */
    public final static String PLUS = "\\+";
    /**
     * matches '-'
     */
    public final static String MINUS = "\\-";

    /**
     * matches '*'
     */
    public final static String STAR = "\\*";

    /**
     * matches '('
     */
    public final static String LPAR = "\\(";
    /**
     * matches ')'
     */
    public final static String RPAR = "\\)";
    /**
     * matches '^'
     */
    public final static String CARET = "\\^";
    /**
     * matches '$'
     */
    public final static String DOLLAR = "\\$";
    /**
     * matches '['
     */
    public final static String LSQ = "\\[";
    /**
     * matches ']'
     */
    public final static String RSQ = "\\]";
    /**
     * matches '{'
     */
    public final static String LSET = "\\{";
    /**
     * matches '}'
     */
    public final static String RSET = "\\}";
    /**
     * matches '|'
     */
    public final static String BAR = "\\|";

    /**
     * matches '?'
     */
    public final static String QUERY = "\\?";

    /**
     * matches a decimal digit
     */
    public final static String DIGIT = "\\d";
    /**
     * matches a single quote
     */
    public final static String SQUOTE = "\\\'";
    /**
     * matches a double quote
     */
    public final static String DQUOTE = "\\\"";
    /**
     * matches any whitespace character
     */
    public final static String WS = "\\s";
    /**
     * matches any horizontal whitespace character (space or tab)
     */
    public final static String HWS = "\\h";
    /**
     * matches any vertical whitespace character (ff, lf etc)
     */
    public final static String VWS = "\\v";

    public static String comment(String start) {
        return start+any(notIn(NL))+NL;
    }
    /**
     * matches any 'word' character
     */
    public final static String WORD = "\\w";
    /**
     * matches any word boundary character
     */
    public final static String BOUNDARY = "\\b"; // A word boundary

    // number and other classes
    /**
     * matches any decimal digit
     */
    public final static String DEC = "[0-9]";

    /**
     * matches any octal
     */
    public final static String OCT = "[0-7]";

    /**
     * matches any hex
     */
    public final static String HEX = "[0-9a-fA-F]";

    /**
     * matches any binary digit
     */
    public final static String BIN = "[0-1]";

    /**
     * matches a lower case character (POSIX class - US ASCII only)
     */
    public final static String LOWER = "[a-z]";        //  A lower-case alphabetic character: [a-z]

    /**
     * matches a upper case character (POSIX class - US ASCII only)
     */
    public final static String UPPER = "[A-Z]";        //  An upper-case alphabetic character:[A-Z]

    /**
     * matches any ASCII character (POSIX class - US ASCII only)
     */
    public final static String ASCII = "[\\x00-\\x7F]";// 	All ASCII:[\x00-\x7F]

    /**
     * matches an alphabetic character (POSIX class - US ASCII only)
     */
    public final static String ALPHA = "[A-Za-z]";     //  	An alphabetic character

    /**
     * matches an alphanumeric character (POSIX class - US ASCII only)
     */
    public final static String ALNUM = "[A-Za-z0-9]";  // 	An alphanumeric character

    /**
     * matches a punctuation character (POSIX class - US ASCII only)
     */
    public final static String PUNCT = "\\p{Punct}";   // 	Punctuation: one of !"#$%&' = *+;-./:;<=>?@[\]^_`{|}~

    /**
     * matches an alphanumeric or punctuation character (POSIX class - US ASCII
     * only)
     */
    public final static String GRAPH = "\\p{Graph}";   //  A visible character: alnum or punct

    /**
     * matches a printable (graphic or space) character (POSIX class - US ASCII
     * only)
     */
    public final static String PRINT = "\\p{Pcrint}";   //  A printable character: graphic or space

    /**
     * matches an control character: [\x00-\x1F\x7F] (POSIX class - US ASCII
     * only)
     */
    public final static String CTRL = "\\p{Cntrl}";   // 	A control character: [\x00-\x1F\x7F]

    /**
     * Characters for which Character.isLowerCase() returns true
     */
    public final static String JLOWER = "\\p{javaLowerCase}";
    /**
     * Characters for which Character.isUpperCase() returns true
     */
    public final static String JUPPER = "\\p{javaUpperCase}";
    /**
     * Characters for which Character.isWhiteSpace() returns true
     */
    public final static String JWS = "\\p{javaWhitespace}";
    /**
     * Characters for which Character.isMirrored() returns true
     */
    public final static String JMIRROR = "\\p{javaMirrored}";

    // unicode
    /**
     * A Latin script character (UNICODE category)
     */
    public final static String LATIN = "\\p{IsLatin}";
    /**
     * A Greek script character (UNICODE category)
     */
    public final static String GREEK = "\\p{InGreek}";
    /**
     * An uppercase character (UNICODE category)
     */
    public final static String UUPPER = "\\p{Lu}";
    /**
     * An alphabetic character (UNICODE category)
     */
    public final static String UALPAHA = "\\p{IsAlphabetic}";
    /**
     * A currency symbol (UNICODE category)
     */
    public final static String UCURRENCY = "\\p{Sc}";

    /**
     * Matches the beginning of a line
     */
    public final static String BOL = "^";
    /**
     * Matches the end of a line
     */
    public final static String EOL = "$";
    /**
     * Matches the beginning of the input
     */
    public final static String BINPUT = "\\A";
    /**
     * Matches the end of the previous match
     */
    public final static String ELASTMATCH = "\\G";
    /**
     * Matches the end of the input but for final terminator; if any
     */
    public final static String MAYBEENDINPUT = "\\Z";
    /**
     * Matches the end of the input
     */
    public final static String ENDINPUT = "\\z";


    /**
     * Create a character class. The method just encloses the parameter between
     * '[' and ']'.
     * <i>in("a-z0-9")</i> produces <i>[a-z0-9]</i> for example. You could also
     * use <i>in("a-z"+"0-9")</i> or <i>in(lower+digit)</i> if you find that
     * clearer.
     *
     * @param string to form the class.
     * @return A character class.
    */
    public static String in(String... string) {
        return "[" + String.join("", string) + "]";
    }

    /**
     * Create a negated character class. Similar to in except a '^' is inserted at the start
     * of the class.
     *
     * @param string to form the class.
     * @return A character class.
     */
    public static String notIn(String... string) {
        return "[^" + String.join("", string) + "]";
    }

    /**
     * Value which can be used to represent unlimited repetitions.
     */
    public static final int MANY = Integer.MAX_VALUE;

    /** Match a string using a greedy strategy.
     * @param string REGEX to be matched by this strategy.
     * @return returns string unchanged - greedy is the default
     */
    public static String greedy(String string) { return string; }

    /** Match a string using a lazy strategy.
     * @param string REGEX to be matched by this strategy.
     * @return returns string with lazy indicator
     */
    public static  String lazy(String string) { return string+"?"; }

    /** Match a string using an eager strategy.
     * @param string REGEX to be matched by this strategy.
     * @return returns string with eager indicator indicator
     */
    public static  String eager(String string) { return string+"+"; };



    /**
     * Reapeat a given sequence a number of times. ( can be
     * used to indicate 'indefinite' repetition.
     * <pre>
     * between(0, MANY, in("a-z")) is equivalent to [a-z]*
     * between(1, MANY, in("a-z")) is equivalent to [a-z]+
     * between(0, 1, in("a-z")) is equivalent to [a-z]?
     * between(n, m, in("a-z")) is equivalent to [a-z]{n,m}
     * between(n, MANY, in("a-z")) is equivalent to [a-z]{n,}
     * between(n, n, in("a-z")) is equivalent to [a-z]{n}
     * </pre>
     * <p>
     * @param low The smallest number of iterations allowed.
     * @param high The maximum number of iterations allowed
     * @param stringList alternate patterns
     * @return the appropriate RE.
     *
     */
    public static String between(int low, int high, String... stringList) {
        StringBuilder sb = new StringBuilder(uncaptured(stringList));

        if (high == MANY) {
            if (low == 0) {
                sb.append("*");
            } else if (low == 1) {
                sb.append("+");
            } else {
                sb.append(String.format("{%d,}", low));
            }
        } else if (low == 0 && high == 1) {
            sb.append("?");
        } else if (low == high) {
            sb.append(String.format("{%d}", low));
        } else {
            sb.append(String.format("{%d,%d}", low, high));
        }
        return sb.toString();
    }


    /**
     * Form a group which matches the given RE a certain number of times.
     * @param n Number of times to match.
     * @param stringList RE to match (converted to an alternation of the given strings)
     * @return An unnamed group to match the RE <b>exactly</b> n times.
     */
    public static String exactly(int n, String... stringList) {
        return between(n,n,stringList);
    }

    /**
     * Form a group which matches the given RE at least certain number of times.
     * @param n Number of times to match.
     * @param stringList RE to match (converted to an alternation of the given strings)
     * @return An unnamed group to match the RE <b>at least</b> n times.
     */
    public static String atLeast(int n, String... stringList) {
        return between(n, MANY,stringList);
    }

    /**
     * Form a group which matches the given RE zero or more times.
     * @param stringList RE to match (converted to an alternation of the given strings)
     * @return An unnamed group to match the RE <b>any</b> number of times.
     */
    public static String any(String... stringList) {
        return between(0,MANY,stringList);
    }

    /**
     * Form a group which matches the given RE some - non-zero - number of times.
     * @param stringList RE to match (converted to an alternation of the given strings)
     * @return An unnamed group to match the RE <b>some</b> number of times.
     */
    public static String some(String... stringList) {
        return between(1,MANY,stringList);
    }

    /**
     * Form a group which may match the given RE (ie 0 or 1 times).
     * @param stringList RE to match (converted to an alternation of the given strings)
     * @return An unnamed group which will <b>maybe</b> match the RE.
     */
    public static String maybe(String... stringList) {
        return between(0,1,stringList);
    }

    /**
     *  A pattern to match a string appearing a fixed number of times, not followed by another instance.
     *
     *  For example  <code>only(1, "&gt;")</code> will match "&gt;" or "&gt;=" but not "&gt;&gt;".
     * @param n number of times pattern must appear
     * @param pattern   pattern - may only work in simple case
     * @return An appropriate RE.
     */
    public static String only(int n, String pattern) {
        return exactly(n, pattern)+notBefore(pattern);
    }

    /**
     * Require a pattern to appear exactly one time.  So <code>one("good")</code> will match "good" but not "goodgood".
     * @param pattern to match.
     * @return An appropriate RE.
     */
    public static String one(String pattern) {
        return RE.only(1, pattern);
    }

    /**
     * Require a pattern to appear exactly twice.  So <code>two("gd")</code> will match "gdgd" but not "gd" or "gdgdgd".
     * @param pattern to match.
     * @return An appropriate RE.
     */
    public static String two(String pattern) {
        return RE.only(2, pattern);
    }

    /**
     * Require a pattern to appear exactly three times.  So <code>three("xxx")</code> will match "xxx" but not "xx", "x" or "xxxx".
     * @param pattern to match.
     * @return An appropriate RE.
     */
    public static String three(String pattern) {
        return RE.only(3, pattern);
    }


    /**
     * Form a group of a given type.  A group begins (?x where x is a String
     * - see {@link java.util.regex.Pattern Pattern} for values for x.
     * @param grp Group type .
     * @param stringList RE to group (converted to an alternation of the given strings)
     * @return An appropriate RE.
     */
    public static String groupOf(String grp, String... stringList) {
        return new StringBuilder("(").append(grp).append(String.join("|", stringList)).append(")").toString();

    }

    /**
     * Form a capturing group.
     * @param stringList RE to group (converted to an alternation of the given strings)
     * @return The RE as a capturing group.
     */
    public static String oneOf(String... stringList) {
        return groupOf("", stringList);
    }

    /**
     * Form a non-capturing group.
     * @param stringList RE to group (converted to an alternation of the given strings)
     * @return The RE as a non-capturing group.
     */
    public static String uncaptured(String... stringList) {
        return groupOf("?:", stringList);
    }

    /**
     * Form a named group.
     * @param name Name of the group.
     * @param stringList RE to group (converted to an alternation of the given strings)
     * @return The RE as a group with the given name.
     */
    public static String namedOneOf(String name, String... stringList) {
        return groupOf(String.format("?<%s>", name), stringList);
    }

    /**
     * Form an independent group.
     * @param stringList RE to group (converted to an alternation of the given strings)
     * @return The RE as an independent group.
     */
    public static String independent(String... stringList) {
        return groupOf("?>", stringList);
    }


     /** Form a zero-width negative lookahead group.
     * @param stringList RE to group (converted to an alternation of the given strings)
     * @return The RE.
     */
    public static String notBefore(String... stringList) {

        return groupOf("?!", stringList);
    }

    /**
     * Form a zero-width positive lookahead group.
     * @param stringList RE to group (converted to an alternation of the given strings)
     * @return The RE.
     */
    public static String before(String... stringList) {

        return groupOf("?=", stringList);
    }


    /**
     * Form a zero-width positive lookbehind group.
     * @param stringList RE to group (converted to an alternation of the given strings)
     * @return The RE as an independent group.
     */
    public static String after(String... stringList) {

        return groupOf("?<=", stringList);
    }

    /**
     * Form a zero-width negative lookbehind group.
     * @param stringList RE to group (converted to an alternation of the given strings)
     * @return The RE as an independent group.
     */
    public static String notAfter(String... stringList) {

        return groupOf("?<!", stringList);
    }


    /**
     * Insert a numbered backref.
     * @param n Group number to insert.
     * @return A backref to the given number group.
     */
    public static String backRef(int n) {
        return String.format("\\%2d", n);
    }

    /**
     * Insert a named backref.
     * @param  name  Name to insert.
     * @return A backref to the given named group.
     */
    public static String backRef(String name) {
        return String.format("\\k<%s>", name);
    }

    private static final String REOPTION = "idmsuxU";
    private static final int REBITMASK[]
            = { Pattern.CASE_INSENSITIVE,       // i
                Pattern.UNIX_LINES,             // d
                Pattern.MULTILINE,              // m
                Pattern.DOTALL,                 // s
                Pattern.UNICODE_CASE,           // u
                Pattern.COMMENTS,               // x
                Pattern.UNICODE_CHARACTER_CLASS // U
             };

    private static void getOptions(StringBuilder sb, int opt) {
        int ln = REBITMASK.length;
        for (int i = 0; i < ln; i++) {
            if ((opt & REBITMASK[i]) != 0) {
                sb.append(REOPTION.charAt(i));
            }
        }
    }

    /**
     * Set or unset pattern compilation options.
     * @param on    Flags to turn on (see {@link java.util.regex.Pattern Pattern}).
     * @param off   Flags to turn off (see {@link java.util.regex.Pattern Pattern}).
     * @param stringList RE to be used under these flags (optional).
     * @return Resulting group.
     */
    public static String options(int on, int off, String... stringList) {
        StringBuilder sb = new StringBuilder("?");
        if(on != 0) getOptions(sb, on); // get 'on' options
        if (off != 0) {
            sb.append('-');
            getOptions(sb, off);    // 'off' options
        }
        if (stringList.length != 0) {
            sb.append(':');
        }
        return groupOf(sb.toString(), stringList);
    }

}
