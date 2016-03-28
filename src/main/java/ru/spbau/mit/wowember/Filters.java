package ru.spbau.mit.wowember;

import ru.spbau.mit.wowember.utils.Constants;
import ru.spbau.mit.wowember.utils.Coordinate;
import ru.spbau.mit.wowember.utils.Functions;

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

    public static void AverageFluorescenceFilter(Cell cell) {
        for (int i = 0; i < cell.getWidth(); i++) {
            for (int j = 0; j < cell.getHeight(); j++) {
                if (Functions.getPixelFluorescence(cell.getPixelsArray()[i][j]) < cell.getAverageFluorescence()) {
                    cell.getPixelsArray()[i][j] = Constants.BLACK_COLOR.getRGB();
                }
            }
        }
    }

    public static void AverageFluorescenceWithoutBlackPixelsFilter(Cell cell) {
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

    public static void toBlackAndWhiteImage(int[][] pixelsArray) {
        for (int i = 0; i < pixelsArray.length; i++) {
            for (int j = 0; j < pixelsArray[i].length; j++) {
                if (pixelsArray[i][j] != Constants.BLACK_COLOR.getRGB()) {
                    pixelsArray[i][j] = Constants.WHITE_COLOR.getRGB();
                }
            }
        }
    }
}
