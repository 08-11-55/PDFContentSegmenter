package com.example.pdfsegmenter;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PDFContentSegmenterTest {

    @Test
    public void testSegmentPDF() {
        try {
            // Load the file from the classpath
            URL resource = getClass().getClassLoader().getResource("test.pdf"); // If placed in src/test/resources/test
            if (resource == null) {
                fail("test.pdf not found in classpath");
            }

            File inputFile = new File(resource.getFile());

            // Specify the number of cuts you want to test
            int numberOfCuts = 3;

            // Call your PDF segmentation method
            PDFContentSegmenter.segmentPDF(inputFile.getAbsolutePath(), numberOfCuts);

            // Verify the output
            // Assuming output files are named as Segment_0.pdf, Segment_1.pdf, etc.
            for (int i = 0; i < numberOfCuts; i++) {
                File segmentFile = new File("Segment_" + i + ".pdf");
                assertTrue("Segment file Segment_" + i + ".pdf should exist", segmentFile.exists());
            }

        } catch (IOException e) {
            fail("IOException thrown: " + e.getMessage());
        }
    }

    @After
    public void cleanUp() {
        // Clean up generated files
        for (int i = 0; i < 3; i++) {
            File segmentFile = new File("Segment_" + i + ".pdf");
            if (segmentFile.exists()) {
                segmentFile.delete();
            }
        }
    }
}
