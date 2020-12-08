package com.hein.ConvertPdfToCsv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.opencv.core.Core;

import org.springframework.stereotype.Service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import java.awt.Rectangle;

@Service
public class ImageToText {
    @Autowired
    private Morphology_3Run imageService;

    public String convert(File imgFile, String sperator, ArrayList<String> areas, String lan)
            throws TesseractException, IOException {
        // get lines removed image in gray

        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Morphology_3Run morpho = new Morphology_3Run();

        try {
            JSONObject obj = new JSONObject();
            JSONObject obj1 = new JSONObject();
            BufferedImage resultImage = (BufferedImage) imageService.getLinesRemovedImage(imgFile, false, false);

            ITesseract _tesseract = new Tesseract();
            _tesseract.setLanguage(lan);
            _tesseract.setTessVariable("user_defined_dpi", "300");

            for (int i = 0; i < areas.size(); i++) {

                String firstArea = areas.get(i);
                // System.out.println(firstArea);
                String[] strArr = firstArea.split("-");

                double imageWidth = resultImage.getWidth();
                double imageHeight = resultImage.getHeight();
                double top = Double.parseDouble(strArr[0]); // y
                top = (imageHeight * top) / 100;
                double left = Double.parseDouble(strArr[1]); // x
                left = (imageWidth * left) / 100;
                double bottom = Double.parseDouble(strArr[2]);
                bottom = (imageHeight * bottom) / 100;
                double right = Double.parseDouble(strArr[3]);
                right = (imageWidth * right) / 100;
                double width = right - left;
                double height = bottom - top;
                Rectangle rect = new Rectangle();

                rect.setRect(left, top, width, height);

                String result = _tesseract.doOCR(resultImage, rect);
                if (result.length() > 0) {
                    result = result.substring(0, result.length() - 1);
                }
                
                // System.out.println(result);
                obj1.put(Integer.toString(i + 1), result.replace(" ", sperator));
            }
            obj.put("csv", obj1);
            obj.put("rectangleCount", Integer.toString(areas.size()));
            return obj.toString();
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "Failed";

    }
}
