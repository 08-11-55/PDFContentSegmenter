package com.example.pdfsegmenter;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomPDFStripper extends PDFTextStripper {

    // This list will hold the text positions extracted from the PDF
    private List<TextPosition> textPositions = new ArrayList<>();

    public CustomPDFStripper() throws IOException {
        super();
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        // Store all text positions from the PDF into our list for further processing
        this.textPositions.addAll(textPositions);
        
        // Optionally, call the super method if you still want to perform the default text output
        super.writeString(text, textPositions);
    }

    public List<TextPosition> getTextPositions() {
        return textPositions;
    }

    public void clearTextPositions() {
        textPositions.clear();
    }

    // Method to analyze text positions and identify segments based on vertical whitespace
    public List<List<TextPosition>> identifyTextSegments(double minWhitespaceHeight) {
        List<List<TextPosition>> segments = new ArrayList<>();
        List<TextPosition> currentSegment = new ArrayList<>();
        
        if (textPositions.isEmpty()) {
            return segments; // No text positions, return empty list
        }
        
        currentSegment.add(textPositions.get(0));

        for (int i = 1; i < textPositions.size(); i++) {
            TextPosition position = textPositions.get(i);
            TextPosition previousPosition = textPositions.get(i - 1);

            // Calculate the vertical space between text positions
            float spaceHeight = position.getY() - (previousPosition.getY() + previousPosition.getHeight());

            // If the vertical space is greater than the defined threshold, start a new segment
            if (spaceHeight > minWhitespaceHeight) {
                segments.add(currentSegment);
                currentSegment = new ArrayList<>();
            }

            currentSegment.add(position);
        }

        // Add the last segment if there is any
        if (!currentSegment.isEmpty()) {
            segments.add(currentSegment);
        }

        return segments;
    }
}
