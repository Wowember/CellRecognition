package ru.spbau.mit.wowember;

import ru.spbau.mit.wowember.utils.Constants;
import ru.spbau.mit.wowember.utils.Coordinate;
import ru.spbau.mit.wowember.utils.Functions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CellRecognizer {

    private final int width;
    private final int height;
    private final File imageFile;
    private List<Cell> cells = new ArrayList<>();
    private final BufferedImage image;
    private int[][] pixelsArray;
    private int[][] pxA;


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

        Filters.toBlackAndWhiteImage(pixelsArray);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, pixelsArray[i][j]);
            }
        }

        new File(imageFile.getParent() + "Recognized").mkdir();
        String pathToRecognizedImage = imageFile.getParent() + "Recognized\\"
                + imageFile.getName().replace(".tif", "");
        new File(pathToRecognizedImage).mkdir();
        File recognizedImageFile = new File(pathToRecognizedImage + ".tif");
        recognizedImageFile.createNewFile();
        ImageIO.write(image, "tif", recognizedImageFile);
        for (int i = 0; i < cells.size(); i++) {
            File file = new File(pathToRecognizedImage + "\\" + i + ".tif");
            Filters.toBlackAndWhiteImage(cells.get(i).getPixelsArray());
            ImageIO.write(cells.get(i).getImage(), "tif", file);
        }
    }
}
