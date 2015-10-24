package com.github.swierkosz.execasync.console;

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class TeeOutputStreamTest {

    @Mock
    private OutputStream left;

    @Mock
    private OutputStream right;

    private TeeOutputStream teeOutputStream;

    @Before
    public void setUp() {
        teeOutputStream = new TeeOutputStream(left, right);
    }

    @Test
    public void shouldDelegateFlush() throws IOException {
        // When
        teeOutputStream.flush();

        // Then
        verify(left).flush();
        verify(right).flush();
    }

    @Test
    public void shouldDelegateClose() throws IOException {
        // When
        teeOutputStream.close();

        // Then
        verify(left).close();
        verify(right).close();
    }

    @Test
    public void shouldDelegateWriteOfInteger() throws IOException {
        // Given
        int input = 1234;

        // When
        teeOutputStream.write(input);

        // Then
        verify(left).write(input);
        verify(right).write(input);
    }

    @Test
    public void shouldDelegateWriteOfByteArray() throws IOException {
        // Given
        byte[] byteArray = new byte[]{1, 2, 3};

        // When
        teeOutputStream.write(byteArray);

        // Then
        verify(left).write(byteArray);
        verify(right).write(byteArray);
    }

    @Test
    public void shouldDelegateWriteOfChunkOfByteArray() throws IOException {
        // Given
        byte[] byteArray = new byte[]{1, 2, 3};
        int off = 1;
        int len = 2;

        // When
        teeOutputStream.write(byteArray, off, len);

        // Then
        verify(left).write(byteArray, off, len);
        verify(right).write(byteArray, off, len);
    }

    @Test
    public void shouldDetachRightStream() throws IOException {
        // Given
        int input = 1234;
        teeOutputStream.detachRight();

        // When
        teeOutputStream.write(input);
        // Then
        verify(left).write(input);
        verifyZeroInteractions(right);
    }

}