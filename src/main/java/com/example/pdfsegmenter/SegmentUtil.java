package com.example.pdfsegmenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.text.TextPosition;

public class SegmentUtil {

    /**
     * Determines the largest vertical gaps (Y-axis cuts) between text positions for segmenting.
     
     */
    public static List<Float> determineCuts(List<TextPosition> textPositions, int numberOfCuts) {
        // Get distinct Y-positions from text, sorted in ascending order
        List<Float> yPositions = textPositions.stream()
                                                .map(TextPosition::getY)
                                                .distinct()
                                                .sorted()
                                                .collect(Collectors.toList());

        // Calculate gaps between consecutive Y-positions
        List<Float> gaps = new ArrayList<>();
        for (int i = 1; i < yPositions.size(); i++) {
            gaps.add(yPositions.get(i) - yPositions.get(i - 1));
        }

        // Find the largest gaps (to make the cuts)
        List<Float> largestGaps = gaps.stream()
                                      .sorted(Collections.reverseOrder())
                                      .limit(numberOfCuts)
                                      .collect(Collectors.toList());

        return largestGaps;
    }

    public static void createSegmentedPDFs(PDDocument document, List<Float> cutPositions,
                                         List<TextPosition> textPositions, String outputDir) throws IOException {
        List<TextPosition> currentSegment = new ArrayList<>();
        int segmentIndex = 0;

        for (PDPage page : document.getPages()) {
            @SuppressWarnings("unused")
            float pageHeight = page.getMediaBox().getHeight();
            
            // Split text based on Y-coordinate cut positions
            for (TextPosition position : textPositions) {
                float yPosition = position.getY();

                // If the current text position is beyond the current cut, create a new segment
                if (!cutPositions.isEmpty() && yPosition > cutPositions.get(0)) {
                    saveSegment(document, page, currentSegment, outputDir, segmentIndex++);
                    currentSegment.clear(); // Start a new segment
                    cutPositions.remove(0); // Remove the cut after processing
                }

                // Add the current text position to the ongoing segment
                currentSegment.add(position);
            }

            // Save the final segment for the current page
            if (!currentSegment.isEmpty()) {
                saveSegment(document, page, currentSegment, outputDir, segmentIndex++);
                currentSegment.clear();
            }
        }
    }

    private static void saveSegment(PDDocument document, PDPage page, List<TextPosition> segmentText, 
                                    String outputDir, int segmentIndex) throws IOException {
        PDDocument newDoc = new PDDocument();
        PDPage newPage = new PDPage(page.getMediaBox());
        newDoc.addPage(newPage);

        try (PDPageContentStream contentStream = new PDPageContentStream(newDoc, newPage)) {
            // Render each TextPosition in the segment
            for (TextPosition textPosition : segmentText) {
                contentStream.beginText();
                contentStream.setFont(textPosition.getFont(), textPosition.getFontSize());
                contentStream.newLineAtOffset(textPosition.getX(), textPosition.getY());
                contentStream.showText(textPosition.getUnicode());
                contentStream.endText();
            }
        }

        // Save the segment PDF
        String outputFilePath = outputDir + "/Segment_" + segmentIndex + ".pdf";
        newDoc.save(outputFilePath);
        newDoc.close();
    }
}
