package com.hein.ConvertPdfToCsv.controller;

import com.hein.ConvertPdfToCsv.service.PDFToCsvExtractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SimpleController {
    @Autowired
    private PDFToCsvExtractor pdfToCsvExtractor;
    @PostMapping("/api/pdf/convertCsv")
    public @ResponseBody ResponseEntity<String> extractCsvFromPDFFile(@RequestParam("file") MultipartFile file) {
        
        try {
           
            PDFToCsvExtractor extractor = pdfToCsvExtractor
            .setSource(file.getInputStream());
            int [] lines = new int[]{0,1,2,3,5,6,7,8,9,10,11,12,13};
            String  resultString = extractor
           .exceptPage(0) //skip first page
            .exceptLine(lines ) // skip lines 1 and 2
            .extract();
            return new ResponseEntity<String>(resultString, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
