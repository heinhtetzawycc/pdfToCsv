package com.hein.ConvertPdfToCsv.controller;

import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.Image;
import com.hein.ConvertPdfToCsv.service.FileConverter;
import com.hein.ConvertPdfToCsv.service.ImageToText;
import com.hein.ConvertPdfToCsv.service.PDFToCsvExtractor;
import com.hein.ConvertPdfToCsv.service.Morphology_3Run;

import org.json.JSONObject;
import org.opencv.core.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SimpleController {

    String origin = "https://bea2d70a0001.ngrok.io/build/web";
    @Autowired
    private PDFToCsvExtractor pdfToCsvExtractor;
    @Autowired
    private ImageToText imageToTextConvertor;
    @Autowired
    private FileConverter fileConverter;
    @Autowired
    private Morphology_3Run imageService;

    @CrossOrigin(origins = "http://127.0.0.1:8081")
    @PostMapping("/api/pdf/convertCsv")
    public @ResponseBody ResponseEntity<String> extractCsvFromPDFFile(@RequestParam("file") MultipartFile file) {

        try {

            PDFToCsvExtractor extractor = pdfToCsvExtractor.setSource(file.getInputStream());
            int[] lines = new int[] { 0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
            String resultString = extractor
                    // .exceptPage(0) //skip first page
                    // .exceptLine(lines ) // skip lines 1 and 2
                    .extract();
            return new ResponseEntity<String>(resultString, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(origins = "http://127.0.0.1:8081")
    // @CrossOrigin(origins = "http://bea2d70a0001.ngrok.io/")
    @PostMapping("/api/image/convertText")
    public @ResponseBody ResponseEntity<String> extractTextFromImage(@RequestParam("imageFile") MultipartFile file,
            @RequestParam(value = "sperator", defaultValue = "\t") String sperator,  @RequestParam(value = "areas", defaultValue = "0-0-0-0") ArrayList<String> areas, @RequestParam(value = "language", defaultValue = "mya") String lan) {
                
        try {
            System.out.println(areas);
            File imgFile = fileConverter.multiPath2File(file);
            String resultString = imageToTextConvertor.convert(imgFile, sperator, areas, lan);
            JSONObject obj = new JSONObject();
            JSONObject obj1 = new JSONObject();
            obj1.put("1", resultString);
            obj.put("csv", obj1);
            obj.put("rectangleCount", "1");
            return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(origins = "http://127.0.0.1:8081")
    // @CrossOrigin(origins = "http://bea2d70a0001.ngrok.io/")
    @PostMapping("/api/image/tableDetectedImage")
    public @ResponseBody ResponseEntity<String> detectTable(@RequestParam("imageFile") MultipartFile file) {

        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            File imgFile = fileConverter.multiPath2File(file);
            String resultString = imageService.getTableDetectedImage(imgFile);
            JSONObject obj = new JSONObject();
            obj.put("decodedImage", resultString);
            return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(origins = "http://127.0.0.1:8081")
    // @CrossOrigin(origins = "http://bea2d70a0001.ngrok.io/")
    @PostMapping("/api/image/resizeImage")
    public @ResponseBody ResponseEntity<String> resizeImage(@RequestParam("imageFile") MultipartFile file,@RequestParam("resizeType") String resizeType) {
        try {
            File imgFile = fileConverter.multiPath2File(file);
            String resultString = fileConverter.resizeImage(ImageIO.read(imgFile), Integer.parseInt(resizeType));
            JSONObject obj = new JSONObject();
            obj.put("decodedImage", resultString);
            return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
