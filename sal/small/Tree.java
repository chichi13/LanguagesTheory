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

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by simon on 29/05/17.
 */


    public class  Tree<T extends Enum<T> > {

        protected T token;

        public Tree() { }

        public Tree(T token) {
            this.token = token;
        }

        public T token() { return this.token; }

		public void setToken(T token) { this.token = token; }
		
        public boolean isLeaf() { return false; }
       
        public Tree<T> child(int childIndex) { return null; };
        
        public  void child(int childIndex, Tree<T> tree) { };
     
        public int children() {
            return 0;
        };

        public List< Tree<T> > allChildren() { return null; }

        public void addChild(Tree<T> t) { }

        ///////////////////////////////////////////////////////////////////////
        public static class Leaf<T extends Enum<T>, V> extends Tree<T> {

            V value;


            public Leaf(T token, V value) {
                super(token);
                this.value = value;

            }

            public V value() {
                return this.value;
            }

            public void setValue(V theValue) {
                this.value = theValue;
            }

            public boolean isLeaf() { return true; }

            public String toString() {
                return value.toString();
            }

    }

        ////////////////////////////////////////////////////////////////////
        public static class Branch<T extends Enum<T>>  extends Tree<T> {

            // accessing nodes in a syntax tree

            protected List< Tree<T> > node;  // list of children

            public Branch(T token, Tree<T> ... children) {
                super(token);
                this.node = new ArrayList<Tree<T> >(children.length);
                for(Tree<T> child : children) this.node.add(child);
            }

            @Override
            public Tree<T> child(int childIndex) {
                return node.get(childIndex);
            }
            

           @Override
            public int children() {
                return node.size();
            };

           @Override
            public List< Tree<T> > allChildren() { return this.node; }

            @Override
            public void addChild(Tree<T> newChild) {
                this.node.add(newChild);
            }


        }

    public static <T extends Enum<T>, V>  Tree<T>  leaf(T t, V value) {
        return new Leaf<T, V>(t, value);
    }
    
    public static <T extends Enum<T> >  Tree<T>  leaf(T t) {
        return new Leaf<T, Object>(t, null);
    }

    public static <T extends Enum<T> >  Tree<T>  list(T token, Tree<T>... nodes) {
       Tree<T> node = new Branch<T>(token);
       for(Tree<T> child : nodes) node.addChild(child);
       return node;
    }
    






}
