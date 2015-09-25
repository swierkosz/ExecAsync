package com.github.swierkosz.execasync;

/*
 * This file is heavily based on org.gradle.api.tasks.AbstractExecTask.
 *
 * Copyright 2010 the original author or authors.
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

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.internal.file.DefaultFileOperations;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;
import org.gradle.process.ProcessForkOptions;
import org.gradle.process.internal.ExecHandle;
import org.gradle.process.internal.ExecHandleBuilder;
import org.gradle.process.internal.ExecHandleListener;
import org.gradle.process.internal.ExecHandleState;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public abstract class AbstractExecAsyncTask<T extends AbstractExecAsyncTask> extends ConventionTask implements ExecSpec {

    private final Class<T> taskType;
    private ExecHandleBuilder execHandleBuilder = new ExecHandleBuilder(getDefaultFileOperations().getFileResolver());
    private ExecHandle execHandle;
    private ExecResult execResult;

    public AbstractExecAsyncTask(Class<T> taskType) {
        this.taskType = taskType;
    }

    @Inject
    protected DefaultFileOperations getDefaultFileOperations() {
        throw new UnsupportedOperationException();
    }

    protected void setExecHandleBuilder(ExecHandleBuilder execHandleBuilder) {
        this.execHandleBuilder = execHandleBuilder;
    }

    /**
     * Starts process, blocking until the process has started.
     */
    @TaskAction
    protected void exec() {
        execHandle = execHandleBuilder.build();
        execHandle.addListener(new ExecHandleListener() {
            @Override
            public void executionStarted(ExecHandle execHandle) {

            }

            @Override
            public void executionFinished(ExecHandle execHandle, ExecResult execResult) {
                setExecResult(execResult);
            }
        });
        execHandle.start();
    }

    /**
     * {@inheritDoc}
     */
    public T commandLine(Object... arguments) {
        execHandleBuilder.commandLine(arguments);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public T commandLine(Iterable<?> args) {
        execHandleBuilder.commandLine(args);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public T args(Object... args) {
        execHandleBuilder.args(args);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public T args(Iterable<?> args) {
        execHandleBuilder.args(args);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public T setArgs(Iterable<?> arguments) {
        execHandleBuilder.setArgs(arguments);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getArgs() {
        return execHandleBuilder.getArgs();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getCommandLine() {
        return execHandleBuilder.getCommandLine();
    }

    /**
     * {@inheritDoc}
     */
    public void setCommandLine(Iterable<?> args) {
        execHandleBuilder.setCommandLine(args);
    }

    /**
     * {@inheritDoc}
     */
    public void setCommandLine(Object... args) {
        execHandleBuilder.setCommandLine(args);
    }

    /**
     * {@inheritDoc}
     */
    public String getExecutable() {
        return execHandleBuilder.getExecutable();
    }

    /**
     * {@inheritDoc}
     */
    public void setExecutable(Object executable) {
        execHandleBuilder.setExecutable(executable);
    }

    /**
     * {@inheritDoc}
     */
    public T executable(Object executable) {
        execHandleBuilder.executable(executable);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public File getWorkingDir() {
        return execHandleBuilder.getWorkingDir();
    }

    /**
     * {@inheritDoc}
     */
    public void setWorkingDir(Object dir) {
        execHandleBuilder.setWorkingDir(dir);
    }

    /**
     * {@inheritDoc}
     */
    public T workingDir(Object dir) {
        execHandleBuilder.workingDir(dir);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getEnvironment() {
        return execHandleBuilder.getEnvironment();
    }

    /**
     * {@inheritDoc}
     */
    public void setEnvironment(Map<String, ?> environmentVariables) {
        execHandleBuilder.setEnvironment(environmentVariables);
    }

    /**
     * {@inheritDoc}
     */
    public T environment(String name, Object value) {
        execHandleBuilder.environment(name, value);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public T environment(Map<String, ?> environmentVariables) {
        execHandleBuilder.environment(environmentVariables);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public T copyTo(ProcessForkOptions target) {
        execHandleBuilder.copyTo(target);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public T setStandardInput(InputStream inputStream) {
        execHandleBuilder.setStandardInput(inputStream);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getStandardInput() {
        return execHandleBuilder.getStandardInput();
    }

    /**
     * {@inheritDoc}
     */
    public T setStandardOutput(OutputStream outputStream) {
        execHandleBuilder.setStandardOutput(outputStream);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getStandardOutput() {
        return execHandleBuilder.getStandardOutput();
    }

    /**
     * {@inheritDoc}
     */
    public T setErrorOutput(OutputStream outputStream) {
        execHandleBuilder.setErrorOutput(outputStream);
        return taskType.cast(this);
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getErrorOutput() {
        return execHandleBuilder.getErrorOutput();
    }

    /**
     * Not supported.
     */
    public T setIgnoreExitValue(boolean ignoreExitValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     */
    public boolean isIgnoreExitValue() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the result for the command run by this task. Returns {@code null} if this task has not been executed
     * or finished yet.
     *
     * @return The result. Returns {@code null} if this task has not been executed or finished yet.
     */
    public ExecResult getExecResult() {
        return execResult;
    }

    protected void setExecResult(ExecResult execResult) {
        this.execResult = execResult;
    }

    /**
     * Waits for the process to finish.
     *
     * @return The result.
     */
    public ExecResult waitForFinish() {
        if (execHandle == null) {
            throw new IllegalStateException("The task has not been executed yet");
        }

        return execHandle.waitForFinish();
    }

    /**
     * Terminates the process.
     */
    public void terminate() {
        if (execHandle == null) {
            throw new IllegalStateException("The task has not been executed yet");
        }

        ExecHandleState state = execHandle.getState();
        if (state == ExecHandleState.STARTED || state == ExecHandleState.DETACHED) {
            execHandle.abort();
        }
    }

    /**
     * Returns information whether the process is still running.
     *
     * @return True if process is running.
     */
    public boolean isRunning() {
        return execHandle != null && execHandle.getState() == ExecHandleState.STARTED;
    }
}

