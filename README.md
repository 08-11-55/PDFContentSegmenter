# PDFContentSegmenter

## Description
`PDFContentSegmenter` is a Java application designed to segment a PDF file into distinct sections based on vertical whitespace between blocks of text. The application identifies significant gaps in whitespace to determine logical breaks in the content, such as headings or paragraphs. This tool does not use image processing but leverages text analysis to segment PDFs accurately.

## Features
- **Segment PDF Files**: Divides a PDF into multiple segments based on detected whitespace.
- **Customizable Number of Cuts**: Allows specifying the number of segments to create.
- **Preserves Content Integrity**: Ensures text blocks are not split mid-sentence or paragraph.

## Technologies
- **Java 8 or above**: Required for running the application.
- **Apache PDFBox**: Used for PDF processing.

## Setup Instructions

### Prerequisites
- **Java**: Ensure you have Java 8 or higher installed on your system.
- **Apache PDFBox**: The project uses Apache PDFBox for PDF manipulation.

### Clone the Repository
1. Clone the repository from GitHub:
   ```bash
   git clone https://github.com/pulipativenkateshece1/PDFContentSegmenter.git

