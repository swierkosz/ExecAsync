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
import com.github.swierkosz.execasync.polling.Poller;
import org.gradle.process.internal.ExecHandle;
import org.gradle.process.internal.ExecHandleBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.OutputStream;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class StartConsoleApplicationAsyncTest extends AbstractTaskTest {

    @Mock
    private ExecHandleBuilder execHandleBuilder;

    @Mock
    private Poller poller;

    @Mock
    private StreamFactory streamFactory;

    private StartConsoleApplicationAsync task;

    @Before
    public void setUp() {
        task = createTask(StartConsoleApplicationAsync.class);
        task.setExecHandleBuilder(execHandleBuilder);
        task.setPoller(poller);
        task.setStreamFactory(streamFactory);

        given(execHandleBuilder.build()).willReturn(mock(ExecHandle.class));
    }

    @Test
    public void shouldSetPatternStringAndReturnPattern() {
        // Given
        String patternString = "test pattern";

        // When
        task.setPattern(patternString);
        Pattern result = task.getPattern();

        // Then
        assertThat(result.toString()).isEqualTo(patternString);
    }

    @Test
    public void shouldSetAndReturnStandardOutput() {
        // Given
        OutputStream outputStream = mock(OutputStream.class);

        // When
        task.setStandardOutput(outputStream);
        OutputStream result = task.getStandardOutput();

        // Then
        assertThat(result).isEqualTo(outputStream);
    }

    @Test
    public void shouldSetAndReturnErrorOutput() {
        // Given
        OutputStream outputStream = mock(OutputStream.class);

        // When
        task.setErrorOutput(outputStream);
        OutputStream result = task.getErrorOutput();

        // Then
        assertThat(result).isEqualTo(outputStream);
    }

    @Test
    public void shouldStartProcessIfApplicationIsNotRunningAndShouldWait() {
        // Given
        Pattern pattern = Pattern.compile("1234");
        task.setPattern(pattern);

        PatternMatchingOutputStream errorOutputMatcher = mock(PatternMatchingOutputStream.class);
        PatternMatchingOutputStream standardOutputMatcher = mock(PatternMatchingOutputStream.class);
        given(streamFactory.createPatternMatchingOutputStream(pattern))
                .willReturn(errorOutputMatcher, standardOutputMatcher);

        OutputStream errorOutput = mock(OutputStream.class);
        task.setErrorOutput(errorOutput);

        TeeOutputStream teeErrorOutput = mock(TeeOutputStream.class);
        given(streamFactory.createTeeOutputStream(errorOutput, errorOutputMatcher))
                .willReturn(teeErrorOutput);

        OutputStream standardOutput = mock(OutputStream.class);
        task.setStandardOutput(standardOutput);

        TeeOutputStream teeStandardOutput = mock(TeeOutputStream.class);
        given(streamFactory.createTeeOutputStream(standardOutput, standardOutputMatcher))
                .willReturn(teeStandardOutput);

        // When
        task.exec();

        // Then
        InOrder inOrder = inOrder(streamFactory, execHandleBuilder, teeErrorOutput, teeStandardOutput);
        inOrder.verify(streamFactory).createTeeOutputStream(errorOutput, errorOutputMatcher);
        inOrder.verify(execHandleBuilder).setErrorOutput(teeErrorOutput);
        inOrder.verify(streamFactory).createTeeOutputStream(standardOutput, standardOutputMatcher);
        inOrder.verify(execHandleBuilder).setStandardOutput(teeStandardOutput);
        inOrder.verify(execHandleBuilder).build();
        inOrder.verify(teeErrorOutput).detachRight();
        inOrder.verify(teeStandardOutput).detachRight();

        assertThat(task.getErrorOutput()).isEqualTo(errorOutput);
        assertThat(task.getStandardOutput()).isEqualTo(standardOutput);

        // Given
        given(standardOutputMatcher.isMatched()).willReturn(true);
        given(errorOutputMatcher.isMatched()).willReturn(false);

        // Then
        assertThat(task.isApplicationReady()).isTrue();

        // Given
        given(standardOutputMatcher.isMatched()).willReturn(false);
        given(errorOutputMatcher.isMatched()).willReturn(true);

        // Then
        assertThat(task.isApplicationReady()).isTrue();

        // Given
        given(standardOutputMatcher.isMatched()).willReturn(false);
        given(errorOutputMatcher.isMatched()).willReturn(false);

        // Then
        assertThat(task.isApplicationReady()).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfPatternIsNotDefined() {
        // When
        task.exec();
    }
}