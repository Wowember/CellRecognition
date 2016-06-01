package ru.spbau.mit.wowember.utils;

import ru.spbau.mit.wowember.Cell;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

public final class Functions {

    public static int getPixelFluorescence(int pixelRGB) {
        return (int) ((pixelRGB & Constants.BLUE) * 0.11f + ((pixelRGB & Constants.GREEN) >> 8) * 0.59f
                + ((pixelRGB & Constants.RED) >> 16) * 0.3f);
    }

    public static boolean isAvailable(Coordinate coordinate, Cell cell) {
        return coordinate.getX() >= 0 && coordinate.getX() < cell.getWidth()
                && coordinate.getY() >= 0 && coordinate.getY() < cell.getHeight();
    }

    public static boolean bfs(Coordinate start, int[][] used, int cellNumber, Cell cell) {
        used[start.getX()][start.getY()] = cellNumber;
        Queue<Coordinate> queue = new ArrayDeque<>();
        queue.add(start);
        int pixelsCount = 0;
        while (!queue.isEmpty()) {
            Coordinate currentPixel = queue.poll();
            pixelsCount++;
            for (int i = 0; i < Constants.MOVE.length; i++) {
                int nx = currentPixel.getX() + Constants.MOVE[i][0];
                int ny = currentPixel.getY() + Constants.MOVE[i][1];
                Coordinate nextPixel = new Coordinate(nx, ny);
                if (isAvailable(nextPixel, cell) && used[nx][ny] == 0
                        && cell.getPixelsArray()[nx][ny] != Constants.BLACK_COLOR.getRGB()) {
                    used[nx][ny] = cellNumber;
                    queue.add(nextPixel);
                }
            }
        }
        return pixelsCount >= Constants.MINIMAL_CELL_SIZE;
    }

    public static void cellRecovery(Coordinate start,
                                    int[][] currentNumber, int[][] distance, int cellNumber, Cell cell) {
        int[][] used = new int[cell.getWidth()][cell.getHeight()];
        used[start.getX()][start.getY()] = 1;
        Deque<Coordinate> deque = new ArrayDeque<>();
        deque.add(start);
        while(!deque.isEmpty()) {
            Coordinate currentPixel = deque.pollFirst();
            for (int i = 0; i < Constants.MOVE.length; i++) {
                int nx = currentPixel.getX() + Constants.MOVE[i][0];
                int ny = currentPixel.getY() + Constants.MOVE[i][1];
                Coordinate nextPixel = new Coordinate(nx, ny);
                if (isAvailable(nextPixel, cell) && cell.getPixelsArray()[nx][ny] != Constants.BLACK_COLOR.getRGB()) {
                    if (used[nx][ny] == 0 && currentNumber[nx][ny] == cellNumber) {
                        distance[nx][ny] = distance[currentPixel.getX()][currentPixel.getY()];
                        used[nx][ny] = 1;
                        deque.addFirst(nextPixel);
                    } else if (distance[nx][ny] > distance[currentPixel.getX()][currentPixel.getY()] + 1) {
                        distance[nx][ny] = distance[currentPixel.getX()][currentPixel.getY()] + 1;
                        currentNumber[nx][ny] = cellNumber;
                        if (used[nx][ny] == 0) {
                            used[nx][ny] = 1;
                            deque.addLast(nextPixel);
                        }
                    }
                }
            }
        }
    }

}
