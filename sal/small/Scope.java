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
/*
 * Created by simon on 21/06/17.
*/

import static sal.small.Descriptor.*;
import static sal.small.Token.isStringName;

import java.util.HashMap;
import java.util.Objects;

public class Scope extends HashMap<String, Descriptor > {

    /** Some names refer to values the compiler needs to know which are not in the
     * program being compiled.  To ensure they don't clash with program names they
     * must either be reserved words or contain a non-id character.
     *
     * If a name begins with '^' it will be copied when a new scope is opened
      */
    final static String UPGRADE = "^";
    final static String NEXT_LOCAL = UPGRADE+"NEXT LOCAL";

    final static String MAX_LOCAL = "MAX LOCAL";


    Scope previous;

    static Scope globalScope = new Scope();
	static Scope currentScope = null;

 	
    public Scope() {
        // link to previous Scope
        Scope prev = currentScope;
        this.previous = currentScope;
        currentScope = this;
		if(prev == null) {
			newValue(NEXT_LOCAL, 0);
			newValue(MAX_LOCAL, 0);
		} else {
			/* check for keys which should be copied to the new scope */
			for(String k : prev.keySet()) {
				if(k.startsWith(UPGRADE)) currentScope.put(k, prev.get(k));
			}
		}
    }

	// Users are not directly aware of scope objects: just entering/leaving 
	
	public static void beginScope() {
		new Scope();
    }
	
	public static void endScope() {
        // If the allocated number of variables exceeds the max used so far, update that.
		Integer nextLocal = getValue(NEXT_LOCAL);
        Integer maxLocal  = getGlobal(MAX_LOCAL);
        if(nextLocal > maxLocal) putGlobal(MAX_LOCAL, nextLocal);
 		currentScope = currentScope.previous;
	}

	public static <T> void putGlobal(String key, T value) {
		globalScope.put(key, new Value<T>(value));
	}
	
	public static <V> V getGlobal(String key) {
		Descriptor d = globalScope.get(key);
		return (d != null && d instanceof Value) ? (V) ((Value) d).getValue() : null;
	}
	
    /**
     * @param key key to value sought.
     * @param defaultValue  value to return if key is not found.
     * @return value corresponding to k in nearest scope which contains it or defaultvalue.
     */
    public static Descriptor getAny(String key, Descriptor defaultValue) {
        for(Scope sc = currentScope; sc != null; sc = sc.previous) {
            Descriptor value = sc.get(key);
            if (value != null) return value;
        }
        return defaultValue;
    }
    /**
     * @param key
     * @return
     */
    public static Descriptor getAny(String key) {
        return getAny(key, null);
    }

    /**
     * @param key            key to value sought.
     * @param nameKey        in scope containing this key
     * @param expectedValue  where nameKey has this value
     * @return value corresponding to k in nearest scope or defaultvalue.
     */
    public static Descriptor getAny(String key, String nameKey, Descriptor expectedValue) {
        Scope sc = getScope(nameKey, expectedValue);
        return (sc == null) ? null : sc.get(key);
    }
 
    /**
     * @param key
     * @return
     */
    public static Scope getScope(String key) {
        for(Scope sc = currentScope; sc != null; sc = sc.previous) {
            if(sc.containsKey(key)) return sc;
        }
        return null;
    }

    /**
     * @param key
     * @return
     */
    public static Scope getScope(String key, Descriptor value) {
        for(Scope sc = currentScope; sc != null; sc = sc.previous) {
            if(sc.get(key).equals(value)) return sc;
        }
        return null;
    }


    public static boolean updateAny(String key, Descriptor value)
    {   Scope sc = getScope(key);
        boolean found = (sc != null);
        if(! found) sc = currentScope;
        sc.put(key, value);
        return found;
    }

   ///////////////////////////// Get/Create objects of various types  /////

   public static <V> void newValue(String key, V value) {
        currentScope.put(key, new Value<V>(value));
   }

   public static <V> V getValue(String key) {
        Descriptor d = getAny(key);
        return (d != null && d instanceof Value) ? (V) ((Value) d).getValue() : null;
   }

    public static Label newLabel(String key) {
        Label d = Label(key.replace(' ','_'));
        currentScope.put(key, d);
        return d;
    }

    public static Label getLabel(String key) {
        Descriptor d  = getAny(key);
        return (d != null && d instanceof Descriptor.Label) ? (Label) d : null;
    }


    public static Variable newLocal(String key, String typeName) {
        // allocate the next local variable number
        Integer nextLocal = getValue(NEXT_LOCAL);
        int localVal = nextLocal + 1;
        newValue(NEXT_LOCAL, localVal);
        // now create the local variable
        Variable v = Local(nextLocal.toString(), typeName);
        currentScope.put(key, v);
        return v;
    }

    public static Variable newLocal(String key) {
        return newLocal(key, isStringName(key) ? "~" : "I");
    }

    public static Variable getVariable(String key) {
        Descriptor d = getAny(key);
        return (d != null && d instanceof Descriptor.Variable) ? (Variable) d : null;
    }

    public static Variable newVariable(String key) {
        Variable d = getVariable(key);
        if(d == null) d = newLocal(key);
        return d;
    }

	public static void dump(String... strList) {
		int depth = 0;
		for(Scope s = currentScope; s != null; s= s.previous) {
			System.out.printf("At level %d\n", depth++);
			for(String name : strList) {
				Descriptor d = s.get(name);
				System.out.printf("  %s=%s\n", name, d == null ? "not found" : d.toString() );
			}
		}		
	}
		

}
