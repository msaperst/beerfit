package com.fatmax.beerfit.utilities;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CSVReaderUnitTest {

    @Test
    public void readNextEmptyTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertNull(csvReader.readNext());
    }

    @Test
    public void readNextTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello world"));
        assertEquals(new String[]{"hello world"}, csvReader.readNext());
        assertNull(csvReader.readNext());
    }

    @Test
    public void readNextTwoLinesTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello world\nfoo"));
        assertEquals(new String[]{"hello world"}, csvReader.readNext());
        assertEquals(new String[]{"foo"}, csvReader.readNext());
        assertNull(csvReader.readNext());
    }

    @Test
    public void readNextTwoColsTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello,world\nfoo"));
        assertEquals(new String[]{"hello", "world"}, csvReader.readNext());
        assertEquals(new String[]{"foo"}, csvReader.readNext());
        assertNull(csvReader.readNext());
    }

    @Test
    public void readNextLineSkipped() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello|world\nfoo"), '|', '"', 1);
        assertEquals(new String[]{"foo"}, csvReader.readNext());
        assertNull(csvReader.readNext());
    }

    @Test
    public void readNextLineInQuotesSkipped() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello\"|world\nfoo\""), '|', '"', 0);
        assertEquals(new String[]{"hello|world\nfoo"}, csvReader.readNext());
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
        assertEquals(new String[]{"foo"}, csvReader.parseLine("foo"));
    }

    @Test
    public void parseLineWithQuoteTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertEquals(new String[]{"hello \"world\n"}, csvReader.parseLine("hello \"world"));
    }

    @Test
    public void parseLineWithQuotesTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader(""));
        assertEquals(new String[]{"hello \"world"}, csvReader.parseLine("hello \"world\""));
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

    @Test(expected = IOException.class)
    public void closeTest() throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader("hello world"));
        assertEquals(new String[]{"hello world"}, csvReader.readNext());
        csvReader.close();
        csvReader.readNext();
    }


//    @Test
//    public void readNextDifferentParsersTest() throws IOException {
//        CSVReader csvReader = new CSVReader(new StringReader("hello|world\nfoo"), '|', '"', 0);
//        assertEquals(new String[] {"hello", "world"}, csvReader.readNext());
//        assertEquals(new String[] {"foo"}, csvReader.readNext());
//        assertNull(csvReader.readNext());
//    }
}
