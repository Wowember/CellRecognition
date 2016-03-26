package ru.spbau.mit.wowember;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        //String path = args[0];
        String path = "C:\\Users\\Wowember\\Desktop\\07-27-11-taxolVero\\control";
                // "C:\\Users\\Wowember\\Desktop\\07-27-11-taxolVero\\5mkg";
                // "C:\\Users\\Wowember\\Desktop\\07-27-11-taxolVero\\01mkg";
                // "C:\\Users\\Wowember\\Desktop\\07-27-11-taxolVero\\1mkg";
                //"C:\\Users\\Wowember\\Desktop\\Images";
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
        for (CellRecognizer cr: cellRecognizerList) {
            try {
                cr.recognizeAndSave();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
