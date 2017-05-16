/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.search;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author alexv
 */
public class BlindSearcher<S extends State<S, M>, M extends State.Move<S, M>>
        extends AbstractSearcher<S, M> {

    private static final Logger LOGGER = getLogger(BlindSearcher.class);

    public static enum SearchType {

        DEPTH_FIRST, BREATH_FIRST;

    }

    private final SearchType searchType;

    public BlindSearcher(S initial_state, S goal_state, SearchType search_type) {
        super(initial_state, goal_state);
        searchType = search_type;
    }

    @Override
    public List<? extends M> solution() {

        Map<S, StateData<S, M>> states_data = new HashMap<S, StateData<S, M>>();

        Deque<StateData<S, M>> states_to_inspect =
                new LinkedList<StateData<S, M>>();

        StateData<S, M> state_data = new StateData<S, M>(initialState);
        states_data.put(initialState, state_data);

        states_to_inspect.add(state_data);

        while(states_to_inspect.size() > 0) {
            if(states_to_inspect.size() > maxStatesToInspect) {
                maxStatesToInspect = states_to_inspect.size();
            }

            state_data = states_to_inspect.removeFirst();

            S current_state = state_data.state;
            if(goalState.equals(current_state)) {
                List<M> solution_path = new ArrayList<M>();
                while(state_data.fromState != null) {
                    solution_path.add(0, state_data.movement);
                    state_data = states_data.get(state_data.fromState);
                }

                generatedStates = states_data.size();
                statesLeftToInspect = states_to_inspect.size();

                return solution_path;
            }

            visitedStates++;
            Set<M> possible_moves = current_state.movements();

            for(M potential_move : possible_moves) {
                S next_state = current_state.move(potential_move);

                if(!states_data.containsKey(next_state)) {
                    StateData<S, M> next_state_data =
                            new StateData<S, M>(next_state);
                    next_state_data.fromState = current_state;
                    next_state_data.movement = potential_move;
                    states_data.put(next_state, next_state_data);

                    switch(searchType) {
                        case DEPTH_FIRST:
                            states_to_inspect.addFirst(next_state_data);
                            break;
                        case BREATH_FIRST:
                            states_to_inspect.addLast(next_state_data);
                            break;
                    }
                } else {
                    alreadyGenerated++;
                }
            }
        }

        generatedStates = states_data.size();
        throw new UnreachableStateException();
    }

    private static class StateData<S extends State<S, M>, M extends State.Move<S, M>> {

        private final S state;

        private S fromState;

        private M movement;

        private StateData(S my_state) {
            state = my_state;
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
