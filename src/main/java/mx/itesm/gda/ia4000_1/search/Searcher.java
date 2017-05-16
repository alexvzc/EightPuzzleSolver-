/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.search;

import java.util.List;

/**
 *
 * @author alexv
 */
public interface Searcher<S extends State<S, M>, M extends State.Move<S, M>>  {

    public List<? extends M> solution();

}
