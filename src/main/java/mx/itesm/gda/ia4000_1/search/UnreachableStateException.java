/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.search;

/**
 *
 * @author alexv
 */
public class UnreachableStateException extends RuntimeException {

    /**
     * Creates a new instance of <code>UnreachableStateException</code> without detail message.
     */
    public UnreachableStateException() {
    }

    /**
     * Constructs an instance of <code>UnreachableStateException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnreachableStateException(String msg) {
        super(msg);
    }

    /**
     * Creates a new instance of <code>UnreachableStateException</code> without detail message.
     */
    public UnreachableStateException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an instance of <code>UnreachableStateException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnreachableStateException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
