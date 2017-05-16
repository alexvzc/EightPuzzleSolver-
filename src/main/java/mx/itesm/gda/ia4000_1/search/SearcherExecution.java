/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.search;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author alexv
 */
public class SearcherExecution<S extends State<S, M>, M extends State.Move<S, M>>
        implements Callable<List<? extends M>> {

    private static final Logger LOGGER = getLogger(SearcherExecution.class);

    private static final Lock LOG_LOCK = new ReentrantLock();

    private String algorithm;

    private AbstractSearcher<S, M> searcher;

    public SearcherExecution(String my_algorithm,
            AbstractSearcher<S, M> my_searcher) {
        algorithm = my_algorithm;
        searcher = my_searcher;
    }

    @Override
    public List<? extends M> call() {
        LOG_LOCK.lock();
        try {
            LOGGER.info("Looking for solution " + algorithm + " for "
                    + searcher.getInitialState() + " -> "
                    + searcher.getGoalState());
        } finally {
            LOG_LOCK.unlock();
        }

        long nano_time = System.nanoTime();
        List<? extends M> solution;

        try {
            nano_time = System.nanoTime();
            solution = searcher.solution();
            nano_time = System.nanoTime() - nano_time;

        } catch(UnreachableStateException e) {
            nano_time = System.nanoTime() - nano_time;
            LOG_LOCK.lock();
            try {
                LOGGER.info("Solution " + algorithm + " not found for "
                        + searcher.getInitialState() + " -> "
                        + searcher.getGoalState() + ": Unreachable state");
                LOGGER.info("Completed in: " + (nano_time / 1e6) + "ms");
                LOGGER.info("Generated states: " + searcher.generatedStates);
                LOGGER.info("Visited states: " + searcher.visitedStates);
                LOGGER.info("Maximum queued states to visit: "
                        + searcher.maxStatesToInspect);
                LOGGER.info("States left to inspect: "
                        + searcher.statesLeftToInspect);
                LOGGER.info("States already generated: "
                        + searcher.alreadyGenerated);
            } finally {
                LOG_LOCK.unlock();
            }

            return null;

        } catch(Exception e) {
            LOG_LOCK.lock();
            try {
                LOGGER.info("Solution " + algorithm + " not found for "
                        + searcher.getInitialState() + " -> "
                        + searcher.getGoalState() + ": ", e);
            } finally {
                LOG_LOCK.unlock();
            }
            return null;

        }

        LOG_LOCK.lock();
        try {
            LOGGER.info("Solution " + algorithm + " found for "
                    + searcher.getInitialState() + " -> "
                    + searcher.getGoalState());
            LOGGER.info("Solution found in " + solution.size() + " steps");
            LOGGER.info("Completed in: " + (nano_time / 1e6) + "ms");
            LOGGER.info("Generated states: " + searcher.generatedStates);
            LOGGER.info("Visited states: " + searcher.visitedStates);
            LOGGER.info("Maximum states to visit: "
                    + searcher.maxStatesToInspect);
            LOGGER.info("States left to inspect: "
                    + searcher.statesLeftToInspect);
            LOGGER.info("States already generated: "
                    + searcher.alreadyGenerated);

            List<? extends M> displayed_soution = solution;

            if(displayed_soution.size() > 50) {
                LOGGER.info("Displaying up to 50 steps");
                displayed_soution = displayed_soution.subList(0, 50);
            }

            S current_state = searcher.getInitialState();
            for(M step : displayed_soution) {
                S next_state = current_state.move(step);
                LOGGER.info(current_state + " -> " + next_state + ": "
                        + step.toString(current_state));
                current_state = next_state;
            }

        } finally {
            LOG_LOCK.unlock();
        }

        return solution;
    }

}
