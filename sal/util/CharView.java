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


import java.text.CharacterIterator;
import java.util.Objects;


public class CharView implements CharacterIterator, CharSequence {

    /** CharSequence upon which this view is based. */
    CharSequence sequence;

    /** current cursor position when iterating
     *
     */
    int curPos;

    /** inclusive start point of view within sequence
     *
     */
    int startPos;

    /** exclusive end point of view within sequence
     *
     */
    int endPos;

    /** End of iteration marker: default is DONE in CharacterIterator.
     *
     */

    char end;


    public CharView() {
        this("", 0, -1);
    }

    public CharView(CharSequence seq) {
        this(seq, 0, -1);
    }

    public CharView(CharSequence seq, int start) {
        this(seq, start, -1);
    }

    /**
     *  Construct an iterator over part of a CharSequence
     * @param seq       CharSequence (could be a String)
     * @param start     inclusive start position of char (Indexed)
     * @param end       exclusive end position (Indexed)
     */
    public CharView(CharSequence seq, int start, int end) {
        setInit(seq, start, end);
        this.end = CharacterIterator.DONE;
    }

    private CharView setInit(CharSequence seq, int start, int end) {
        if (seq == null) throw new NullPointerException();
        this.sequence = seq;
        return set(start, end);
    }

    public CharView set(CharSequence seq, int start, int end) {
        return setInit(seq, start, end);
    }

    public CharView set(int start, int end) {
        int len = this.sequence.length();
        if(end < 0) end = len;
        if(start < 0 ||  start > end || end > len)
            throw new IllegalArgumentException(String.format("[%d, %d) not within [%d,d) - invalid indices", start, end, 0, len));
        this.curPos = this.startPos = start;
        this.endPos = end;
        return this;
    }

    public CharView set(int start) {
        return set(start, -1);
    }

    public CharView set(CharSequence cs) {
        return setInit(cs, 0, -1);
    }

    public CharSequence sequence() {
        return this.sequence;
    }

    /** reset provides similar facilities to set but values are 'within' the current range.
     *
     *
     * @param   start new start position.
     * @param   end   new end position.
     * @return  the view.
     *
     * For example starting at position x means this.startPos + x.
     * So whereas set("abcdef", 2, 4)  sets the view to "cde",
     * set(1,2) would produce "b"  while reset(1, 2) would give "c".
     */

    public CharView reset(int start, int end) {
        int sp = this.startPos;
        return set(start+sp, (end < 0) ? this.endPos : (end+sp));
    }

    /** reset provides similar facilities to set but values are 'within' the current range.
     *
     *
     * @param   start new start position.
     * @return  the view.
     *
     */
    public CharView reset(int start) {
        return reset(start, -1);
    }

    /** move provides a 'sliding wiew' onto a sequence.
     *
     *
     * @param   step distance +ve or -ve to move.
     * @return  the view.
     *
     */

    public CharView move(int step) {
        return this.set(this.startPos + step, this.endPos + step);
    }

    /**
     * Set the end of sequence character;
     *
     * @param eos New end of sequence character.
     * @return The previous value of EOS;
     */
    public CharView end(char eos) {
        this.end = eos;
        return this;
    }


    ////////////////////// Override CharacterIterator

    /**
     * Return the current EOS marker.
     *
     * @return The current EOS marker.
     */
    public char end() {
        return this.end;
    }


    //  for(char c = x.first(); c != x.end(); c = x.next() )   { ... }
    /**
     * Implements CharacterIterator.getBeginIndex() for CharSequence.
     * @see CharacterIterator#getBeginIndex
     */
    @Override
    public int getBeginIndex() {
        return startPos;
    }

    /**
     * Implements CharacterIterator.first() for CharSequence.
     * @see CharacterIterator#first
     */

    @Override
    public char first() {
        this.curPos = this.startPos;
        return current();
    }


    /**
     * Implements CharacterIterator.next() for CharSequence.
     * @see CharacterIterator#next
     */
    @Override
    public char next() {
        this.curPos++;
        return current();
    }


    //  for(char c = x.last(); c != x.end(); x.previous() )  { ... }

    /**
     * Implements CharacterIterator.getEndIndex() for CharSequence.
     * @see CharacterIterator#getEndIndex
     */

    @Override
    public int getEndIndex() {
        return endPos;
    }

    /**
     * Implements CharacterIterator.last() for CharSequence.
     * @see CharacterIterator#last
     */
    @Override
    public char last() {
        this.curPos = this.endPos - 1;
        return current();
    }

    /**
     * Implements CharacterIterator.previous() for CharSequence.
     * @see CharacterIterator#previous
     */
    @Override
    public char previous() {
        this.curPos--;
        return current();
    }

    /**
     * Implements CharacterIterator.length() for CharSequence.
     *
     */

    @Override
    public int length() { return this.endPos - this.startPos;  }


    /**
     * Implements CharacterIterator.current() for CharSequence.
     * @see CharacterIterator#current
     */
    @Override
    public char current() {
        int pos = this.curPos;
        return (this.startPos <= pos && pos < this.endPos)
                ? sequence.charAt(pos)
                : this.end;
    }


    /**
     * Implements CharacterIterator.getIndex() for String.
     * @see CharacterIterator#getIndex
     */
    @Override
    public int getIndex() {
        return curPos;
    }

    /**
     * Implements CharacterIterator.getIndex() for String.
     * @param pos   position to set the iterator
     * @see CharacterIterator#getIndex
     */
    @Override
    public char setIndex(int pos) {
        curPos = pos;
        return current();
    }

    /**
     * Computes a hashcode for this iterator.
     * @return A hash code
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final CharView other = (CharView) obj;
        return (this.curPos == other.curPos)
                && (this.startPos == other.startPos)
                && (this.endPos == other.endPos)
                && Objects.equals(this.sequence, other.sequence);
    }

    @Override
    public int hashCode() {
        return sequence.hashCode() ^ curPos ^ startPos ^ endPos;
    }

    /**
     * Creates a copy of this iterator.
     * @return A copy of this
     */
    @Override
    public Object clone()
    {
        try {
            return (CharView) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    // now make it a CharSequence as well

    @Override
    public char charAt(int index) {
        return this.sequence.charAt(this.startPos + index);
    }

    /**
     * Create a new CharView from a subsequence of this one.
     *
     * @param start Where the new CharView is to start.
     * @return The substring from start to the end of the sequence.
     */
    public CharSequence subSequence(int start) {
        return subSequence(start, -1);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        int sp = this.startPos;
        return new CharView(this.sequence, sp+start , (end < 0) ? this.endPos : (sp+end));
    }


    /**
     * Return a subsequence of the CharView object as a char array.
     *
     * @param start Inclusive start position.
     * @param end Exclusive end position.
     * @return Substring as char[].
     */
    public char[] asArray(int start, int end) {
        start += this.startPos;
        end = (end < 0) ? this.endPos : (this.startPos+end);
        int ln = end - start;     // length of substring
        if (ln <= 0) {
            return new char[0];
        }
        char buff[] = new char[ln];
        for (int i = 0; i < ln; i++) {
            buff[i] = this.sequence.charAt(start++);
        }
        return buff;
    }

    /**
     * Returns the whole of the current view as a String.
     *
     * @return the contained CharSequence as a String.
     */
    @Override
    public String toString() {
        return (startPos == 0) && (this.endPos == sequence.length())
                ? sequence.toString()
                : toString(0, -1);
    }

    /**
     * Convert a subsequence to a string.
     *
     * @param start Inclusive start index.
     * @param end Exclusive end index.
     * @return The subsequence as a string.
     */
    public String toString(int start, int end) {
        // Simplest is to build a string
        return new String(asArray(start, end));
    }




}
