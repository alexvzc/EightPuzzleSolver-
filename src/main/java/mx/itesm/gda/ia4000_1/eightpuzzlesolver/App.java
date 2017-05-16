/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.eightpuzzlesolver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import mx.itesm.gda.ia4000_1.search.BlindSearcher;
import mx.itesm.gda.ia4000_1.search.AStarSearcher;
import mx.itesm.gda.ia4000_1.search.SearcherExecution;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author alexv
 */
public class App {

    private static final Logger LOGGER = getLogger(App.class);

    private static final int[] initialState = {5, 6, 3, 8, 2, 0, 7, 4, 1};

    private static final int[] goalState1 = {0, 1, 2, 3, 4, 5, 6, 7, 8};

    private static final int[] goalState2 = {1, 2, 3, 4, 5, 6, 7, 8, 0};

    private static final int[] goalState3 = {1, 2, 3, 8, 0, 4, 7, 6, 5};

    public static void main(String[] args) {

        TileSquareState initial = new TileSquareState(initialState).
                checkStateConsistency();
        TileSquareState goal1 = new TileSquareState(goalState1).
                checkStateConsistency();
        TileSquareState goal2 = new TileSquareState(goalState2).
                checkStateConsistency();
        TileSquareState goal3 = new TileSquareState(goalState3).
                checkStateConsistency();

        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        //ExecutorService executor = Executors.newSingleThreadExecutor();

        long nano_time = System.nanoTime();

        BlindSearcher<TileSquareState, TileSquareState.Move> depth_1st_1 =
                new BlindSearcher<TileSquareState, TileSquareState.Move>(
                initial, goal1, BlindSearcher.SearchType.DEPTH_FIRST);

        executor.submit(
                new SearcherExecution<TileSquareState, TileSquareState.Move>(
                "DEPTH FIRST", depth_1st_1));

        BlindSearcher<TileSquareState, TileSquareState.Move> breath_1st_1 =
                new BlindSearcher<TileSquareState, TileSquareState.Move>(
                initial, goal1, BlindSearcher.SearchType.BREATH_FIRST);

        executor.submit(
                new SearcherExecution<TileSquareState, TileSquareState.Move>(
                "BREATH FIRST", breath_1st_1));

        AStarSearcher<TileSquareState, TileSquareState.Move> a_star_1 =
                new AStarSearcher<TileSquareState, TileSquareState.Move>(
                initial, goal1, new TileSquareState.Manhattan());

        executor.submit(
                new SearcherExecution<TileSquareState, TileSquareState.Move>(
                "A*", a_star_1));

        BlindSearcher<TileSquareState, TileSquareState.Move> depth_1st_2 =
                new BlindSearcher<TileSquareState, TileSquareState.Move>(
                initial, goal2, BlindSearcher.SearchType.DEPTH_FIRST);

        executor.submit(
                new SearcherExecution<TileSquareState, TileSquareState.Move>(
                "DEPTH FIRST", depth_1st_2));

        BlindSearcher<TileSquareState, TileSquareState.Move> breath_1st_2 =
                new BlindSearcher<TileSquareState, TileSquareState.Move>(
                initial, goal2, BlindSearcher.SearchType.BREATH_FIRST);

        executor.submit(
                new SearcherExecution<TileSquareState, TileSquareState.Move>(
                "BREATH FIRST", breath_1st_2));

        AStarSearcher<TileSquareState, TileSquareState.Move> a_star_2 =
                new AStarSearcher<TileSquareState, TileSquareState.Move>(
                initial, goal2, new TileSquareState.Manhattan());

        executor.submit(
                new SearcherExecution<TileSquareState, TileSquareState.Move>(
                "A*", a_star_2));

        BlindSearcher<TileSquareState, TileSquareState.Move> depth_1st_3 =
                new BlindSearcher<TileSquareState, TileSquareState.Move>(
                initial, goal3, BlindSearcher.SearchType.DEPTH_FIRST);

        executor.submit(
                new SearcherExecution<TileSquareState, TileSquareState.Move>(
                "DEPTH FIRST", depth_1st_3));

        BlindSearcher<TileSquareState, TileSquareState.Move> breath_1st_3 =
                new BlindSearcher<TileSquareState, TileSquareState.Move>(
                initial, goal3, BlindSearcher.SearchType.BREATH_FIRST);

        executor.submit(
                new SearcherExecution<TileSquareState, TileSquareState.Move>(
                "BREATH FIRST", breath_1st_3));

        AStarSearcher<TileSquareState, TileSquareState.Move> a_star_3 =
                new AStarSearcher<TileSquareState, TileSquareState.Move>(
                initial, goal3, new TileSquareState.Manhattan());

        executor.submit(
                new SearcherExecution<TileSquareState, TileSquareState.Move>(
                "A*", a_star_3));

        executor.shutdown();
        while(!executor.isTerminated()) {
            try {
                executor.awaitTermination(1, TimeUnit.DAYS);
            } catch(InterruptedException ie) {
            }
        }
        nano_time = System.nanoTime() - nano_time;

        LOGGER.info("Total execution time: " + (nano_time / 1e6) + "ms");
    }

}
