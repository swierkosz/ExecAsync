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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class AbstractPollingExecAsyncTask<T extends AbstractPollingExecAsyncTask> extends AbstractExecAsyncTask<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPollingExecAsyncTask.class);

    private int timeout = 300;
    private Poller poller = new Poller();

    public AbstractPollingExecAsyncTask(Class<T> taskType) {
        super(taskType);
    }

    /**
     * Returns the timeout for application startup.
     *
     * @return duration in seconds
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout for application startup.
     *
     * @param timeout duration in seconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Starts process, blocking until the process has started.
     */
    @Override
    protected void exec() {
        super.exec();

        LOGGER.info("Waiting for the application to become available...");
        poller.awaitAtMost(timeout, SECONDS, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (!isRunning()) {
                    throw new ApplicationTerminatedException();
                }

                return isApplicationReady();
            }
        });
        LOGGER.info("The application url is now available");
    }

    protected abstract boolean isApplicationReady();

    protected void setPoller(Poller poller) {
        this.poller = poller;
    }
}
