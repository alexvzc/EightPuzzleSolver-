/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.itesm.gda.ia4000_1.eightpuzzlesolver;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import mx.itesm.gda.ia4000_1.search.Heuristic;
import mx.itesm.gda.ia4000_1.search.State;

/**
 *
 * @author alexv
 */
public class TileSquareState implements
        State<TileSquareState, TileSquareState.Move> {

    public static final int MAX_ROWS = 3;

    public static final int MAX_COLS = 3;

    public static final int EMPTY_TILE = 0;

    public static final int TILE_SPACES = MAX_ROWS * MAX_COLS;

    public static final TileSquareState RESULT_A =
            new TileSquareState(0, 1, 2, 3, 4, 5, 6, 7, 8);

    public static final TileSquareState RESULT_B =
            new TileSquareState(1, 2, 3, 8, 0, 4, 7, 6, 5);

    private final int[] elements;

    public TileSquareState(int... my_elements) {
        elements = Arrays.copyOf(my_elements, my_elements.length);
    }

    public static int indexOf(int[] array, int item) {
        for(int i = 0; i < array.length; i++) {
            if(array[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public static boolean between(int i, int start, int end) {
        return Math.min(start, end) <= i && i <= Math.max(start, end);
    }

    public TileSquareState getResultSet() {
        int n = 0;

        for(int i = 0; i < (TILE_SPACES - 1); i++) {
            int tile_a = elements[i];
            if(tile_a > 1) {
                for(int j = (i + 1); j < TILE_SPACES; j++) {
                    int tile_b = elements[j];
                    if((tile_b > 0) && (tile_b < tile_a)) {
                        n++;
                    }
                }
            }
        }

        return ((n & 1) == 0) ? RESULT_A : RESULT_B;
    }

    public TileSquareState checkStateConsistency()
            throws IllegalArgumentException {
        if(elements.length != TILE_SPACES) {
            throw new IllegalArgumentException();
        }

        BitSet bit_set = new BitSet();

        for(int elem : elements) {
            bit_set.flip(elem);
        }

        if(bit_set.length() != TILE_SPACES
                || bit_set.cardinality() != TILE_SPACES) {
            throw new IllegalArgumentException();
        }

        return this;
    }

    @Override
    public Set<Move> movements() {
        int index = indexOf(elements, EMPTY_TILE);
        int column = index % MAX_COLS;
        int row = index / MAX_COLS;

        Set<Move> set = new HashSet<Move>();

        if(row > 0) {
            int tile_p = column + (row - 1) * MAX_COLS;
            set.add(new Move(tile_p));
        }
        if(column > 0) {
            int tile_p = (column - 1) + row * MAX_COLS;
            set.add(new Move(tile_p));
        }
        if(row < (MAX_ROWS - 1)) {
            int tile_p = column + (row + 1) * MAX_COLS;
            set.add(new Move(tile_p));
        }
        if(column < (MAX_COLS - 1)) {
            int tile_p = (column + 1) + row * MAX_COLS;
            set.add(new Move(tile_p));
        }

        return set;
    }

    @Override
    public TileSquareState move(Move movement) {
        int empty_tile_position = indexOf(elements, EMPTY_TILE);
        int tile_face = elements[movement.tilePosition];

        int[] new_elements = Arrays.copyOf(elements, elements.length);
        new_elements[empty_tile_position] = tile_face;
        new_elements[movement.tilePosition] = EMPTY_TILE;

        return new TileSquareState(new_elements);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < TILE_SPACES; i++) {
            if(i % MAX_COLS == 0) {
                sb.append("|");
            } else {
                sb.append(" ");
            }

            int tile_face = elements[i];

            if(tile_face != EMPTY_TILE) {
                sb.append(tile_face);
            } else {
                sb.append("-");
            }
        }

        sb.replace(0, 1, "[").append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }

        if(obj == null || !(obj instanceof TileSquareState)) {
            return false;
        }

        TileSquareState that = (TileSquareState)obj;

        if(elements.length != that.elements.length) {
            return false;
        }

        for(int i = 0; i < elements.length; i++) {
            if(elements[i] != that.elements[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        for(int elem : elements) {
            hash = 31 * hash + Integer.valueOf(elem).hashCode();
        }
        return hash;
    }

    public static enum Direction {

        UP, DOWN, LEFT, RIGHT;

        public static Direction findDirection(int from, int to) {
            int from_col = from % MAX_COLS;
            int from_row = from / MAX_COLS;
            int to_col = to % MAX_COLS;
            int to_row = to / MAX_COLS;

            if(from_col == to_col) {
                if((from_row + 1) == to_row) {
                    return DOWN;
                } else if((from_row - 1) == to_row) {
                    return UP;
                }
            } else if(from_row == to_row) {
                if((from_col + 1) == to_col) {
                    return RIGHT;
                } else if((from_col - 1) == to_col) {
                    return LEFT;
                }
            }

            throw new IllegalStateException();
        }

    }

    public static class Move
            implements State.Move<TileSquareState, TileSquareState.Move> {

        private final int tilePosition;

        private Move(int tile_position) {
            tilePosition = tile_position;
        }

        @Override
        public long cost() {
            return 1l;
        }

        @Override
        public String toString(TileSquareState from) {
            int tileFace = from.elements[tilePosition];
            Direction direction = Direction.findDirection(tilePosition,
                    indexOf(from.elements, EMPTY_TILE));

            return new StringBuilder("Move tile [").append(tileFace).
                    append("] ").
                    append(direction.toString().toLowerCase()).toString();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }

            if(obj == null || !(obj instanceof Move)) {
                return false;
            }

            Move that = (Move)obj;
            return that.tilePosition == tilePosition;
        }

        @Override
        public int hashCode() {
            return Integer.valueOf(tilePosition).hashCode();
        }

    }

    public static class Manhattan implements Heuristic<TileSquareState> {

        private final double bias;

        public Manhattan() {
            this(1.0);
        }

        public Manhattan(double my_bias) {
            bias = my_bias;
        }

        @Override
        public long distance(TileSquareState current, TileSquareState goal) {
            long ret = 0;

            for(int i = 0; i < TILE_SPACES; i++) {
                int tile_face = current.elements[i];
                if(tile_face == EMPTY_TILE) {
                    continue;
                }
                int goal_position = indexOf(goal.elements, tile_face);

                if(goal_position == i) {
                    continue;
                }

                int tile_col = i % MAX_COLS;
                int tile_row = i / MAX_COLS;
                int goal_col = goal_position % MAX_COLS;
                int goal_row = goal_position / MAX_COLS;

                ret += Math.abs(tile_col - goal_col)
                        + Math.abs(tile_row - goal_row);

            }

            return (long)(ret * bias);
        }

    }

}
