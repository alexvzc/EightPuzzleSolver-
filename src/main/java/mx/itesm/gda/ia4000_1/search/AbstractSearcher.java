/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.search;

/**
 *
 * @author alexv
 */
public abstract class AbstractSearcher<S extends State<S, M>, M extends State.Move<S, M>>
        implements Searcher<S, M> {

    public final S initialState;

    public final S goalState;

    public long generatedStates;

    public long maxStatesToInspect;

    public long statesLeftToInspect;

    public long alreadyGenerated;

    public long visitedStates;

    public AbstractSearcher(S initial_state, S goal_state) {
        initialState = initial_state;
        goalState = goal_state;
        resetStats();
    }

    public void resetStats() {
        generatedStates = 0;
        maxStatesToInspect = 0;
        statesLeftToInspect = 0;
        alreadyGenerated = 0;
        visitedStates = 0;
    }

    /**
     * @return the initialState
     */
    public S getInitialState() {
        return initialState;
    }

    /**
     * @return the goalState
     */
    public S getGoalState() {
        return goalState;
    }

    /**
     * @return the generatedStates
     */
    public long getGeneratedStates() {
        return generatedStates;
    }

    /**
     * @return the maxStatesToInspect
     */
    public long getMaxStatesToInspect() {
        return maxStatesToInspect;
    }

    /**
     * @return the statesLeftToInspect
     */
    public long getStatesLeftToInspect() {
        return statesLeftToInspect;
    }

    /**
     * @return the alreadyGenerated
     */
    public long getAlreadyVisited() {
        return alreadyGenerated;
    }

    /**
     * @return the visitedStates
     */
    public long getVisitedStates() {
        return visitedStates;
    }

}
