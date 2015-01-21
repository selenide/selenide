package integration;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.util.PDFTextStripper;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;


public class PdfContentTest extends IntegrationTest {
    private static String PDF_URL = "http://www.analysis.im/uploads/seminar/pdf-sample.pdf";


    @Test
    public void containSpecificObject() throws IOException {
        URL url = new URL(PDF_URL);

        BufferedInputStream fileToParse = new BufferedInputStream(url.openStream());
        PDFParser parser = new PDFParser(fileToParse);
        parser.parse();
        String output = new PDFTextStripper().getText(parser.getPDDocument());
        assertTrue(output.contains("Adobe Acrobat PDF Files"));
    }


}
