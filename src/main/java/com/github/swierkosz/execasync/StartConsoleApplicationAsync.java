package com.github.swierkosz.execasync;

/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.swierkosz.execasync.console.PatternMatchingOutputStream;
import com.github.swierkosz.execasync.console.StreamFactory;
import com.github.swierkosz.execasync.console.TeeOutputStream;
import org.gradle.process.internal.streams.SafeStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.regex.Pattern;

public class StartConsoleApplicationAsync extends AbstractPollingExecAsyncTask<StartConsoleApplicationAsync> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartConsoleApplicationAsync.class);
    private Pattern pattern = null;
    private StreamFactory streamFactory = new StreamFactory();
    private OutputStream standardOutput = SafeStreams.systemOut();
    private PatternMatchingOutputStream standardOutputMatcher;
    private OutputStream errorOutput = SafeStreams.systemErr();
    private PatternMatchingOutputStream errorOutputMatcher;

    public StartConsoleApplicationAsync() {
        super(StartConsoleApplicationAsync.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StartConsoleApplicationAsync setStandardOutput(OutputStream outputStream) {
        this.standardOutput = outputStream;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getStandardOutput() {
        return standardOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StartConsoleApplicationAsync setErrorOutput(OutputStream outputStream) {
        this.errorOutput = outputStream;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getErrorOutput() {
        return errorOutput;
    }

    /**
     * Returns the pattern to be matched against application logs.
     *
     * @return compiled Pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Sets the pattern to be matched against application logs to check if application is ready.
     *
     * @param pattern compiled Pattern
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Sets the pattern to be matched against application logs to check if application is ready.
     *
     * @param pattern regular expression
     */
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    protected void exec() {
        if (pattern == null) {
            throw new IllegalArgumentException("Missing pattern");
        }
        errorOutputMatcher = streamFactory.createPatternMatchingOutputStream(pattern);
        TeeOutputStream teeErrorOutput = streamFactory.createTeeOutputStream(errorOutput, errorOutputMatcher);
        super.setErrorOutput(teeErrorOutput);

        standardOutputMatcher = streamFactory.createPatternMatchingOutputStream(pattern);
        TeeOutputStream teeStandardOutput = streamFactory.createTeeOutputStream(standardOutput, standardOutputMatcher);
        super.setStandardOutput(teeStandardOutput);

        LOGGER.info("Starting the application...");
        super.exec();
        teeErrorOutput.detachRight();
        teeStandardOutput.detachRight();
    }

    @Override
    protected boolean isApplicationReady() {
        return errorOutputMatcher.isMatched() || standardOutputMatcher.isMatched();
    }

    protected void setStreamFactory(StreamFactory streamFactory) {
        this.streamFactory = streamFactory;
    }
}
