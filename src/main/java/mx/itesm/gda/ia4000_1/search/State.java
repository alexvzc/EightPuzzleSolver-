/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.search;

import java.util.Set;

/**
 *
 * @author alexv
 */
public interface State<S extends State<S, M>, M extends State.Move<S, M>> {

    public Set<M> movements();

    public S move(M movement);

    public interface Move<T extends State<T, M>, M extends State.Move<T, M>> {

        public long cost();

        public String toString(T from);

    }

}
