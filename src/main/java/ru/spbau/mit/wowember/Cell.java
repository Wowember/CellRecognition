package ru.spbau.mit.wowember;

import ru.spbau.mit.wowember.utils.Constants;
import ru.spbau.mit.wowember.utils.Coordinate;
import ru.spbau.mit.wowember.utils.Functions;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Cell {

    private int width;
    private int height;
    private Coordinate upperLeftCellPixel;

    private int minWidth = (int) 1e9;
    private int minHeight = (int) 1e9;
    private int[][] pixelsArray;
    private long averageFluorescence;

    private void initialize(int[][] pixelsArray, int[][] used, int cellNumber, Coordinate upperLeftCellPixel) {
        int maxWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < used.length; i++) {
            for (int j = 0; j < used[i].length; j++) {
                if (used[i][j] == cellNumber) {
                    minWidth = Math.min(minWidth, i);
                    minHeight = Math.min(minHeight, j);
                    maxWidth = Math.max(maxWidth, i);
                    maxHeight = Math.max(maxHeight, j);
                }
            }
        }
        this.upperLeftCellPixel = new Coordinate(upperLeftCellPixel.getX() + minWidth,
                upperLeftCellPixel.getY() + minHeight);
        width = maxWidth - minWidth + 1;
        height = maxHeight - minHeight + 1;
        this.pixelsArray = new int[width][height];
        for (int i = minWidth; i <= maxWidth; i++) {
            for (int j = minHeight; j <= maxHeight; j++) {
                if (used[i][j] == cellNumber) {
                    this.pixelsArray[i - minWidth][j - minHeight] = pixelsArray[i][j];
                } else {
                    this.pixelsArray[i - minWidth][j - minHeight] = Constants.BLACK_COLOR.getRGB();
                }
            }
        }
        updateAverageFluorescence();
    }

    public Cell(int[][] pixelsArray, int[][] used, int cellNumber, Coordinate upperLeftCellPixel) {
        initialize(pixelsArray, used, cellNumber, upperLeftCellPixel);
    }

    public Cell(Cell cell) {
        initialize(cell.getPixelsArray(),
                new int[cell.getWidth()][cell.getHeight()], 0, cell.getUpperLeftCellPixel());
    }

    private void updateAverageFluorescence() {
        int pixelsCount = width * height;
        averageFluorescence = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                averageFluorescence += Functions.getPixelFluorescence(pixelsArray[i][j]);
            }
        }
        averageFluorescence = (long) (averageFluorescence / (double) pixelsCount);
    }

    public List<Cell> divideIntoCells() {
        int[][] used = new int[width][height];
        int currentCellNumber = 1;
        List<Integer> newCellsNumbers = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (used[i][j] == 0 && pixelsArray[i][j] != Constants.BLACK_COLOR.getRGB()) {
                    if (Functions.bfs(new Coordinate(i, j), used, currentCellNumber, this)) {
                        newCellsNumbers.add(currentCellNumber);
                    }
                    currentCellNumber++;
                }
            }
        }
        List<Cell> newCells = new ArrayList<>();
        for (int i = 0; i < newCellsNumbers.size(); i++) {
            newCells.add(new Cell(pixelsArray, used, newCellsNumbers.get(i), upperLeftCellPixel));
        }
        return newCells;
    }

    public long getAverageFluorescence() {
        return averageFluorescence;
    }

    public long getAverageForNonBlackPixelsFluorescence() {
        int pixelsCount = 0;
        int averageForNonBlackPixelsFluorescence = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixelsArray[i][j] != Constants.BLACK_COLOR.getRGB()) {
                    averageForNonBlackPixelsFluorescence += Functions.getPixelFluorescence(pixelsArray[i][j]);
                    pixelsCount++;
                }
            }
        }
        return (long) (averageForNonBlackPixelsFluorescence / (double) pixelsCount);
    }

    public int getNonBlackPixelsCount() {
        int pixelsCount = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixelsArray[i][j] != Constants.BLACK_COLOR.getRGB()) {
                    pixelsCount++;
                }
            }
        }
        return pixelsCount;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int[][] getPixelsArray() {
        return pixelsArray;
    }

    public Coordinate getUpperLeftCellPixel() {
        return upperLeftCellPixel;
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, pixelsArray[i][j]);
            }
        }
        return image;
    }

    public void setPixelsArray(int[][] pixelsArray) {
        this.pixelsArray = pixelsArray;
    }

}
