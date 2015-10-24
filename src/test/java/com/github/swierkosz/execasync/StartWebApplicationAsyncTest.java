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

import com.github.swierkosz.execasync.polling.Poller;
import com.github.swierkosz.execasync.web.WebApplicationChecker;
import com.github.swierkosz.execasync.web.WebApplicationIsAlreadyAvailableException;
import org.gradle.process.internal.ExecHandle;
import org.gradle.process.internal.ExecHandleBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class StartWebApplicationAsyncTest extends AbstractTaskTest {

    @Mock
    private WebApplicationChecker checker;

    @Mock
    private ExecHandleBuilder execHandleBuilder;

    @Mock
    private ExecHandle execHandle;

    @Mock
    private Poller poller;

    private StartWebApplicationAsync task;

    @Before
    public void setUp() {
        task = createTask(StartWebApplicationAsync.class);
        task.setChecker(checker);
        task.setExecHandleBuilder(execHandleBuilder);
        task.setPoller(poller);

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

        // When
        task.exec();

        // Then
        verify(execHandleBuilder).build();
        verify(poller).awaitAtMost(anyInt(), any(TimeUnit.class), any(Callable.class));
    }

    @Test
    public void shouldCheckIfApplicationIsReady() {
        // Given
        String url = "http://test.test";
        int expectedResponseCode = 201;

        task.setApplicationUrl(url);
        task.setExpectedResponseCode(expectedResponseCode);

        given(checker.isUrlAccessible(url, expectedResponseCode)).willReturn(true);

        // When
        boolean result = task.isApplicationReady();

        // Then
        assertThat(result).isTrue();
    }

}