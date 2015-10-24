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

import com.github.swierkosz.execasync.web.WebApplicationChecker;
import com.github.swierkosz.execasync.web.WebApplicationIsAlreadyAvailableException;
import com.github.swierkosz.execasync.web.WebApplicationTimeoutException;
import org.gradle.process.internal.ExecHandle;
import org.gradle.process.internal.ExecHandleBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class StartWebApplicationAsyncTest extends AbstractTaskTest {

    @Mock
    private WebApplicationChecker checker;

    @Mock
    private ExecHandleBuilder execHandleBuilder;

    @Mock
    private ExecHandle execHandle;

    private StartWebApplicationAsync task;

    @Before
    public void setUp() {
        task = createTask(StartWebApplicationAsync.class);
        task.setChecker(checker);
        task.setExecHandleBuilder(execHandleBuilder);

        given(execHandleBuilder.build()).willReturn(execHandle);
    }

    @Test
    public void shouldSetAndReturnApplicationUrl() {
        // Given
        String url = "http://test.test";

        // When
        task.setApplicationUrl(url);
        String result = task.getApplicationUrl();

        // Then
        assertThat(result).isEqualTo(url);
    }

    @Test
    public void shouldSetAndReturnTimeout() {
        // Given
        int timeout = 123456;

        // When
        task.setTimeout(timeout);
        int result = task.getTimeout();

        // Then
        assertThat(result).isEqualTo(timeout);
    }

    @Test
    public void shouldReturnDefaultTimeout() {
        // When
        int result = task.getTimeout();

        // Then
        assertThat(result).isEqualTo(300);
    }

    @Test
    public void shouldSetAndReturnExpectedResponseCode() {
        // Given
        int expectedResponseCode = 401;

        // When
        task.setExpectedResponseCode(expectedResponseCode);
        int result = task.getExpectedResponseCode();

        // Then
        assertThat(result).isEqualTo(expectedResponseCode);
    }

    @Test
    public void shouldReturnDefaultExpectedResponseCode() {
        // When
        int result = task.getExpectedResponseCode();

        // Then
        assertThat(result).isEqualTo(200);
    }

    @Test
    public void shouldSetAndReturnFailIfAlreadyRunning() {
        // Given
        boolean failIfAlreadyRunning = false;

        // When
        task.setFailIfAlreadyRunning(failIfAlreadyRunning);
        boolean result = task.isFailIfAlreadyRunning();

        // Then
        assertThat(result).isEqualTo(failIfAlreadyRunning);
    }

    @Test(expected = WebApplicationIsAlreadyAvailableException.class)
    public void shouldThrowErrorIfApplicationIsAlreadyRunning() {
        // Given
        String url = "http://test.test";
        int expectedResponseCode = 201;

        task.setApplicationUrl(url);
        task.setExpectedResponseCode(expectedResponseCode);
        task.setFailIfAlreadyRunning(true);

        given(checker.isUrlAccessible(url, expectedResponseCode)).willReturn(true);

        // When
        task.exec();
    }

    @Test
    public void shouldNotStartProcessIfApplicationIsAlreadyRunningAndShouldNotFail() {
        // Given
        String url = "http://test.test";
        int expectedResponseCode = 201;

        task.setApplicationUrl(url);
        task.setExpectedResponseCode(expectedResponseCode);
        task.setFailIfAlreadyRunning(false);

        given(checker.isUrlAccessible(url, expectedResponseCode)).willReturn(true);

        // When
        task.exec();

        // Then
        verifyZeroInteractions(execHandleBuilder);
    }

    @Test
    public void shouldStartProcessIfApplicationIsNotRunningAndShouldWait() {
        // Given
        String url = "http://test.test";
        int expectedResponseCode = 201;
        int timeout = 123;

        task.setApplicationUrl(url);
        task.setExpectedResponseCode(expectedResponseCode);
        task.setTimeout(timeout);

        given(checker.waitForUrlToBeAccessible(url, expectedResponseCode, timeout)).willReturn(true);

        // When
        task.exec();

        // Then
        InOrder inOrder = inOrder(execHandleBuilder, checker);
        inOrder.verify(execHandleBuilder).build();
        inOrder.verify(checker).waitForUrlToBeAccessible(url, expectedResponseCode, timeout);
    }

    @Test(expected = WebApplicationTimeoutException.class)
    public void shouldThrowExceptionIfApplicationIsNotAvailable() {
        // Given
        String url = "http://test.test";
        task.setApplicationUrl(url);

        // When
        task.exec();
    }
}