/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.eightpuzzlesolver;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import mx.itesm.gda.ia4000_1.eightpuzzlesolver.binding.Results;
import mx.itesm.gda.ia4000_1.eightpuzzlesolver.binding.SearchResult;
import mx.itesm.gda.ia4000_1.eightpuzzlesolver.binding.TestcaseResult;
import mx.itesm.gda.ia4000_1.search.AStarSearcher;
import mx.itesm.gda.ia4000_1.search.AbstractSearcher;
import mx.itesm.gda.ia4000_1.search.BlindSearcher;
import mx.itesm.gda.ia4000_1.search.State;
import mx.itesm.gda.ia4000_1.search.UnreachableStateException;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author alexv
 */
public class GenerateData {

    private static final Logger LOGGER = getLogger(GenerateData.class);

    private ExecutorService executor;

    private Lock consoleLock;

    private Random rng;

    public int[] generate_random() {
        int[] elements = new int[TileSquareState.TILE_SPACES];
        List<Integer> source = new ArrayList<Integer>(
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        for(int i = 0; i < TileSquareState.TILE_SPACES; i++) {
            elements[i] = source.remove(rng.nextInt(source.size()));
        }
        return elements;
    }

    public TestcaseResult runRandomTest() {
        TileSquareState initial_state = new TileSquareState(generate_random());
        TileSquareState goal_state = initial_state.getResultSet();

        TestcaseResult testcase_data = new TestcaseResult();
        testcase_data.setInitialState(initial_state.toString());
        testcase_data.setFinalState(goal_state.toString());

        BlindSearcher<TileSquareState, TileSquareState.Move> breath_searcher =
                new BlindSearcher<TileSquareState, TileSquareState.Move>(
                initial_state, goal_state, BlindSearcher.SearchType.BREATH_FIRST);

        Future<SearchResult> breath_results = executor.submit(
                new ExecuteSearch("Breath-first", breath_searcher));

        AStarSearcher<TileSquareState, TileSquareState.Move> manhattan_searcher =
                new AStarSearcher<TileSquareState, TileSquareState.Move>(
                initial_state, goal_state, new TileSquareState.Manhattan());

        Future<SearchResult> manhattan_results = executor.submit(
                new ExecuteSearch("Manhattan 100%", manhattan_searcher));

        AStarSearcher<TileSquareState, TileSquareState.Move> manhattan80_searcher =
                new AStarSearcher<TileSquareState, TileSquareState.Move>(
                initial_state, goal_state, new TileSquareState.Manhattan(0.8));

        Future<SearchResult> manhattan80_results = executor.submit(
                new ExecuteSearch("Manhattan 80%", manhattan80_searcher));

        AStarSearcher<TileSquareState, TileSquareState.Move> manhattan90_searcher =
                new AStarSearcher<TileSquareState, TileSquareState.Move>(
                initial_state, goal_state, new TileSquareState.Manhattan(0.9));

        Future<SearchResult> manhattan90_results = executor.submit(
                new ExecuteSearch("Manhattan 90%", manhattan90_searcher));

        AStarSearcher<TileSquareState, TileSquareState.Move> manhattan150_searcher =
                new AStarSearcher<TileSquareState, TileSquareState.Move>(
                initial_state, goal_state, new TileSquareState.Manhattan(1.5));

        Future<SearchResult> manhattan150_results = executor.submit(
                new ExecuteSearch("Manhattan 150%", manhattan150_searcher));

        AStarSearcher<TileSquareState, TileSquareState.Move> manhattan200_searcher =
                new AStarSearcher<TileSquareState, TileSquareState.Move>(
                initial_state, goal_state, new TileSquareState.Manhattan(2));

        Future<SearchResult> manhattan200_results = executor.submit(
                new ExecuteSearch("Manhattan 200%", manhattan200_searcher));

        AStarSearcher<TileSquareState, TileSquareState.Move> manhattan300_searcher =
                new AStarSearcher<TileSquareState, TileSquareState.Move>(
                initial_state, goal_state, new TileSquareState.Manhattan(3));

        Future<SearchResult> manhattan300_results = executor.submit(
                new ExecuteSearch("Manhattan 300%", manhattan300_searcher));

        try {
            testcase_data.setBreathResults(breath_results.get());
            testcase_data.setManhathanResults(manhattan_results.get());
            testcase_data.setManhathan80Results(manhattan80_results.get());
            testcase_data.setManhathan90Results(manhattan90_results.get());
            testcase_data.setManhathan150Results(manhattan150_results.get());
            testcase_data.setManhathan200Results(manhattan200_results.get());
            testcase_data.setManhathan300Results(manhattan300_results.get());
        } catch(InterruptedException ie) {
        } catch(ExecutionException ee) {
        }

        return testcase_data;
    }

    public class ExecuteSearch
            implements Callable<SearchResult> {

        private final String algorithm;

        private final AbstractSearcher<?, ?> searcher;

        private ExecuteSearch(String my_algorithm,
                AbstractSearcher<?, ?> my_searcher) {
            algorithm = my_algorithm;
            searcher = my_searcher;
        }

        @Override
        public SearchResult call() throws Exception {
            return runSearch(algorithm, searcher);
        }

    }

    public <S extends State<S, M>, M extends State.Move<S, M>>
                      SearchResult runSearch(
            String algorithm, AbstractSearcher<S, M> searcher) {
        SearchResult search_data = new SearchResult();

        consoleLock.lock();
        try {
            LOGGER.info("Looking for solution " + algorithm + " for "
                    + searcher.getInitialState() + " -> "
                    + searcher.getGoalState());
        } finally {
            consoleLock.unlock();
        }

        long nano_time = System.nanoTime();
        List<? extends M> solution;

        try {
            nano_time = System.nanoTime();
            solution = searcher.solution();
            nano_time = System.nanoTime() - nano_time;

        } catch(UnreachableStateException e) {
            nano_time = System.nanoTime() - nano_time;
            consoleLock.lock();
            try {
                search_data.setExecTime(nano_time);
                LOGGER.info("Solution " + algorithm + " not found for "
                        + searcher.getInitialState() + " -> "
                        + searcher.getGoalState() + ": Unreachable state");
                LOGGER.info("Completed in: " + (nano_time / 1e6) + "ms");
                search_data.setGeneratedStates(searcher.generatedStates);
                LOGGER.info("Generated states: " + searcher.generatedStates);
                search_data.setVisitedStates(searcher.visitedStates);
                LOGGER.info("Visited states: " + searcher.visitedStates);
                search_data.setMaxQueuedStates(searcher.maxStatesToInspect);
                LOGGER.info("Maximum queued states to visit: "
                        + searcher.maxStatesToInspect);
                search_data.setUnvisitedStates(searcher.statesLeftToInspect);
                LOGGER.info("States left to inspect: "
                        + searcher.statesLeftToInspect);
                search_data.setDuplicatedStates(searcher.alreadyGenerated);
                LOGGER.info("States already generated: "
                        + searcher.alreadyGenerated);
            } finally {
                consoleLock.unlock();
            }

            return null;

        } catch(Exception e) {
            consoleLock.lock();
            try {
                LOGGER.info("Solution " + algorithm + " not found for "
                        + searcher.getInitialState() + " -> "
                        + searcher.getGoalState() + ": ", e);
            } finally {
                consoleLock.unlock();
            }
            return null;

        }

        consoleLock.lock();
        try {
            LOGGER.info("Solution " + algorithm + " found for "
                    + searcher.getInitialState() + " -> "
                    + searcher.getGoalState());
            search_data.setSolutionSteps(solution.size());
            LOGGER.info("Solution found in " + solution.size() + " steps");
            search_data.setExecTime(nano_time);
            LOGGER.info("Completed in: " + (nano_time / 1e6) + "ms");
            search_data.setGeneratedStates(searcher.generatedStates);
            LOGGER.info("Generated states: " + searcher.generatedStates);
            search_data.setVisitedStates(searcher.visitedStates);
            LOGGER.info("Visited states: " + searcher.visitedStates);
            search_data.setMaxQueuedStates(searcher.maxStatesToInspect);
            LOGGER.info("Maximum states to visit: "
                    + searcher.maxStatesToInspect);
            search_data.setUnvisitedStates(searcher.statesLeftToInspect);
            LOGGER.info("States left to inspect: "
                    + searcher.statesLeftToInspect);
            search_data.setDuplicatedStates(searcher.alreadyGenerated);
            LOGGER.info("States already generated: "
                    + searcher.alreadyGenerated);
        } finally {
            consoleLock.unlock();
        }

        return search_data;

    }

    public GenerateData() {
        rng = new SecureRandom();
        executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        consoleLock = new ReentrantLock();
    }

    public void runTestCases() {
        final int CASES = 100;
        Results data = new Results();
        Runtime rt = Runtime.getRuntime();
        LOGGER.info("Running " + CASES + " random test cases");
        for(int i = 0; i < CASES; i++) {
            rt.gc();
            data.getTestcaseResult().add(runRandomTest());
        }

        try {
            JAXBContext ctx = JAXBContext.newInstance(
                    "mx.itesm.gda.ia4000_1.eightpuzzlesolver.binding");
            Marshaller m = ctx.createMarshaller();
            m.setProperty("jaxb.formatted.output", true);
            FileWriter writer = new FileWriter("result-data.xml");
            m.marshal(data, writer);
            writer.close();

        } catch(IOException ioe) {
            LOGGER.error("IO error", ioe);
        } catch(JAXBException jaxbe) {
            LOGGER.error("JAXB error", jaxbe);
        }

    }

    public static void main(String[] args) {
        GenerateData generator = new GenerateData();
        generator.runTestCases();
    }

}
