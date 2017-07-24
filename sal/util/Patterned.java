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

/**
 * Created by simon on 29/05/17.
 */
public interface Patterned {

    String pattern(); // return a REGEX-pattern

    String asText();  // a suitable name/example

/** Check that the current token is as expected.
 *
 * @param tokens symbol to ignore if found: good for lazy languages
 * where, maybe, the 'do' in {@code while ... do} is optional.
 * @return  true if the expected token was fount, false otherwise.
*/
@SuppressWarnings("unchecked")
default public boolean isIn(Patterned ... tokens) {
        for(Patterned t : tokens) {
            if(t == this) return true;
        }
        return false;
    }


@SuppressWarnings("unchecked")
default public boolean isIn(Patterned current, Patterned ... tokens) {
        for(Patterned t : tokens) {
            if(t == this) return true;
        }
        return false;
    }


/** Produce a list of expected tokens.
 *
 *  @param tokens list of expected tokens.
 *  @return A text representation of the expected tokens
 *
 *   Unlike {link #mustBe} expected assumes the current token didn't match.
 *   It doesn't advance to the next token.
 *
 *   It might be used as the default case of a switch on start values;
*/
static public String expected(Patterned... tokens) {
        StringBuilder sb = new StringBuilder();
        if(tokens.length > 1) {
            sb.append("one of");
        }
        for(Patterned t : tokens) sb.append(" ").append(t.asText());
        return sb.toString();
    }


}
