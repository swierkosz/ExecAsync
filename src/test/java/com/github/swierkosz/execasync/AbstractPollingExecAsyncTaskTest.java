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

import com.github.swierkosz.execasync.polling.ApplicationTerminatedException;
import com.github.swierkosz.execasync.polling.Poller;
import org.gradle.process.internal.ExecHandle;
import org.gradle.process.internal.ExecHandleBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.process.internal.ExecHandleState.ABORTED;
import static org.gradle.process.internal.ExecHandleState.STARTED;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AbstractPollingExecAsyncTaskTest extends AbstractTaskTest {

    @Mock
    private ExecHandleBuilder execHandleBuilder;

    @Mock
    private ExecHandle execHandle;

    @Mock
    private Poller poller;

    private TestableAbstractPollingExecAsyncTask task;

    @Before
    public void setUp() {
        task = createTask(TestableAbstractPollingExecAsyncTask.class);

        task.setExecHandleBuilder(execHandleBuilder);
        given(execHandleBuilder.build()).willReturn(execHandle);
        given(execHandle.getState()).willReturn(STARTED);

        task.setPoller(poller);
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
    public void shouldWaitForApplicationToStart() {
        // Given
        int timeout = 1234;
        task.setTimeout(timeout);

        // When
        task.exec();

        // Then
        verify(poller).awaitAtMost(eq(timeout), eq(TimeUnit.SECONDS), any(Callable.class));
    }

    @Test(expected = ApplicationTerminatedException.class)
    public void shouldThrowExceptionWhenApplicationHasTerminated() throws Exception {
        // Given
        given(execHandle.getState()).willReturn(ABORTED);
        task.exec();
        ArgumentCaptor<Callable> callableArgumentCaptor = ArgumentCaptor.forClass(Callable.class);
        verify(poller).awaitAtMost(anyInt(), any(TimeUnit.class), callableArgumentCaptor.capture());
        Callable callable = callableArgumentCaptor.getValue();

        // When
        callable.call();

        // Then exception is thrown
    }

    @Test
    public void shouldReturnFalseWhenApplicationIsNotReady() throws Exception {
        // Given
        task.exec();
        ArgumentCaptor<Callable> callableArgumentCaptor = ArgumentCaptor.forClass(Callable.class);
        verify(poller).awaitAtMost(anyInt(), any(TimeUnit.class), callableArgumentCaptor.capture());
        Callable<Boolean> callable = (Callable<Boolean>) callableArgumentCaptor.getValue();

        // When
        Boolean result = callable.call();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnTrueWhenApplicationIsReady() throws Exception {
        // Given
        task.ready = true;
        task.exec();
        ArgumentCaptor<Callable> callableArgumentCaptor = ArgumentCaptor.forClass(Callable.class);
        verify(poller).awaitAtMost(anyInt(), any(TimeUnit.class), callableArgumentCaptor.capture());
        Callable<Boolean> callable = (Callable<Boolean>) callableArgumentCaptor.getValue();

        // When
        Boolean result = callable.call();

        // Then
        assertThat(result).isTrue();
    }

    public static class TestableAbstractPollingExecAsyncTask extends AbstractPollingExecAsyncTask<TestableAbstractPollingExecAsyncTask> {

        private boolean ready = false;

        public TestableAbstractPollingExecAsyncTask() {
            super(TestableAbstractPollingExecAsyncTask.class);
        }

        @Override
        protected boolean isApplicationReady() {
            return ready;
        }
    }
}