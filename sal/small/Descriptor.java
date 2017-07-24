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

/** A class to link names used in a SMALL program to their description as used by Jasmin.
 *
 * For each name used in the program we store what it becomes to the assembler and the name of it's type.
 *
 *
 * Created by simon on 18/06/17.
 */
public class Descriptor {


    public Descriptor() { }


    /**
     * A descriptor holding any type of value (a wrapper).
     *
     * @param <V> Type of value stored.
     */
    public static class Value<V> extends Descriptor {

        V value;

        public Value(V value) {
            this.value = value;
        }

        public String toString() {
            return value.toString();
        }

        public V getValue() {
            return value;
        }

        public void set(V value) {
            this.value = value;
        }
    }

    /** Static method to create a Value.  Does little but included for consistency with other subclasses of Descriptor.
     *
     * @param value Value to be stored in the descriptor.
     * @param <X>   Class of value
     * @return  A suitable descriptor.
     */
    public static <X> Value<X> Value(X value) { return new Value(value); }


    /** Generates unique labels for use in jump instructions.
     *
     */
    public static class Label extends Value<String> {

        static int nextSuffix = 0;

        public Label(String name) {

            super(String.format("%s#%d", name, nextSuffix++));
        }

        public Label() {
            super("");
        }

    }

    /** static method of creating a label - for consistency.
     *
     * @param name name to be given to label.
     * @return A new Label instance.
     */
    public static Label Label(String name) {
        return new Label(name);
    }


    /** Static method of creating a label - for consistency.
     *
     * @return A new Label instance with a name based on 'label'.
     */
    public static Label Label() {
        return new Label("label");
    }


    /** A java variable, could be local, static or field (last not implemented yet)
     *
     */
    public static class Variable extends Value<String> {

        String typeName;   // java name for type, I, etc

        public Variable(String asmName, String typeName) {
            super(asmName);
            this.typeName = typeName.equals("~") ? "Ljava.lang.String;" : typeName;
        }

        public String getType() {
            return typeName;
        }

	
        public String getLoad() {
            return null;
        }

        public String getStore() {
            return null;
        }
    }

    /** Convert a java type name to a suitable prefix for a load/store local command.
     * E.g. java type 'I' (integer) maps to 'i', Object types (e.g. Ljava.lang.String; ) map to 'a' rtc
     *
     * @param typeCode
     * @return
     */
    static String asmType(String typeCode) {
        typeCode = typeCode.toLowerCase();  // asm use lower case, java types use upper.
        char shortCode = typeCode.charAt(0);
        if(shortCode == 'l') {
            return "a";
        } else if(shortCode != '[') {
            return Character.toString(shortCode);
        } else {
            char nxt = typeCode.charAt(1);
            if (nxt == '[' || nxt == 'l') {
                return "aa";
            } else {
                return nxt + "a";
            }
        }
    }


    public static class Local extends Variable {

        String prefix;

        public Local(String asmName, String typeName) {
            super(asmName, typeName);
            this.prefix = asmType(this.typeName);
        }

        public Local(String asmName) {
            this(asmName, "I");
        }

		public String getAsmType() {
			return prefix;
		}

        // by default this does getstatic
        public String getLoad() {
            return  String.format("%sload %s", this.prefix, this.toString());
        }

        public String getStore() {
            return  String.format("%sstore %s", this.prefix, this.toString());
        }

    }

    public static Local Local(String asmName, String typeName) {
        return new Local(asmName, typeName);
    }

    public static Local Local(String asmName) {
        return new Local(asmName, "I");
    }



    public static class Static extends Variable {

        public Static(String asmName, String typeName) {
            super(asmName, typeName);

        }

        // by default this does getstatic
        public String getLoad() {
            return  String.format("getstatic %s %s", this.toString(), this.typeName);
        }

        public String getStore() {
            return  String.format("putstatic %s %s", this.toString(), this.typeName);
        }

    }


    public static Static Static(String asmName, String typeName) {
        return new Static(asmName, typeName);
    }

    public static Static Static(String asmName) {
        return new Static(asmName, "I");
    }

}





