/* Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS.
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.google.ie.common.search.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

/**
 * Custom analyzer class for analyzing query for objectionable content used by
 * compass in search. This analyzer removes stop words from query,take care of
 * spaces,make search case in-sensitive.
 * 
 * @author gmaurya
 * 
 */
public class IdeaExchangeQueryAnalyzer extends Analyzer {
    /** default language if nothing specified. */
    private static final String DEFAULT_LANGUAGE = "English";

    /** Inner class for accessing previous saved stream. */
    private static final class SavedStreams {
        StandardTokenizer tokenStream;
        TokenStream filteredTokenStream;
    }

    /**
     * Set of stop words
     */
    private Set<String> stopSet;
    /**
     * Stop words array
     */
    public static final String STOP_WORDS[];
    /**
     * Default max length of token.
     */
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    /**
     * Max length of token.
     */
    private int maxTokenLength;

    /**
     * Constructor
     */
    public IdeaExchangeQueryAnalyzer() {
        this(STOP_WORDS);
    }

    /**
     * Constructor
     * 
     * @param stopWords Set
     */
    public IdeaExchangeQueryAnalyzer(Set<String> stopWords) {
        maxTokenLength = 255;
        stopSet = stopWords;
    }

    /**
     * Constructor
     * 
     * @param stopWords String[]
     */
    @SuppressWarnings("unchecked")
    public IdeaExchangeQueryAnalyzer(String stopWords[]) {
        maxTokenLength = 255;
        stopSet = StopFilter.makeStopSet(stopWords);
    }

    /**
     * Constructor
     * 
     * @param stopwords File
     * @throws IOException IOException
     */
    @SuppressWarnings("unchecked")
    public IdeaExchangeQueryAnalyzer(File stopwords) throws IOException {
        maxTokenLength = 255;
        stopSet = WordlistLoader.getWordSet(stopwords);
    }

    /**
     * Constructor
     * 
     * @param stopwords Reader
     * @throws IOException IOException
     */
    @SuppressWarnings("unchecked")
    public IdeaExchangeQueryAnalyzer(Reader stopwords) throws IOException {
        maxTokenLength = 255;
        stopSet = WordlistLoader.getWordSet(stopwords);
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        StandardTokenizer tokenStream = new StandardTokenizer(reader, false);
        tokenStream.setMaxTokenLength(maxTokenLength);
        TokenStream result = new StandardFilter(tokenStream);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopSet);
        fieldName = DEFAULT_LANGUAGE;
        result = new SnowballFilter(result, fieldName);
        return result;
    }

    /**
     * Setter method for setting max token length
     * 
     * @param length int
     */
    public void setMaxTokenLength(int length) {
        maxTokenLength = length;
    }

    /**
     * Getter for maxTokenLength.
     * 
     * @return int
     */
    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    @Override
    public TokenStream reusableTokenStream(String fieldName, Reader reader)
                    throws IOException {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null) {
            streams = new SavedStreams();
            setPreviousTokenStream(streams);
            streams.tokenStream = new StandardTokenizer(reader);
            streams.filteredTokenStream = new StandardFilter(
                            streams.tokenStream);
            streams.filteredTokenStream = new LowerCaseFilter(
                            streams.filteredTokenStream);
            streams.filteredTokenStream = new StopFilter(
                            streams.filteredTokenStream, stopSet);
            fieldName = DEFAULT_LANGUAGE;
            streams.filteredTokenStream = new
                            SnowballFilter(streams.filteredTokenStream, fieldName);
        } else {
            streams.tokenStream.reset(reader);
        }
        streams.tokenStream.setMaxTokenLength(maxTokenLength);
        return streams.filteredTokenStream;
    }

    static {
        /* static block for initializing stop word with English Stop Words */
        STOP_WORDS = StopAnalyzer.ENGLISH_STOP_WORDS;
    }
}

