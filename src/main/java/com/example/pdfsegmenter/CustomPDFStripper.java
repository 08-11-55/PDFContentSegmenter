package com.example.pdfsegmenter;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom PDF stripper that extends PDFTextStripper to collect and analyze text positions
 * and identify segments based on vertical whitespace.
 */
public class CustomPDFStripper extends PDFTextStripper {

    // List to hold the text positions extracted from the PDF
    private List<TextPosition> textPositions;

    // Default threshold for what we consider significant whitespace (in points)
    private static final double DEFAULT_MIN_WHITESPACE_HEIGHT = 15.0;

    /**
     * Constructor that initializes the PDF stripper.
     * @throws IOException if there is an issue with PDF parsing.
     */
    public CustomPDFStripper() throws IOException {
        super();
        textPositions = new ArrayList<>();
    }

    /**
     * Overrides writeString to collect the text positions as the PDF is processed.
     *
     * @param text          The text being processed.
     * @param textPositions The positions of the characters.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        // Store all text positions for further processing
        this.textPositions.addAll(textPositions);
        // Call super if you want to retain default functionality
        super.writeString(text, textPositions);
    }

    /**
     * Returns the collected text positions.
     * @return List of TextPosition objects.
     */
    public List<TextPosition> getTextPositions() {
        return textPositions;
    }

    /**
     * Clears the collected text positions.
     */
    public void clearTextPositions() {
        textPositions.clear();
    }

    /**
     * Segments text blocks into distinct groups based on significant vertical whitespace.
     * @param minWhitespaceHeight The threshold for considering vertical space as significant.
     * @return A list of text segments, each segment being a list of TextPosition objects.
     */
    public List<List<TextPosition>> segmentTextByWhitespace(double minWhitespaceHeight) {
        List<List<TextPosition>> segments = new ArrayList<>();
        List<TextPosition> currentSegment = new ArrayList<>();

        if (textPositions.isEmpty()) {
            return segments; // Return empty list if no text positions are found
        }

        currentSegment.add(textPositions.get(0));

        // Loop through all text positions and check for vertical whitespace
        for (int i = 1; i < textPositions.size(); i++) {
            TextPosition currentPosition = textPositions.get(i);
            TextPosition previousPosition = textPositions.get(i - 1);

            // Calculate vertical space between current and previous text position
            double spaceHeight = calculateVerticalSpace(previousPosition, currentPosition);

            // If the vertical space is greater than the defined threshold, start a new segment
            if (spaceHeight > minWhitespaceHeight) {
                segments.add(currentSegment);
                currentSegment = new ArrayList<>();
            }

            currentSegment.add(currentPosition);
        }

        // Add the last segment if it's non-empty
        if (!currentSegment.isEmpty()) {
            segments.add(currentSegment);
        }

        return segments;
    }

    /**
     * Segments text blocks into distinct groups using a default whitespace threshold.
     * @return A list of text segments, each segment being a list of TextPosition objects.
     */
    public List<List<TextPosition>> segmentTextByWhitespace() {
        return segmentTextByWhitespace(DEFAULT_MIN_WHITESPACE_HEIGHT);
    }

    /**
     * Calculates the vertical space between two text positions.
     *
     * @param previousPosition The previous TextPosition.
     * @param currentPosition  The current TextPosition.
     * @return The vertical space between the two positions.
     */
    private double calculateVerticalSpace(TextPosition previousPosition, TextPosition currentPosition) {
        return currentPosition.getY() - (previousPosition.getY() + previousPosition.getHeight());
    }

    public List<List<TextPosition>> identifyTextSegments(double minWhitespaceHeight) {
        throw new UnsupportedOperationException("Unimplemented method 'identifyTextSegments'");
    }
}
