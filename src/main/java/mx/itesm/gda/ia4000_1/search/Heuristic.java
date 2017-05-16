/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.search;

/**
 *
 * @author alexv
 */
public interface Heuristic<S extends State<S, ?>>  {

    public long distance(S current, S goal);

}
