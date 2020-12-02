package com.hein.ConvertPdfToCsv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

@Service
public class Morphology_3Run {
    // static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    @Autowired
    private FileConverter fileService;

    public Object getLinesRemovedImage(File imageFile, Boolean isGray, Boolean getBase64) throws Exception {

        try {
            Mat srcMat = Imgcodecs.imread(imageFile.getPath());
            Mat originalMat = new Mat();
            if (!isGray) {
                originalMat = srcMat.clone();
            }
            Mat gray = new Mat();
            Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_BGR2GRAY);

            // Convert to grey scale image (text and line white, background black)
            Imgproc.threshold(gray, gray, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

            // Detect Horizontal lines
            Mat horizontal_kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(40, 1));
            Mat remove_horizontal = new Mat();
            for (int i = 0; i < 2; i++) {
                Imgproc.morphologyEx(gray, remove_horizontal, Imgproc.MORPH_OPEN, horizontal_kernel);
            }
            // Get points of Horizontal lines
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(remove_horizontal, contours, new Mat(), Imgproc.RETR_EXTERNAL,
                    Imgproc.CHAIN_APPROX_SIMPLE);
            // Line Color
            Scalar color = new Scalar(0, 0, 0);
            if (!isGray) {
                // line color for gray image
                color = new Scalar(255, 255, 255);
            }
            // Overwrite horizontal black lines with white lines
            if (!isGray) {
                Imgproc.drawContours(originalMat, contours, -1, color, 2);
            } else {
                Imgproc.drawContours(gray, contours, -1, color, 2);
            }
            // Save result image to disk
            // BufferedImage img1 = fileService.Mat2BufferedImage(result);
            // File ouptut = new File("horizontalRemoved.jpg");
            // ImageIO.write(img1, "jpg", ouptut);

            // Detect Vertical lines
            Mat vertical_kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 40));
            Mat remove_vertical = new Mat();
            for (int i = 0; i < 2; i++) {
                Imgproc.morphologyEx(gray, remove_vertical, Imgproc.MORPH_OPEN, vertical_kernel);
            }
            // Get points of Vertical lines
            List<MatOfPoint> vertical_contours = new ArrayList<>();
            Imgproc.findContours(remove_vertical, vertical_contours, new Mat(), Imgproc.RETR_EXTERNAL,
                    Imgproc.CHAIN_APPROX_SIMPLE);
            // Line Color
            // Overwrite horizontal black lines with white lines
            if (!isGray) {
                Imgproc.drawContours(originalMat, vertical_contours, -1, color, 2);
            } else {
                Imgproc.drawContours(gray, vertical_contours, -1, new Scalar(0,0,0), 2);
            }
            BufferedImage bufferedImage;
            if (!isGray) {
                bufferedImage = fileService.Mat2BufferedImage(originalMat);
            }
            else{
                // TODO :: dilation and erosion
                // gray = smoothImage(gray);
                bufferedImage = fileService.Mat2BufferedImage(gray);    
            }
            // delete uploaded image
            fileService.deleteImage(imageFile.getName());
            fileService.saveImage("linesRemoved.png", bufferedImage);

            if(getBase64){
                // Save final result Image to disk
                fileService.saveImage("linesRemoved.png", bufferedImage);
                return fileService.imgToBase64String(bufferedImage, "png");
            } else{
                return bufferedImage;
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return "Remove Lines Failed";
    }

    public String getTableDetectedImage(File imageFile) throws Exception {
        try {
            Mat srcMat = Imgcodecs.imread(imageFile.getPath());
            // Check if image is loaded fine
            if (srcMat.empty()) {
                System.out.println("Error opening image: " + imageFile);
                throw new Exception("Image Error");
            }

            Mat originalMat = srcMat.clone();
            Mat gray = new Mat();
            // Convert image to gray
            Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_BGR2GRAY);

            // Convert to grey scale image (text and line white, background black)
            Imgproc.threshold(gray, gray, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

            // Detect Horizontal lines
            Mat horizontal_kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(40, 1));
            Mat detect_horizontal = new Mat();
            for (int i = 0; i < 2; i++) {
                Imgproc.morphologyEx(gray, detect_horizontal, Imgproc.MORPH_OPEN, horizontal_kernel);
            }
            // get points of horizontal lines
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(detect_horizontal, contours, new Mat(), Imgproc.RETR_EXTERNAL,
                    Imgproc.CHAIN_APPROX_SIMPLE);
            // Line Color
            Scalar color = new Scalar(0, 128, 0);
            // Overrite horizontal black lines with green lines
            Imgproc.drawContours(originalMat, contours, -1, color, 2);

            // Get Vertical lines
            Mat vertical_kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 40));
            Mat detect_vertical = new Mat();
            for (int i = 0; i < 2; i++) {
                Imgproc.morphologyEx(gray, detect_vertical, Imgproc.MORPH_OPEN, vertical_kernel);
            }
            // get points of vertical lines
            List<MatOfPoint> vertical_contours = new ArrayList<>();
            Imgproc.findContours(detect_vertical, vertical_contours, new Mat(), Imgproc.RETR_EXTERNAL,
                    Imgproc.CHAIN_APPROX_SIMPLE);
            // Line Color
            // Overrite vertical black lines with green lines
            Imgproc.drawContours(originalMat, vertical_contours, -1, color, 2);

            BufferedImage img2 = fileService.Mat2BufferedImage(originalMat);
            // Save result Image to disk
            // File ouptut2 = new File("tableDetected.png");
            // ImageIO.write(img2, "png", ouptut2);
            // return base64 image

            String result = fileService.imgToBase64String(img2, "png");
            fileService.deleteImage(imageFile.getName());
            // Path imagePath = Paths.get("tableDetected.png");
            // Files.delete(imagePath);
            // System.out.print(result);
            return result;
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return "Table Detect Failed";
    }

    // code credit https://github.com/IBM/opencv-power/blob/master/samples/java/tutorial_code/ImgProc/morph_lines_detection/Morphology_3.java
    private Mat smoothImage(Mat grayImage) {
         // Extract edges and smooth image according to the logic
        // 1. extract edges
        // 2. dilate(edges)
        // 3. src.copyTo(smooth)
        // 4. blur smooth img
        // 5. smooth.copyTo(src, edges)

        // Step 1
        Mat edges = new Mat();
        Imgproc.adaptiveThreshold(grayImage, edges, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 3, -2);
        
        // Step 2
        Mat kernel = Mat.ones(2, 2, CvType.CV_8UC1);
        Imgproc.dilate(edges, edges, kernel);
       

        // Step 3
        Mat smooth = new Mat();
        grayImage.copyTo(smooth);

        // Step 4
        Imgproc.blur(smooth, smooth, new Size(2, 2));

        // Step 5
        smooth.copyTo(grayImage, edges);

        return grayImage;
    }

}
