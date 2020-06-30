package com.fatmax.beerfit.utilities;

import java.nio.charset.StandardCharsets;
import com.google.common.io.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.fatmax.beerfit.utilities.CSVWriter.DEFAULT_ESCAPE_CHARACTER;
import static com.fatmax.beerfit.utilities.CSVWriter.DEFAULT_LINE_END;
import static com.fatmax.beerfit.utilities.CSVWriter.DEFAULT_QUOTE_CHARACTER;
import static com.fatmax.beerfit.utilities.CSVWriter.DEFAULT_SEPARATOR;
import static com.fatmax.beerfit.utilities.CSVWriter.NO_ESCAPE_CHARACTER;
import static com.fatmax.beerfit.utilities.CSVWriter.NO_QUOTE_CHARACTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVWriterUnitTest {

    File sampleFile = new File("sample.csv");

    @BeforeEach
    public void createFile() throws IOException {
        sampleFile.createNewFile();
    }

    @AfterEach
    public void deleteFile() throws IOException {
        sampleFile.delete();
    }

    @Test
    public void writeNextNullTest() throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileOutputStream(sampleFile));
        csvWriter.writeNext(null);
        csvWriter.close();
        assertEquals("",Files.toString(sampleFile, StandardCharsets.UTF_8));
    }

    @Test
    public void writeNextAlsoNullTest() throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileOutputStream(sampleFile));
        csvWriter.writeNext(new String[] {null});
        csvWriter.close();
        assertEquals("\n",Files.toString(sampleFile, StandardCharsets.UTF_8));
    }

    @Test
    public void writeNextSimpleTest() throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileOutputStream(sampleFile));
        csvWriter.writeNext(new String[]{"hello","world"});
        csvWriter.close();
        assertEquals("\"hello\",\"world\"\n",Files.toString(sampleFile, StandardCharsets.UTF_8));
    }

    @Test
    public void writeNextSimpleTwoLinesTest() throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileOutputStream(sampleFile));
        csvWriter.writeNext(new String[]{"hello"});
        csvWriter.writeNext(new String[]{"world"});
        csvWriter.close();
        assertEquals("\"hello\"\n\"world\"\n",Files.toString(sampleFile, StandardCharsets.UTF_8));
    }

    @Test
    public void writeNextNoWrapQuoteTest() throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileOutputStream(sampleFile), DEFAULT_SEPARATOR,
                NO_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
        csvWriter.writeNext(new String[]{"hello","world"});
        csvWriter.close();
        assertEquals("hello,world\n",Files.toString(sampleFile, StandardCharsets.UTF_8));
    }

    @Test
    public void writeNextQuotesTest() throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileOutputStream(sampleFile));
        csvWriter.writeNext(new String[]{"he\"llo","world"});
        csvWriter.close();
        assertEquals("\"he\"\"llo\",\"world\"\n",Files.toString(sampleFile, StandardCharsets.UTF_8));
    }

    @Test
    public void writeNextNoEscapeTest() throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileOutputStream(sampleFile), DEFAULT_SEPARATOR,
                DEFAULT_QUOTE_CHARACTER, NO_ESCAPE_CHARACTER, DEFAULT_LINE_END);
        csvWriter.writeNext(new String[]{"he\"llo","world"});
        csvWriter.close();
        assertEquals("\"he\"llo\",\"world\"\n",Files.toString(sampleFile, StandardCharsets.UTF_8));
    }

    @Test
    public void writeNextEscapeCharTest() throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileOutputStream(sampleFile), DEFAULT_SEPARATOR,
                DEFAULT_QUOTE_CHARACTER, '|', DEFAULT_LINE_END);
        csvWriter.writeNext(new String[]{"he|llo","world"});
        csvWriter.close();
        assertEquals("\"he||llo\",\"world\"\n",Files.toString(sampleFile, StandardCharsets.UTF_8));
    }

    @Test
    public void closeTest() throws FileNotFoundException {
        CSVWriter csvWriter = new CSVWriter(new FileOutputStream(sampleFile));
        csvWriter.close();
    }
}
