package com.fatmax.beerfit.utilities;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVReaderUnitTest {

    @Test
    public void readNextEmptyTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertNull(csvReader.readNext());
    }

    @Test
    public void readNextTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello world"));
        String[] first = csvReader.readNext();
        assertEquals(1, first.length);
        assertEquals("hello world", first[0]);
        assertNull(csvReader.readNext());
    }

    @Test
    public void readNextTwoLinesTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello world\nfoo"));
        String[] first = csvReader.readNext();
        assertEquals(1, first.length);
        assertEquals("hello world", first[0]);

        String[] second = csvReader.readNext();
        assertEquals(1, second.length);
        assertEquals("foo", second[0]);
        assertNull(csvReader.readNext());
    }

    @Test
    public void readNextTwoColsTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello,world\nfoo"));
        String[] first = csvReader.readNext();
        assertEquals(2, first.length);
        assertEquals("hello", first[0]);
        assertEquals("world", first[1]);

        String[] second = csvReader.readNext();
        assertEquals(1, second.length);
        assertEquals("foo", second[0]);
        assertNull(csvReader.readNext());
    }

    @Test
    public void readNextLineSkipped() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello|world\nfoo"), '|', '"', 1);
        String[] first = csvReader.readNext();
        assertEquals(1, first.length);
        assertEquals("foo", first[0]);
        assertNull(csvReader.readNext());
    }

    @Test
    public void readNextLineInQuotesSkipped() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello\"|world\nfoo\""), '|', '"', 0);
        String[] first = csvReader.readNext();
        assertEquals(1, first.length);
        assertEquals("hello|world\nfoo", first[0]);
        assertNull(csvReader.readNext());
    }

    @Test
    public void parseLineNullTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertNull(csvReader.parseLine(null));
    }

    @Test
    public void parseLineRegular() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        String[] line = csvReader.parseLine("foo");
        assertEquals(1, line.length);
        assertEquals("foo", line[0]);
    }

    @Test
    public void parseLineWithQuoteTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        String[] line = csvReader.parseLine("hello \"world");
        assertEquals(1, line.length);
        assertEquals("hello \"world\n", line[0]);
    }

    @Test
    public void parseLineWithQuotesTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        String[] line = csvReader.parseLine("hello \"world\"");
        assertEquals(1, line.length);
        assertEquals("hello \"world", line[0]);
    }

    @Test
    public void isInQuotesEmptyTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.isInAQuote("", new ArrayList<>(), new StringBuilder(), false));
    }

    @Test
    public void isInQuotesNoQuotesTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.isInAQuote("hello world", new ArrayList<>(), new StringBuilder(), false));
    }

    @Test
    public void isInQuotesQuotesTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.isInAQuote("hello \"world\"", new ArrayList<>(), new StringBuilder(), false));
    }

    @Test
    public void isInQuotesStartQuotesTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertTrue(csvReader.isInAQuote("hello \"world", new ArrayList<>(), new StringBuilder(), false));
    }

    @Test
    public void isInQuotesQuotesInARowTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.isInAQuote("hello \"\"world", new ArrayList<>(), new StringBuilder(), false));
    }

    @Test
    public void isInQuotesQuotesInARowSeparatorInTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.isInAQuote("hello \",\"world", new ArrayList<>(), new StringBuilder(), false));
    }

    @Test
    public void isInQuotesQuotesInARowSeparatorOutTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertTrue(csvReader.isInAQuote("hello, \"\"world", new ArrayList<>(), new StringBuilder(), true));
    }

    @Test
    public void embeddedQuoteBeginningTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.emdeddedQuote("hello \"world\"", 0));
    }

    @Test
    public void embeddedQuoteNotStartEscapeTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.emdeddedQuote("hell,o world", 5));
    }

    @Test
    public void embeddedQuoteNotLongTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.emdeddedQuote("hello world", 10));
    }

    @Test
    public void embeddedQuoteNotEndEscapeTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.emdeddedQuote("hello ,world", 5));
    }

    @Test
    public void embeddedQuoteTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertTrue(csvReader.emdeddedQuote("hello world", 5));
    }

    @Test
    public void embeddedQuoteDifferentSeparatorTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""), '|', '"', 0);
        assertFalse(csvReader.emdeddedQuote("hello |world", 5));
    }

    @Test
    public void twoQuotesInARowNotInQuoteTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.isNextCharAlsoQuote("hello \"world", false, 5));
    }

    @Test
    public void twoQuotesInARowAtEndTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.isNextCharAlsoQuote("hello \"world", true, 13));
    }

    @Test
    public void twoQuotesInARowNotQuoteTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertFalse(csvReader.isNextCharAlsoQuote("hello \"world", true, 7));
    }

    @Test
    public void twoQuotesInARowTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertTrue(csvReader.isNextCharAlsoQuote("hello \"world", true, 5));
    }

    @Test
    public void twoQuotesInARowDifferentQuoteTest() {
        CSVReader csvReader = new CSVReader(new StringReader(""), '|', '`', 0);
        assertTrue(csvReader.isNextCharAlsoQuote("hello `world", true, 5));
    }

    @Test
    public void closeTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello world"));
        String[] first = csvReader.readNext();
        assertEquals(1, first.length);
        assertEquals("hello world", first[0]);
        csvReader.close();
        IOException exception = assertThrows(IOException.class, () -> csvReader.readNext());
        assertEquals("Stream closed", exception.getMessage());
    }

//    @Test
//    public void readNextDifferentParsersTest() throws IOException {
//        CSVReader csvReader = new CSVReader(new StringReader("hello|world\nfoo"), '|', '"', 0);
//        assertEquals(new String[] {"hello", "world"}, csvReader.readNext());
//        assertEquals(new String[] {"foo"}, csvReader.readNext());
//        assertNull(csvReader.readNext());
//    }
}
