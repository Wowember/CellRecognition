package ru.spbau.mit.wowember;

import ru.spbau.mit.wowember.utils.Constants;
import ru.spbau.mit.wowember.utils.Coordinate;
import ru.spbau.mit.wowember.utils.Functions;

import java.util.ArrayList;
import java.util.List;

public class Filters {

    public static void neighbourFluorescenceFilter(Cell cell) {
        int[][] newPixelsArray = new int[cell.getWidth()][cell.getHeight()];
        for (int i = 0; i < cell.getWidth(); i++) {
            for (int j = 0; j < cell.getHeight(); j++) {
                long pixelFluorescence = Functions.getPixelFluorescence(cell.getPixelsArray()[i][j]);
                for (int k = 0; k < Constants.MOVE.length; k++) {
                    int ni = i + Constants.MOVE[k][0];
                    int nj = j + Constants.MOVE[k][1];
                    if (Functions.isAvailable(new Coordinate(ni, nj), cell)) {
                        pixelFluorescence += Functions.getPixelFluorescence(cell.getPixelsArray()[ni][nj]);
                    }
                }
                newPixelsArray[i][j] = (int) (pixelFluorescence / (double) Constants.NEAREST_NEIGHBORS_NUMBER);
            }
        }
        cell.setPixelsArray(newPixelsArray);
    }

    public static void averageFluorescenceFilter(Cell cell) {
        for (int i = 0; i < cell.getWidth(); i++) {
            for (int j = 0; j < cell.getHeight(); j++) {
                if (Functions.getPixelFluorescence(cell.getPixelsArray()[i][j]) < cell.getAverageFluorescence()) {
                    cell.getPixelsArray()[i][j] = Constants.BLACK_COLOR.getRGB();
                }
            }
        }
    }

    public static void averageFluorescenceWithoutBlackPixelsFilter(Cell cell) {
        long averageForNonBlackPixelsFluorescence = cell.getAverageForNonBlackPixelsFluorescence();
        for (int i = 0; i < cell.getWidth(); i++) {
            for (int j = 0; j < cell.getHeight(); j++) {
                if (Functions.getPixelFluorescence(cell.getPixelsArray()[i][j])
                        < averageForNonBlackPixelsFluorescence) {
                    cell.getPixelsArray()[i][j] = Constants.BLACK_COLOR.getRGB();
                }
            }
        }
    }

    public static void fluorescenceFilter(Cell cell, long fluorescence) {
        for (int i = 0; i < cell.getWidth(); i++) {
            for (int j = 0; j < cell.getHeight(); j++) {
                if (Functions.getPixelFluorescence(cell.getPixelsArray()[i][j])
                        < fluorescence) {
                    cell.getPixelsArray()[i][j] = Constants.BLACK_COLOR.getRGB();
                }
            }
        }
    }

    public static List<Cell> averageFluorescenceBinarySearchFilter(List<Cell> cells) {
        List<Cell> newCells = new ArrayList<>();
        for (Cell cell: cells) {
            Filters.averageFluorescenceFilter(cell);
            boolean flag = false;
            long l = 0, r = 0, m = 0;
            for (int i = 0; i < 5; i++) {
                l = (long) (Functions.getPixelFluorescence(Constants.BLACK_COLOR.getRGB()) * i * 0.2);
                r = (long) (cell.getAverageForNonBlackPixelsFluorescence() * (i + 1) * 0.2);
                while (r - l > 1) {
                    m = (l + r) / 2;
                    Cell tmp = new Cell(cell);
                    fluorescenceFilter(tmp, m);
                    if (tmp.divideIntoCells().size() <= 1) {
                        l = m;
                    } else {
                        flag = true;
                        r = m;
                    }
                }
                if (flag) {
                    break;
                }
            }
            if (flag) {
                Cell tmp = new Cell(cell);
                fluorescenceFilter(tmp, r);
                newCells.addAll(averageFluorescenceBinarySearchFilter(tmp.divideIntoCells()));
            } else {
                newCells.add(cell);
            }
        }
        return newCells;
    }

    public static void toBlackAndWhiteImage(int[][] pixelsArray) {
        for (int i = 0; i < pixelsArray.length; i++) {
            for (int j = 0; j < pixelsArray[i].length; j++) {
                if (pixelsArray[i][j] != Constants.BLACK_COLOR.getRGB()) {
                    pixelsArray[i][j] = Constants.WHITE_COLOR.getRGB();
                }
            }
        }
    }

    /*private int getBlackCountInCircle(int radius) {

    }
    public static int findNucleus(Cell cell) {
        for (int i = 0; i < cell.getWidth(); i++) {
            for (int j = 0; j < cell.getHeight(); j++) {
                int sum = 1;
                int sumBlack = 0;
                for (int k = 0; k < 100; k++) {
                    sumBlack += getBlackCountInCircle(k);
                    sum += k * 4;
                }

            }
        }
    }*/
}
