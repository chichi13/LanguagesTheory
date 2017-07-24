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
import static sal.small.Code.*;


/** Generate Jasmin (Java Assembler) code from a syntax tree.
 *
 * This is the main output program. It walks over the AST.  The method {@link #writeProgram(PrintStream,Tree)}
 *  takes the syntax tree and embeds it in a 'wrapper' - a Jasmin program with 'housekeeping already done so that all {@link #generate(Tree, Scope)}
 *  needs to do is deal with each tree node type as it finds it.
 */

public class CodeGen {

    /**
     * Writes (to the PrintStream provided by CodeWriter) a boilerplate Jasmin program in which is embedded the result of
     * generating code from the AST.
     *
     * @param tree  AST which forms the program.
     */
     public static void writeProgram(PrintStream outputStream, Tree<Token> tree) {

         // Code needs to know the output stream  for all small 'emit' methods below.
         Code.setOutputStream(outputStream);

         Templater tr = new Templater(outputStream)
         {

             String   className = getGlobal("CLASS NAME");

             public void toString(String s) {

                 switch(s) {

                     case "CLASSNAME" : print(className);  return;

                     case "CODE":       // we need an inner scope since code is generated as body of main()
                                         beginScope();
                                         // invent a mythical first arg
                                         newLocal("ARGS TO MAIN","[~");	// array of string (~ will be converted to string)
                                         writeStatementCode(tree);
                                         endScope();
                                         return;

                     case "LOCALS":      //Integer n = getGlobal(MAX_LOCAL);
                                         print(getGlobal(MAX_LOCAL));

                     default:            return;
                 }
            }
         };

		// this is the 'boiler plate' code into which generated code is inserted. 
		// it is done this way to make it easier to change - and because Templater already existed!
		String[] theText = new String[]{
            ".class public (~CLASSNAME~)\n\n",
            ".super java/lang/Object\n",
            
            ".method public <init>()V\n",
            ".limit stack 10\n",
            "    aload_0\n",
            "    invokespecial java/lang/Object/<init>()V\n",
            "    return\n",
            ".end method\n",
            ".method public static main([Ljava/lang/String;)V\n",
            ".limit stack 10\n",
            "(~CODE~)",
            "    return\n",
            ".limit locals (~LOCALS~)\n",
            ".end method\n",
            "\n" };
            
          for(String aLine : theText) tr.render(aLine);
       }


		// small method for checking int/string types 

	public static boolean isStringVar(Tree<Token> tree) {
		if(! tree.isLeaf()) return false;
		return isStringName(tree);
	}	

	final static boolean INT_TYPE = false;
	final static boolean STR_TYPE = true;
	

    /**
     * Generate Jasmin assembler from an AST.
     * @param t  The AST.
     *
     *  Generate writes Jasmin assembler code to the {@link java.io.PrintStream PrintStream} out.
     */

    static void writeStatementCode(Tree<Token> tree) {

        if (tree == null) return;
        Token token = tree.token();
        
        switch (token) {
 
			// generate code for a list of statements
            case STATEMENTLIST:
                for(Tree<Token> tst: tree.allChildren()) writeStatementCode(tst);
                return;

			// as STATEMENTLIST but within a new scope
			case BLOCK: {
				beginScope();
				for(Tree<Token> tst: tree.allChildren()) writeStatementCode(tst);
                endScope();
			}
			return;
			
			
			// individual statements
			
			case DECREMENT:  	
			case INCREMENT:
				 { Tree<Token> var = tree.child(0);
				   increment(var.toString(), (token == INCREMENT) ? 1 : -1);
				  }		
				  return;
			
 
            case ASSIGN: {
					Tree<Token> var = tree.child(0);
					boolean stringVar = isStringVar(var);
					boolean stringExp = writeExpressionCode(tree.child(1));
					if(stringVar == stringExp) {
						store(var.toString());
					} else if(stringVar) {
						ErrorStream.log("Attempt to assign int value to string variable \'%s\'.\n", var.toString());
					} else {
						ErrorStream.log("Attempt to assign string value to int variable \'%s\'.\n", var.toString());
					}	
				}
                return;

          case IF: {
				beginScope();	// start a scope to cover the whole if
                Label endIf = newLabel("END IF");  // label for this end-if 
                int pairs = tree.children();	// (test then code)+
				for(int i = 0; i < pairs; i += 2) {
					Tree<Token> test = tree.child(i);
					Tree<Token> code = tree.child(i+1);
					if(test != null) {	// not 'else' part
						Label nextTest = newLabel("NEXT TEST");  // for jump to next elif/else 		
						writeExpressionCode(test, INT_TYPE);
						ifFalse(nextTest);
						writeStatementCode(code);
						jump(endIf);
						setLabel(nextTest);
					} else {
						writeStatementCode(code);
					}
				}
				setLabel(endIf);
				endScope();
                }
            return;

			
            case WHILE: { // also do/end
                beginScope();
                Label continueLabel = newLabel("NEXT LOOP");
                Label breakLabel 	= newLabel("EXIT LOOP");
                setLabel(continueLabel);	// jump back here for 'continue'
                Tree<Token> testExpr = tree.child(0);
				if(testExpr != null) {		// 'null' for do/end
					writeExpressionCode(tree.child(0), INT_TYPE);  		// expression to test  
					ifFalse(breakLabel);		// if not true, 'break'
				}
                writeStatementCode(tree.child(1)); 		// content of while/do
                jump(continueLabel);		// jump back to beginning
                setLabel(breakLabel);		// outside while
                endScope();
            }
            return;

           case UNTIL: {
                beginScope();	
                Label continueLabel = newLabel("NEXT LOOP");
                Label breakLabel =    newLabel("EXIT LOOP");
                Label startLabel =    newLabel("START LOOP");
                setLabel(startLabel); 		//	jump to here exit conmdition isn't met
                writeStatementCode(tree.child(1)); 		// contents of do/until loop
                setLabel(continueLabel);	// 'continue' goes to just before the test
                writeExpressionCode(tree.child(0), INT_TYPE);  		// code for test
                ifFalse(startLabel);		// if test fails jump back to start
                setLabel(breakLabel);		// or continue here
                endScope();
            }
            return;


			case BREAK: 
			case CONTINUE:  {
				 Label l = getLabel((token == BREAK) ? "EXIT LOOP" : "NEXT LOOP");
				 if(l == null)
					ErrorStream.log("\'break\' or \'continue\' used outside a loop.\n");
				 else
					jump(l);
				 return;
			}	
				
                
            case PRINT: {
				 boolean isString = writeExpressionCode(tree.child(0));
				 emit(isString ? PRINT_STR : PRINT_INT);
                return;
			}
                

            case READ_STR:
            case READ_INT: {
				 emit(token);
				 store(tree.toString());
				 return;
			}

		}
	}

	public static void	writeExpressionCode(Tree<Token> tree, boolean needsString) {
		boolean expIsString = writeExpressionCode(tree);
		if(needsString != expIsString) emit(needsString ? TO_STR : LEN_STR);
	}
	
	public static boolean writeExpressionCode(Tree<Token> tree) {
		
		Token token = tree.token();
		int kids = tree.children();
		if(kids == 0) {
		//  a leaf - must be Number, String or Identifier
			emit(token, tree.toString());
			return  (token == NUMBER) ? INT_TYPE
					:(token == STRING) ? STR_TYPE
					: isStringVar(tree);
		}
		// write code for first child and check type
		boolean child0IsString = writeExpressionCode(tree.child(0));
		// Deal with unary operators 
		switch(token) {
			// unary numeric operations
            case NEGATE:
				if(child0IsString) {
					ErrorStream.log("Attempt to apply \'-\' to a string.\n");
				}
				else 
					emit(NEGATE);
					
				return INT_TYPE;	// assuming an int was intended!	
			//!!! Insert String operations here !!!		
			
			}
			
			// Now binary operations
			boolean child1IsString = writeExpressionCode(tree.child(1));
			
			switch(token) {
            case LE:
            case LT:
            case GE:
            case GT:
            case EQ:
            case NE:
                {	// first deal with different types
					if(child0IsString) {
						if(!child1IsString) {
							ErrorStream.log(" <string> %s <int> is illegal.\n", token);
						} else {
							emit(COMPARE_STR);	// compare strings
							emit(ZERO);		// to give compare with 0		
						}
					} else /* child0 is an int */ {
						if(child1IsString) {
							ErrorStream.log(" <int> %s <string> is illegal.\n", token);
						}
						// otherwise the correct values are on top of the stack
					}
					// deal with code to make an int comparison
                    /* if the test is false, the code falls through to the next line
                       where 0 (i.e.false) is loaded onto the stack. There is then a jump to the end.
                       The next line is the target when the test was true, -1 (true) is loaded onto the stack
                     */
                    Label ifTrue  = newLabel("TRUE VAL");
                    Label ifFalse = newLabel("FALSE VAL");
                    jump(token, ifTrue);
                    emit(ZERO);		// for false
                    jump(ifFalse);
                    setLabel(ifTrue);
                    emit(ONE);		// for true
                    setLabel(ifFalse);
                }
                return INT_TYPE;	// int left on stack
				
			// String and integer operations
			// !!!!! STRING OPS NOT YET COMPLETE !!!!
            
            case PLUS:		
            case MINUS:
            case TIMES:
            case DIVIDE:
            case MOD:
            case SHL:
            case SHR:
            case SHRS:
                {
                    emit(token);
                }
                return INT_TYPE;

            default:            //must be a binary operator +, -, *, /, %
                    ErrorStream.log("Unexpected token in code generation %s", token.toString());
					return INT_TYPE; // and why not!
        }
    }
 	

}



