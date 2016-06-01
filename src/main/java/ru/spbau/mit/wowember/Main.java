package ru.spbau.mit.wowember;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        String path = "test";



        List<String> cellImageList = Arrays.asList(new File(path).list()).stream()
                .filter(x -> x.endsWith(".tif")).collect(Collectors.toList());
        List<CellRecognizer> cellRecognizerList = new ArrayList<>();
        for (String cell: cellImageList) {
            try {
                cellRecognizerList.add(new CellRecognizer(path + "\\" + cell));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long directoryAverageFluorescence = 0;
        long directoryPixelsCount = 0;
        for (CellRecognizer cr: cellRecognizerList) {
            try {
                cr.recognizeAndSave();
                for (Cell cell: cr.getCells()) {
                    directoryPixelsCount += cell.getNonBlackPixelsCount();
                    directoryAverageFluorescence += cell.getAverageForNonBlackPixelsFluorescence()
                            * cell.getNonBlackPixelsCount();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.print("DirectoryAverageFluorescence = "
                + (long) (directoryAverageFluorescence / (double) directoryPixelsCount) + "\n");
    }
}
