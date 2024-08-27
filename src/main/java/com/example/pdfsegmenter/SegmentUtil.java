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

    public static List<Integer> determineCuts(List<TextPosition> textPositions, int numberOfCuts) {
        // Analyze text positions and calculate whitespace gaps
        List<Float> yPositions = textPositions.stream()
                                                .map(TextPosition::getY)
                                                .distinct()
                                                .sorted()
                                                .collect(Collectors.toList());

        List<Integer> gaps = new ArrayList<>();
        for (int i = 1; i < yPositions.size(); i++) {
            gaps.add((int) (yPositions.get(i) - yPositions.get(i - 1)));
        }

        List<Integer> largestGaps = gaps.stream()
                                        .sorted(Collections.reverseOrder())
                                        .limit(numberOfCuts)
                                        .collect(Collectors.toList());

        return largestGaps;
    }

    public static void createSegmentedPDFs(PDDocument document, List<Integer> cutPositions) throws IOException {
        // Use cutPositions to split the original document and save new PDF files
        for (int i = 0; i < cutPositions.size(); i++) {
            PDDocument newDoc = new PDDocument();
            PDPage page = document.getPage(i);
            newDoc.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(newDoc, page)) {
                // Add content to the new document if needed
            }

            newDoc.save("Segment_" + i + ".pdf");
            newDoc.close();
        }
    }
}
