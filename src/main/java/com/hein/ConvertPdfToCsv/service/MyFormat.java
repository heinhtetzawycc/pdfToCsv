package com.hein.ConvertPdfToCsv.service;
import org.springframework.stereotype.Service;

import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;

import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import java.util.List;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class MyFormat {

    public Object getLinesRemovedImage(File imageFile, Boolean isGray, Boolean getBase64) throws Exception {

        try {
            Mat srcMat = Imgcodecs.imread(imageFile.getPath());
            Mat originalMat = new Mat();
            if (!isGray) {
                originalMat = srcMat.clone();
            }
            // Step 2.1
            Mat gray = new Mat();
            Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_BGR2GRAY);

            // Convert to grey scale image (text and line white, background black)
            Imgproc.threshold(gray, gray, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
            // Setp 2.2
            // Detect Horizontal lines
            Mat horizontal_kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(40, 1));
            Mat remove_horizontal = new Mat();
            for (int i = 0; i < 2; i++) {
                Imgproc.morphologyEx(gray, remove_horizontal, Imgproc.MORPH_OPEN, horizontal_kernel);
            }
            // Step 2.3
            // Get points of Horizontal lines
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(remove_horizontal, contours, new Mat(), Imgproc.RETR_EXTERNAL,
                    Imgproc.CHAIN_APPROX_SIMPLE);
            // Line Color

            // line color for gray image
            Scalar color = new Scalar(255, 255, 255);

            // Overwrite horizontal black lines with white lines
            // Step 2.4
            Imgproc.drawContours(originalMat, contours, -1, color, 2);

            // Save result image to disk -- if you want to save on disk
            // BufferedImage img1 = Mat2BufferedImage(result);
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
            Imgproc.drawContours(originalMat, vertical_contours, -1, color, 2);

            BufferedImage bufferedImage;

            bufferedImage = Mat2BufferedImage(originalMat);
            return imgToBase64String(bufferedImage, "png");

        } catch (IOException e1) {
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

            BufferedImage img2 = Mat2BufferedImage(originalMat);
            // Save result Image to disk
            File ouptut2 = new File("tableDetected.png");
            ImageIO.write(img2, "png", ouptut2);
            // return base64 image

            String result = imgToBase64String(img2, "png");

            return result;
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return "Table Detect Failed";
    }

    private BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }

    private String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, formatName, os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

}
