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

import sal.util.Templater;
import sal.util.ErrorStream;

import java.io.PrintStream;
import java.util.EnumMap;
import java.util.Map;

import static sal.small.Scope.*;
import static sal.small.Descriptor.*;
import static sal.small.Token.*;


/** Generate Jasmin (Java Assembler) code from a syntax tree.
 *
 */

public class Code {

    /**
     * Specify the print stream to use.
     * @param outStream The stream to use (default is {@link System#out})
     */

    static PrintStream outStream;


	static void setOutputStream(PrintStream out) { outStream = out; }


// These produce appropriate output in JASMIN assembler  code.


    /** Calls printf on the {@link PrintStream}
     *
     * @param format format string - as for usual printf calls.
     * @param rest  arguments to format string.
     */
    public static void emitf(String format, Object... rest) {
        outStream.printf(format, rest);
    }

    /**
     * Output a line of assembler for an operation (such as nop, swap, etc) without arguments.
     * @param code - name of the operation.
     *
     *  Example {@code emit("swap");}
     */
    public static void emit(String code) {
        emitf("  %s\n", code);
    }

    /**
     * Output a line of assembler for an operation (such as load, store, etc) with a single argument.
     * @param code  name of the operation.
     * @param operand  operand used by code.
     *
     * Example {@code emit("new", "java/util/Scanner");}
     */
    public static void emit(String code, String operand) {
        emitf("  %s %s\n", code, operand);
    }

    /**
     * As for {@link #emit(String, String)} but with an integer argument.
     * @param code  name of the operation.
     * @param operand   operand used by code.
     */
    public static void emit(String code, int operand) {
        emit(code, Integer.toString(operand));
    }


private static Map<Token, String> opCode = new EnumMap(Token.class);


/** Set up library entries.
 *  Create a string containing instructions to call a library routine.
 * '~' in string is replaced by  Ljava.lang.String; - this is just to save typing
 */
static void libCall(Token t, String s) {
	opCode.put(t, "invokestatic sal/Library/"+s.replace("~","Ljava.lang.String;"));
}

	
    static {
        // default
        opCode.put(PLUS,   "iadd");
        opCode.put(MINUS,  "isub");
        opCode.put(TIMES,  "imul");
        opCode.put(DIVIDE, "idiv");
        opCode.put(MOD,    "imod");
        opCode.put(NEGATE,  "ineg");
        opCode.put(SHL,  	"ishl");
        opCode.put(SHR,  	"iushr");
        opCode.put(SHRS,  	"ishr");
        
        // these aren't in the 'default list' but are checked
        // for explicitly - this is a good place to store their opcodes
        opCode.put(GT,    "if_icmpgt");
        opCode.put(GE,    "if_icmpge");
        opCode.put(LT,    "if_icmplt");
        opCode.put(LE,    "if_icmple");
        opCode.put(EQ,    "if_icmpeq");
        opCode.put(NE,    "if_icmpne");

		// load values onto the stack
		opCode.put(NUMBER, "ldc");
        opCode.put(STRING, "ldc");
        opCode.put(ONE, 	"iconst_1");
        opCode.put(ZERO, 	"iconst_0");
         
        
        
        opCode.put(SWAP, "swap");
        

		libCall(CONCAT, 	"concat(~~)~");		// ~ with be replaced with String
		libCall(TO_STR, 	"toStr(I)~");		// ~ with be replaced with String
		libCall(TO_INT, 	"toInt(I)~");		// ~ with be replaced with String
		libCall(RIGHT_STR, 	"right(~I)~");		// ~ with be replaced with String
		libCall(LEFT_STR, 	"left(~I)~");		// ~ with be replaced with String
		libCall(FORMAT_STR, "format(~~)~");		// ~ with be replaced with String
		libCall(FORMAT_INT, "format(~I)~");		// ~ with be replaced with String
		libCall(COMPARE_STR, "compare(~~)I");	// ~ with be replaced with String
		libCall(LEN_STR, 	"len(~)I");			// ~ with be replaced with String
		libCall(READ_INT,	"readInt()I"); 		// call java scanner to get an integer
		libCall(READ_STR,	"readStr()~"); 		// call java scanner to get a String
		libCall(PRINT_STR,	"print(~)V");		// print String
		libCall(PRINT_INT,	"print(I)V");		// print String
		
    }

	public static void emit(Token t, String s) {
		if(t == IDENTIFIER) {
			emit(getVar(s).getLoad());
		} else{
			emit(opCode.get(t), s);
		}
	}	

 	static Variable getVar(String varName) {
		Variable var = getVariable(varName);
        if(var == null) {	// doesn't exist!
			// name hasn't been declared yet?
			ErrorStream.log("Variable \'%s\' is used before it has been initialised\n", varName);
			// create a new variable anyway
			var = newLocal(varName);
		}
        return var;
    }

	    /**
     * Output a line of assembler for an operation (such as nop, swap, etc) without arguments.
     * @param code - lexer token.
     *
     *  Example {@code emit("swap");}
     */
	public static void emit(Token t) {
			emit(opCode.get(t));
	}	


 	public static void	increment(String varName, int incDec) {
		Variable v = getVar(varName);
		emitf("  iinc %s  %d\n", v.toString(), incDec);
	}
	
    /**
     * Store a value from the stack using a descriptor.
     * @param var  Variable descriptor.
     *
     */
    public static void store(String varName) {
        emit(newVariable(varName).getStore());
    }

    /**
     * Set a label on the current line.
     * @param labelName  a label descriptor.
     *
     */
    public static void setLabel(Label labelName) {
        emitf("%s:\n", labelName.toString());
    }

    /**
     * Jump to a label if the value on the top of the stack is 0.
     * @param jumpOpcode  the sort of jump "ifeq" etc
     * @param labelName  label descriptor to jump to.
     *
     */
    public static void jump(String jumpOpcode, Label labelName) {
        emit(jumpOpcode, labelName.toString());
    }

    /**
     * Jump to a label if the value on the top of the stack is 0.
     * @param t  a token for the sort of jump (e.g. GE for the sort of jump "ifge")
     * @param labelName  label descriptor to jump to.
     *
     */
    public static void jump(Token t, Label labelName) {
        emit(t, labelName.toString());
    }

    /**
     * Jump to a label if the value on the top of the stack is 0 (i.e. false).
     * @param labelName  label to jump to.
     *
     */
    public static void ifFalse(Label labelName) {
        jump("ifeq", labelName);
    }

    /**
     * Jump to a label if the value on the top of the stack is not 0.
     * @param labelName  label to jump to.
     *
     *  Example {@code ifTrue("exit"); }
     */
    public static void ifTrue(Label labelName) {
        jump("ifne", labelName);
    }


    /**
     * Unconditional jump.
     *
     * @param labelName  label to jump to.
     *
     *  Example  {@code goto("exit"); }
     */
    public static void jump(Label labelName) {
        jump("goto", labelName);
    }

 
 	

}



