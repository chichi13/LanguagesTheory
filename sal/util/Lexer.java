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

/*
 * Created by simon on 29/05/17.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sal.util.Fail.failEmpty;

/** Generate a stream of tokens on demand from an enum specification.
 *
 * Lexer uses the methods provided by <code></code>Enum&lt;T&gt;</code> to obtain details of all tokens in the enum class T.  The interface {@link sal.util.Patterned Patterned}
 * specifies the condition T must satisfy.
 *
 * To extract the information it needs, instances of Lexer need two enum values: a token representing an unmatched pattern and one for end of input.
 * The other tokens properties can be deduced using standard methods for enum types.
 *
 * Lexer doesn't handle nested constructs or tokens which can cross a line barrier.
 * Created by simon on 26/05/17.
 */
public class Lexer<T extends Enum<T> & Patterned > {

    // ********************* TOKENS ******************************

    /** Predefined token: no match in input.  */
    protected T UNMATCHED;

    /** Predefined Token: end of input.  */
    protected T EOF;

    /** Token representing an identifier.
     * Any other token which matches this pattern is assumed to be a keyword.
     * By default it is not defined (no keywords).
     *
    */
    protected T identifier;


    /** The last detected token.  The tokeniser works the opposite way to Java's Scanner -#
     * when called it detects the token which the input must be.
     *
     */
    protected T currentToken;



    // ********************* PATTERNS ****************************

    /** RE Expression matcher compiled from token patterns. **/
    protected Matcher tokenMatcher;

    /** Map for fixed text tokens (i.e. 'if', 'then') */
    protected Map<String, T> words;

    protected Map<String, T> patternNames;

    /** RE Expression matcher compiled from token patterns. **/
    protected Matcher spaceMatcher;


    protected int lineNumber;

    // ********************** INPUT BUFFER **************************

    /** A view of the current input buffer - from the end of the last found
     * token to the end of the buffer.
     **/
    protected CharView buffer;

    protected BufferedReader reader;

    /**
     * Recorded if there was an I/O error - UNMATCHED will be returned
     * If this is null there was no error on the last read.
     */
    protected IOException ioException = null;


    // *********************** CURRENT TOKEN ***********************

    /** A 'view' of the last token found.  Invalidated as soon as the next
     call to scan is made

    **/
    protected CharView   text;


    /** Construct a Lexer given the end of file and unmatched tokens.
     *
     * @param eof           token to return when end of file detected.
     * @param unmatched     token to return when no match is found. It is also returned if there is an I/O exception.
     */
    public Lexer(T eof, T unmatched) {
        this(eof, unmatched, null);
    }

    /** Construct a Lexer given the end of file and unmatched tokens.
     *
     * @param eof           token to return when end of file detected.
     * @param unmatched     token to return when no match is found. It is also returned if there is an I/O exception.
     * @param identifier    token which represent an identifier.
     */
    public Lexer(T eof, T unmatched, T identifier)
    {
        // 1. ******************* TOKENS ******************************

        this.EOF = eof;
        this.identifier = identifier;
        this.currentToken = this.UNMATCHED = unmatched;

            /* Build an RE match to describe all tokens */
        String pattern = null;						// to hold all patterns as created

        // Create an identifier pattern.  There doesn't have to be an identifier (think of Scanner)
        String p = (identifier != null) ? identifier.pattern()    // identifier has a pattern
                                        : "(?!)";                 // a pattern which always fails!

        // create a matcher to identify keywords
        Matcher identifierMatcher =  Pattern.compile(p).matcher("");

        this.words = new HashMap<>();				// map: keyword text -> token

        Class<T> enumClass = eof.getDeclaringClass();

        this.patternNames = new HashMap<>();

        // if identifier is defined, any token with a pattern which would match identifier is a keyword
        for(T t : enumClass.getEnumConstants()) {
            String tokenPattern = t.pattern();
            if(tokenPattern == null) continue;  // not detected in pattern matcher

            // check if pattern matches identifier
            identifierMatcher.reset(tokenPattern);
            if(identifierMatcher.matches() ) {
                // keyword found
                this.words.put(tokenPattern, t);
            } else {
                // add this as a named group
                String tokenName = t.toString();
                String matchTokenGroup = String.format("(?<%s>%s)", tokenName, tokenPattern);
                pattern = (pattern == null) ? matchTokenGroup : (pattern + "|" + matchTokenGroup);
                this.patternNames.put(tokenName, t);
            }
        }

        // create a matcher from the patterns
        this.tokenMatcher   = Pattern.compile(pattern).matcher("");

        // initialise defaulter matcher for white space
        this.whiteSpace(RE.WS);

        // 3. ********************** INPUT BUFFER **************************

        /** A view of the current input buffer - from the end of the last found
         * token to the end of the buffer.
         **/

        this.buffer = new CharView();

        this.lineNumber = 0;

        this.text   = new CharView();
    }

    /** Allow overriding of whitespace defaults.
     *
     * @param alts a list of patterns which correspond to white space.  These override the default.
     * @return the tokeniser itself.
     */
    public Lexer<T> whiteSpace(String... alts) {
        this.spaceMatcher = Pattern.compile(RE.any(alts)).matcher("");
        return this;
    }


    //  ************** TOKENISING ********************

    /** Set/reset the tokeniser to take input from a BufferedReader.
    *   @aparm reader - The reader to use for input when scan is called.
     */
    public Lexer<T> input(BufferedReader reader) {
        this.text.set(0, 0);
        this.reader 	= reader;	// forget previous input
        this.lineNumber = 0;
        this.currentToken = UNMATCHED;
        return this;
    }

    /** Set/reset the tokeniser to take input from a String.
     *   @aparm String to tokenise.
     */
    public Lexer<T> input(String source) {
        return input(new BufferedReader(new StringReader(source)));
    }

    /** Set/reset the tokeniser to take input from any Reader.
     *   @aparm The reader to use for input when scan is called.
     */
    public Lexer<T> input(Reader read) {
        return input(new BufferedReader(read));
    }


    /** Reads a line of input, checking for EOF and I/O exceptions.
     *
     * @return false if there an I/O exception or the input is exhausted.
     *
     * This should be rewritten to throw exceptions - the current design is ugly and inappropriate.
     */

    protected boolean fillBuffer() {

        // Empty buffer so try to read another line
        String newBuffer;
        this.ioException = null;  // assume no error
        try {
            newBuffer = reader.readLine();
        } catch (IOException ioerr) {
            this.ioException = ioerr;
            text.set("");
            this.currentToken = UNMATCHED;
            return false;
        }

        // read line returns null at eof
        if (newBuffer == null) {
            this.currentToken = EOF;
            text.set("");
            return false;
        }

        // otherwise a line has been read
        // count line
        this.lineNumber++;
        // set text buffer to what was read (with \n)
        this.buffer.set(newBuffer+"\n");
        // set last text read to empty
        this.text.set(newBuffer, 0, 0);
        this.currentToken = UNMATCHED;  // set to imply: continue to look for a match
        return true;
    }


    /**
     * Read the next token, discarding whitespace.
     * @return The next matched token.
     */
    public T scan() {

        Matcher wsMatcher = this.spaceMatcher;
        Matcher tokenMatcher = this.tokenMatcher;

        // First ensure there is some data
            /* Should only be EOF when input is complete.
               Subsequent calls will continue to return EOF.
             */
        while (currentToken != EOF) {

            // first check if buffer is empty
            if ((this.buffer.length() == 0) && ! fillBuffer()) return this.currentToken;

            // there must be at least one character in the buffer
            // check for leading ws

            // now look for a real token
            CharView buff = this.buffer;

            wsMatcher.reset(buff);
            wsMatcher.lookingAt(); // never fails!
            // now step over the white space
            buff.set(buff.getBeginIndex() + wsMatcher.end());
            if(buff.length() == 0) continue; // whole line consumed

            // set the matcher to refer to the current buffer.
            tokenMatcher.reset(buff);

            // by default assume nothing matches
            int textLength = 1;
            this.currentToken = UNMATCHED;
            // check for a matching symbol at the start of input.
            if (tokenMatcher.lookingAt()) {
                // extract data about match
                for (Map.Entry<String, T> pattern : this.patternNames.entrySet()) {
                    //System.out.printf("Buffer \"<<%s>>\" Trying group %s\n", buff, patternName);
                    String grp = tokenMatcher.group(pattern.getKey());
                    if (grp != null) {
                        textLength = grp.length();
                        //this.currentToken = this.nameToToken.get(patternName);
                        this.currentToken = pattern.getValue();
                        //System.out.printf("Buffer \"<<%s>>\" matching \"%s\"\n", buff, grp);
                        break;
                    }
                }
            }

            // see what's happening:
            //System.out.printf("Token %s: @%d length %d\n", this.currentToken, buff.getBeginIndex(), textLength);
            int start = buff.getBeginIndex();
            int end = start + textLength;
            this.text.set(start, end);
            // move buffer point forward
            buff.set(end);
            if (this.currentToken == UNMATCHED) break;
            // check for a keyword
            if (this.currentToken == this.identifier)
                this.currentToken = this.words.getOrDefault(this.text.toString(), this.identifier);
            break;
        }
        return this.currentToken;
    }

    /** Returns the current token - undefined until scan() has been called.
     *
     * @return The current token.
     */
    public T currentToken() { return this.currentToken; }

    /** Returns the buffer in which the last token was found.
     *
     * @return  A view of the buffer from just after the current token to the end of the current line.
     *
     * This may disappear from a future version - use tokenInLine instead.
     */
    public CharView buffer() {
        return this.buffer;
    }

    /** Return the text of the current token.
     *
     * @return A string containing the current token.
     */
    public String currentText() {
        return this.text.toString();
    }

    /** Return the last IOException raised.
     *
     * @return  The last IOException or null if none.
     *
     * The token UNMATCHED can be returned as a result of an IOException or a failure to match input.
     * In the latter case, ioException() will return null.
     */
    public IOException ioException() { return this.ioException; }

    /** Return the current line number.
     *
      * @return number of lines read.
     */
    public  int lineNumber() { return this.lineNumber; }


    /** Returns the buffer in which the last token was found, represented as a CharView indicating the tokens limits.
     *
     * @return  A view of the buffer from the start to the end of the current token.
     *
     *  The {@link sal.util.CharView#sequence() CharView sequence method} can be used to extract the underlying text buffer.
     *  See {@link java.lang.CharSequence CharSequence} (which CharView inmplements) for details.
     */
    public  CharView tokenInLine() { return this.text; }

}
