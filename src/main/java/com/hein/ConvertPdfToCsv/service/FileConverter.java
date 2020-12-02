package com.hein.ConvertPdfToCsv.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileConverter {
    public File multiPath2File(MultipartFile file) throws IOException {
        File convFile = new File("images/" + file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    // public String resizeImage(BufferedImage originalImage, int targetWidth, int
    // targetHeight) throws IOException {
    // BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight,
    // BufferedImage.TYPE_INT_RGB);
    // Graphics2D graphics2D = resizedImage.createGraphics();
    // graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
    // graphics2D.dispose();
    // return imgToBase64String(resizedImage, "png");
    // }

    // public String resizeImage(BufferedImage originalImage, int targetWidth, int
    // targetHeight) throws IOException {
    // // double scale = determineImageScale(originalImage.getWidth(),
    // originalImage.getHeight(), targetWidth, targetHeight);
    // Image resultingImage = originalImage.getScaledInstance(targetWidth ,
    // targetHeight , Image.SCALE_SMOOTH);
    // BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight,
    // BufferedImage.TYPE_INT_RGB);
    // outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
    // return imgToBase64String(outputImage, "png");
    // }

    public String resizeImage(BufferedImage originalImage, int targetWidth) throws Exception {
        BufferedImage outputImage = Scalr.resize(originalImage, targetWidth);
        return imgToBase64String(outputImage, "png");
    }

    // private double determineImageScale(int sourceWidth, int sourceHeight, int
    // targetWidth, int targetHeight) {

    // double scalex = (double) targetWidth / sourceWidth;
    // double scaley = (double) targetHeight / sourceHeight;
    // return Math.min(scalex, scaley);
    // }

    public BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }

    public String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, formatName, os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public void saveImage(String imageName, BufferedImage bufferedImage) {

        File imageFile = new File("images/" + imageName);
        try {
            ImageIO.write(bufferedImage, "png", imageFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteImage(String imageName) {
        Path imagePath = Paths.get("images/" + imageName);
        try {
            Files.delete(imagePath);
            System.out.println("File successfully removed");
        } catch (IOException e) {
            System.err.println("Unable to delete due to...");
            e.printStackTrace();
        }
    }
}
