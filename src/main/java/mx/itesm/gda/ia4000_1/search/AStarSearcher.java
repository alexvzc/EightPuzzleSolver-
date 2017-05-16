/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author alexv
 */
public class AStarSearcher<S extends State<S, M>, M extends State.Move<S, M>>
        extends AbstractSearcher<S, M> {

    private static final Logger LOGGER = getLogger(AStarSearcher.class);

    private final Heuristic<S> heuristic;

    public AStarSearcher(S initial_state, S goal_state,
            Heuristic<S> my_heuristic) {
        super(initial_state, goal_state);
        heuristic = my_heuristic;
    }

    @Override
    public List<? extends M> solution() {

        Map<S, StateData<S, M>> states_data = new HashMap<S, StateData<S, M>>();

        SortedSet<StateData<S, M>> states_to_inspect =
                new TreeSet<StateData<S, M>>();

        Set<S> visited_nodes = new HashSet<S>();

        StateData<S, M> state_data = new StateData<S, M>(initialState,
                heuristic.distance(initialState, goalState));
        state_data.fScore = state_data.hScore;
        states_data.put(initialState, state_data);

        states_to_inspect.add(state_data);

        while(states_to_inspect.size() > 0) {
            if(states_to_inspect.size() > maxStatesToInspect) {
                maxStatesToInspect = states_to_inspect.size();
            }

            state_data = states_to_inspect.first();

            S current_state = state_data.state;
            if(goalState.equals(current_state)) {
                List<M> solution_path = new ArrayList<M>();
                while(state_data.fromState != null) {
                    solution_path.add(0, state_data.movement);
                    state_data = states_data.get(state_data.fromState);
                }

                generatedStates = states_data.size();
                statesLeftToInspect = states_to_inspect.size() - 1;

                return solution_path;
            }

            states_to_inspect.remove(state_data);
            visited_nodes.add(current_state);

            visitedStates++;
            Set<M> possible_moves = current_state.movements();

            for(M potential_move : possible_moves) {
                S next_state = current_state.move(potential_move);

                if(!visited_nodes.contains(next_state)) {
                    StateData<S, M> next_state_data;
                    long potential_g_score = state_data.gScore
                            + potential_move.cost();

                    if(!states_data.containsKey(next_state)) {
                        next_state_data = new StateData<S, M>(next_state,
                                heuristic.distance(next_state, goalState));
                        next_state_data.fromState = current_state;
                        next_state_data.movement = potential_move;
                        next_state_data.gScore = potential_g_score;
                        next_state_data.fScore = next_state_data.gScore
                                + next_state_data.hScore;
                        states_data.put(next_state, next_state_data);
                        states_to_inspect.add(next_state_data);
                    } else {
                        next_state_data = states_data.get(next_state);
                        if(potential_g_score < next_state_data.gScore) {
                            states_to_inspect.remove(next_state_data);
                            next_state_data.fromState = current_state;
                            next_state_data.movement = potential_move;
                            next_state_data.gScore = potential_g_score;
                            next_state_data.fScore = next_state_data.gScore
                                    + next_state_data.hScore;
                            states_to_inspect.add(next_state_data);
                        }
                    }
                } else {
                    alreadyGenerated++;
                }
            }
        }

        generatedStates = states_data.size();
        throw new UnreachableStateException();
    }

    private static class StateData<S extends State<S, M>, M extends State.Move<S, M>>
            implements Comparable<StateData<S, M>> {

        private final S state;

        private final long hScore;

        private S fromState;

        private M movement;

        private long gScore;

        private long fScore;

        private StateData(S my_state, long my_h_score) {
            state = my_state;
            hScore = my_h_score;
        }

        @Override
        public int compareTo(StateData<S, M> that) {
            int ret = Long.valueOf(fScore).compareTo(that.fScore);
            if(ret == 0) {
                ret = Integer.valueOf(hashCode()).compareTo(that.hashCode());
            }
            return ret;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            if(obj == null || !(obj instanceof StateData<?, ?>)) {
                return false;
            }
            return state.equals(((StateData<?, ?>)obj).state);
        }

        @Override
        public int hashCode() {
            return state.hashCode();
        }

    }

}
