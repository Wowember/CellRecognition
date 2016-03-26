package ru.spbau.mit.wowember.utils;

import java.awt.*;

public final class Constants {

    public static final Color WHITE_COLOR = new Color(255, 255, 255);
    public static final Color BLACK_COLOR = new Color(0, 0, 0);
    public static final int MINIMAL_CELL_SIZE = 5000;

    public static final int BLUE = 0x0000ff;
    public static final int GREEN = 0x00ff00;
    public static final int RED = 0xff0000;

    public static final int[][] MOVE = {{1, 0}, {1, 1}, {0, 1}, {-1, 1},
            {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};

    public static final int NEAREST_NEIGHBORS_NUMBER = 9;

}
