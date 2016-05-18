package ru.spbau.mit.wowember;

import ru.spbau.mit.wowember.utils.Constants;
import ru.spbau.mit.wowember.utils.Coordinate;
import ru.spbau.mit.wowember.utils.Functions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CellRecognizer {

    private final int width;
    private final int height;
    private final File imageFile;
    private List<Cell> cells = new ArrayList<>();
    private final BufferedImage image;
    private int[][] pixelsArray;
    private int[][] pxA;
    private int allSellsCount = 0;
    private int recognizedSellsCount = 0;


    public CellRecognizer(String pathToImage) throws IOException {

        imageFile = new File(pathToImage);
        image = ImageIO.read(imageFile);
        width = image.getWidth();
        height = image.getHeight();

        pixelsArray = new int[width][height];
        pxA = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixelsArray[i][j] = image.getRGB(i, j);
                pxA[i][j] = pixelsArray[i][j];
            }
        }
        cells.add(new Cell(pixelsArray, new int[width][height], 0, new Coordinate(0, 0)));
    }

    private void applyFilterToImage(int[][] filterPixelsArray) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (filterPixelsArray[i][j] == Constants.BLACK_COLOR.getRGB()) {
                    pixelsArray[i][j] = Constants.BLACK_COLOR.getRGB();
                }
            }
        }
    }

    public void recognizeAndSave() throws IOException {

        System.err.print("recognizing\n");

        List<Cell> newCells = new ArrayList<>();
        for (Cell cell: cells) {
            Filters.averageFluorescenceFilter(cell);
            newCells.addAll(cell.divideIntoCells());
        }
        System.err.println(newCells.size());
        cells.clear();
        List<Cell> tmp = cells;
        cells = newCells;
        newCells = tmp;

        /*for (Cell cell: cells) {
            Filters.averageFluorescenceWithoutBlackPixelsFilter(cell);
            newCells.addAll(cell.divideIntoCells());
        }
        cells = newCells;
        System.err.println(cells.size());*/

        cells = Filters.averageFluorescenceBinarySearchFilter(cells);
        System.err.println(cells.size());

        save();
    }

    public List<Cell> getCells() {
        return cells;
    }

    private void save() throws IOException {
        int[][] newPixelsArray = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newPixelsArray[i][j] = Constants.BLACK_COLOR.getRGB();
            }
        }
        for (Cell cell: cells) {
            for (int i = 0; i < cell.getWidth(); i++) {
                for (int j = 0; j < cell.getHeight(); j++) {
                    if (cell.getPixelsArray()[i][j] != Constants.BLACK_COLOR.getRGB()) {
                        int ni = i + cell.getUpperLeftCellPixel().getX();
                        int nj = j + cell.getUpperLeftCellPixel().getY();
                        newPixelsArray[ni][nj] = cell.getPixelsArray()[i][j];
                    }
                }
            }
        }
        pixelsArray = newPixelsArray;

        //Cell C = new Cell(pxA, new int[width][height], 0, new Coordinate(0, 0));
        //Filters.averageFluorescenceFilter(C);
        //pixelsArray = C.getPixelsArray();

        //Filters.toBlackAndWhiteImage(pixelsArray);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, pixelsArray[i][j]);
            }
        }

        compareResults();
        System.err.print(imageFile.getName()
                + ":\nFound Sells: " + cells.size()
                + ",\n Correctly Recognized: " + recognizedSellsCount + "/" + allSellsCount
                + ",\n Accuracy: " + ((double) recognizedSellsCount / allSellsCount + "\n\n"));

        new File(imageFile.getParent() + "Recognized").mkdir();
        String pathToRecognizedImage = imageFile.getParent() + "Recognized\\"
                + imageFile.getName().replace(".tif", "");
        new File(pathToRecognizedImage).mkdir();
        File recognizedImageFile = new File(pathToRecognizedImage + ".tif");
        recognizedImageFile.createNewFile();
        ImageIO.write(image, "tif", recognizedImageFile);
        for (int i = 0; i < cells.size(); i++) {
            File file = new File(pathToRecognizedImage + "\\" + i + ".tif");
            //Filters.toBlackAndWhiteImage(cells.get(i).getPixelsArray());
            ImageIO.write(cells.get(i).getImage(), "tif", file);
        }
    }

    private void compareResults() throws FileNotFoundException {
        Scanner cellsCoordinateScanner = new Scanner(new File(imageFile.getParent()
                + "\\HandRec_" + imageFile.getName().replace(".tif", ".txt")));
        int cellsCount = cellsCoordinateScanner.nextInt();
        Coordinate[] outerCellCoordinate = new Coordinate[cellsCount];
        Coordinate[] outerCellSize = new Coordinate[cellsCount];
        Coordinate[] innerCellCoordinate = new Coordinate[cellsCount];
        Coordinate[] innerCellSize = new Coordinate[cellsCount];
        for (int i = 0; i < cellsCount; i++) {
            outerCellCoordinate[i] = new Coordinate(cellsCoordinateScanner.nextInt(),
                    cellsCoordinateScanner.nextInt());
            outerCellSize[i] = new Coordinate(cellsCoordinateScanner.nextInt(),
                    cellsCoordinateScanner.nextInt());
            innerCellCoordinate[i] = new Coordinate(cellsCoordinateScanner.nextInt(),
                    cellsCoordinateScanner.nextInt());
            innerCellSize[i] = new Coordinate(cellsCoordinateScanner.nextInt(),
                    cellsCoordinateScanner.nextInt());
        }
        allSellsCount += cellsCount;
        for (Cell cell: cells) {
            for (int i = 0; i < cellsCount; i++) {
                if (/*isInner(outerCellCoordinate[i], outerCellSize[i],
                        cell.getUpperLeftCellPixel(), new Coordinate(cell.getWidth(), cell.getHeight()))
                        && */isInner(cell.getUpperLeftCellPixel(), new Coordinate(cell.getWidth(), cell.getHeight()),
                        innerCellCoordinate[i], innerCellSize[i])) {
                    recognizedSellsCount++;
                    break;
                }
            }
        }
    }

    private boolean isInner(Coordinate outerCoordinate, Coordinate outerSize,
                            Coordinate innerCoordinate, Coordinate innerSize) {
        if (innerCoordinate.getX() < outerCoordinate.getX()
                || innerCoordinate.getY() < outerCoordinate.getY()
                || innerCoordinate.getX() + innerSize.getX() > outerCoordinate.getX() + outerSize.getX()
                || innerCoordinate.getY() + innerSize.getY() > outerCoordinate.getY() + outerSize.getY()) {
            return false;
        }
        return true;
    }
}
