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

import com.google.common.collect.ImmutableMap;
import org.gradle.process.ExecResult;
import org.gradle.process.ProcessForkOptions;
import org.gradle.process.internal.ExecHandle;
import org.gradle.process.internal.ExecHandleBuilder;
import org.gradle.process.internal.ExecHandleListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.process.internal.ExecHandleState.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StartApplicationAsyncTest extends AbstractTaskTest {

    @Mock
    private ExecHandleBuilder execHandleBuilder;

    private StartApplicationAsync task;

    @Before
    public void setUp() {
        task = createTask(StartApplicationAsync.class);
        task.setExecHandleBuilder(execHandleBuilder);
    }

    @Test
    public void shouldPassCommandLineWithArrayOfObjects() {
        // Given
        Object a = new Object();
        Object b = new Object();

        // When
        AbstractExecAsyncTask result = task.commandLine(a, b);

        // Then
        verify(execHandleBuilder).commandLine(a, b);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldPassCommandLineWithIterable() {
        // Given
        Iterable<Object> iterable = newArrayList(new Object(), new Object());

        // When
        AbstractExecAsyncTask result = task.commandLine(iterable);

        // Then
        verify(execHandleBuilder).commandLine(iterable);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldPassArgsWithArrayOfObjects() {
        // Given
        Object a = new Object();
        Object b = new Object();

        // When
        AbstractExecAsyncTask result = task.args(a, b);

        // Then
        verify(execHandleBuilder).args(a, b);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldPassArgsWithIterable() {
        // Given
        Iterable<Object> iterable = newArrayList(new Object(), new Object());

        // When
        AbstractExecAsyncTask result = task.args(iterable);

        // Then
        verify(execHandleBuilder).args(iterable);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldSetArgs() {
        // Given
        Iterable<Object> iterable = newArrayList(new Object(), new Object());

        // When
        AbstractExecAsyncTask result = task.setArgs(iterable);

        // Then
        verify(execHandleBuilder).setArgs(iterable);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldReturnArgs() {
        // Given
        List<String> args = newArrayList("a", "b");
        given(execHandleBuilder.getArgs()).willReturn(args);

        // When
        List<String> result = task.getArgs();

        // Then
        assertThat(result).isEqualTo(args);
    }

    @Test
    public void shouldReturnCommandLine() {
        // Given
        List<String> args = newArrayList("a", "b");
        given(execHandleBuilder.getCommandLine()).willReturn(args);

        // When
        List<String> result = task.getCommandLine();

        // Then
        assertThat(result).isEqualTo(args);
    }

    @Test
    public void shouldSetCommandLineWithIterable() {
        // Given
        List<String> iterable = newArrayList("a", "b");

        // When
        task.setCommandLine(iterable);

        // Then
        verify(execHandleBuilder).setCommandLine(iterable);
    }

    @Test
    public void shouldSetCommandLineWithArrayOfObjects() {
        // Given
        Object a = new Object();
        Object b = new Object();

        // When
        task.setCommandLine(a, b);

        // Then
        verify(execHandleBuilder).setCommandLine(a, b);
    }

    @Test
    public void shouldReturnExecutable() {
        // Given
        given(execHandleBuilder.getExecutable()).willReturn("abc");

        // When
        String result = task.getExecutable();

        // Then
        assertThat(result).isEqualTo("abc");
    }

    @Test
    public void shouldSetExecutable() {
        // Given
        Object executable = new Object();

        // When
        task.setExecutable(executable);

        // Then
        verify(execHandleBuilder).setExecutable(executable);
    }

    @Test
    public void shouldPassExecutable() {
        // Given
        Object executable = new Object();

        // When
        AbstractExecAsyncTask result = task.executable(executable);

        // Then
        verify(execHandleBuilder).executable(executable);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldReturnWorkingDir() {
        // Given
        File file = new File("abc");
        given(execHandleBuilder.getWorkingDir()).willReturn(file);

        // When
        File result = task.getWorkingDir();

        // Then
        assertThat(result).isEqualTo(file);
    }

    @Test
    public void shouldSetWorkingDir() {
        // Given
        Object workingDir = new Object();

        // When
        task.setWorkingDir(workingDir);

        // Then
        verify(execHandleBuilder).setWorkingDir(workingDir);
    }

    @Test
    public void shouldPassWorkingDir() {
        // Given
        Object workingDir = new Object();

        // When
        AbstractExecAsyncTask result = task.workingDir(workingDir);

        // Then
        verify(execHandleBuilder).workingDir(workingDir);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldReturnEnvironment() {
        // Given
        Map<String, Object> environment = ImmutableMap.of("key", new Object());

        given(execHandleBuilder.getEnvironment()).willReturn(environment);

        // When
        Map<String, Object> result = task.getEnvironment();

        // Then
        assertThat(result).isEqualTo(environment);
    }

    @Test
    public void shouldSetEnvironment() {
        // Given
        Map<String, Object> environment = ImmutableMap.of("key", new Object());

        // When
        task.setEnvironment(environment);

        // Then
        verify(execHandleBuilder).setEnvironment(environment);
    }

    @Test
    public void shouldPassKeyValueAsEnvironment() {
        // Given
        String key = "key";
        Object value = new Object();

        // When
        AbstractExecAsyncTask result = task.environment(key, value);

        // Then
        verify(execHandleBuilder).environment(key, value);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldPassMapAsEnvironment() {
        // Given
        Map<String, Object> environment = ImmutableMap.of("key", new Object());

        // When
        AbstractExecAsyncTask result = task.environment(environment);

        // Then
        verify(execHandleBuilder).environment(environment);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldPassCopyTo() {
        // Given
        ProcessForkOptions target = mock(ProcessForkOptions.class);

        // When
        AbstractExecAsyncTask result = task.copyTo(target);

        // Then
        verify(execHandleBuilder).copyTo(target);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldSetStandardInput() {
        // Given
        InputStream inputStream = mock(InputStream.class);

        // When
        AbstractExecAsyncTask result = task.setStandardInput(inputStream);

        // Then
        verify(execHandleBuilder).setStandardInput(inputStream);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldReturnStandardInput() {
        // Given
        InputStream inputStream = mock(InputStream.class);
        given(execHandleBuilder.getStandardInput()).willReturn(inputStream);

        // When
        InputStream result = task.getStandardInput();

        // Then
        assertThat(result).isEqualTo(inputStream);
    }

    @Test
    public void shouldSetStandardOutput() {
        // Given
        OutputStream outputStream = mock(OutputStream.class);

        // When
        AbstractExecAsyncTask result = task.setStandardOutput(outputStream);

        // Then
        verify(execHandleBuilder).setStandardOutput(outputStream);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldReturnStandardOutput() {
        // Given
        OutputStream outputStream = mock(OutputStream.class);
        given(execHandleBuilder.getStandardOutput()).willReturn(outputStream);

        // When
        OutputStream result = task.getStandardOutput();

        // Then
        assertThat(result).isEqualTo(outputStream);
    }

    @Test
    public void shouldSetErrorOutput() {
        // Given
        OutputStream outputStream = mock(OutputStream.class);

        // When
        AbstractExecAsyncTask result = task.setErrorOutput(outputStream);

        // Then
        verify(execHandleBuilder).setErrorOutput(outputStream);
        assertThat(result).isEqualTo(task);
    }

    @Test
    public void shouldReturnErrorOutput() {
        // Given
        OutputStream outputStream = mock(OutputStream.class);
        given(execHandleBuilder.getErrorOutput()).willReturn(outputStream);

        // When
        OutputStream result = task.getErrorOutput();

        // Then
        assertThat(result).isEqualTo(outputStream);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionWhenTryingToSetIgnoreExitValue() {
        // When
        task.setIgnoreExitValue(true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionWhenTryingToGetIgnoreExitValue() {
        // When
        task.isIgnoreExitValue();
    }

    @Test
    public void shouldSetAndReturnExecResult() {
        // Given
        ExecResult execResult = mock(ExecResult.class);

        // When
        task.setExecResult(execResult);
        ExecResult result = task.getExecResult();

        // Then
        assertThat(result).isEqualTo(execResult);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenWaitForFinishInvokedBeforeProcessExecution() {
        // When
        task.waitForFinish();
    }

    @Test
    public void shouldDelegateWaitForFinish() {
        // Given
        ExecHandle execHandle = mock(ExecHandle.class);
        given(execHandleBuilder.build()).willReturn(execHandle);
        ExecResult execResult = mock(ExecResult.class);
        given(execHandle.waitForFinish()).willReturn(execResult);
        task.exec();

        // When
        ExecResult result = task.waitForFinish();

        // Then
        assertThat(result).isEqualTo(execResult);
        verify(execHandle).waitForFinish();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenTerminateInvokedBeforeProcessExecution() {
        // When
        task.terminate();
    }

    @Test
    public void shouldDelegateProcessTerminationWhenProcessStarted() {
        // Given
        ExecHandle execHandle = mock(ExecHandle.class);
        given(execHandleBuilder.build()).willReturn(execHandle);
        given(execHandle.getState()).willReturn(STARTED);
        task.exec();

        // When
        task.terminate();

        // Then
        verify(execHandle).abort();
    }

    @Test
    public void shouldDelegateProcessTerminationWhenProcessDetached() {
        // Given
        ExecHandle execHandle = mock(ExecHandle.class);
        given(execHandleBuilder.build()).willReturn(execHandle);
        given(execHandle.getState()).willReturn(DETACHED);
        task.exec();

        // When
        task.terminate();

        // Then
        verify(execHandle).abort();
    }

    @Test
    public void shouldNotDelegateProcessTerminationWhenProcessIsNotStartedOrDetached() {
        // Given
        ExecHandle execHandle = mock(ExecHandle.class);
        given(execHandleBuilder.build()).willReturn(execHandle);
        given(execHandle.getState()).willReturn(INIT);
        task.exec();

        // When
        task.terminate();

        // Then
        verify(execHandle, never()).abort();
    }

    @Test
    public void shouldReturnFalseWhenProcessHasNotBeenExecuted() {
        // When
        boolean result = task.isRunning();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenProcessHasStateOtherThanStarted() {
        // Given
        ExecHandle execHandle = mock(ExecHandle.class);
        given(execHandleBuilder.build()).willReturn(execHandle);
        given(execHandle.getState()).willReturn(INIT);
        task.exec();

        // When
        boolean result = task.isRunning();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnTrueWhenProcessHasStateStarted() {
        // Given
        ExecHandle execHandle = mock(ExecHandle.class);
        given(execHandleBuilder.build()).willReturn(execHandle);
        given(execHandle.getState()).willReturn(STARTED);
        task.exec();

        // When
        boolean result = task.isRunning();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void shouldCreateExecHandleAndStartProcess() {
        // Given
        ExecHandle execHandle = mock(ExecHandle.class);
        given(execHandleBuilder.build()).willReturn(execHandle);

        // When
        task.exec();

        // Then
        verify(execHandle).start();
    }

    @Test
    public void shouldRetrieveExecResultOnceProcessHasFinished() {
        // Given
        ExecHandle execHandle = mock(ExecHandle.class);
        given(execHandleBuilder.build()).willReturn(execHandle);

        // When
        task.exec();

        // Then
        ArgumentCaptor<ExecHandleListener> listenerCaptor = ArgumentCaptor.forClass(ExecHandleListener.class);
        verify(execHandle).addListener(listenerCaptor.capture());

        // Given
        ExecHandleListener listener = listenerCaptor.getValue();
        ExecResult execResult = mock(ExecResult.class);
        listener.executionFinished(execHandle, execResult);

        // When
        ExecResult result = task.getExecResult();

        // Then
        assertThat(result).isEqualTo(execResult);

    }

}