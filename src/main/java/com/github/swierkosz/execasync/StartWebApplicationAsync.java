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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartWebApplicationAsync extends StartApplicationAsync {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartWebApplicationAsync.class);

    private String applicationUrl;
    private int timeout = 300;
    private int expectedResponseCode = 200;
    private boolean failIfAlreadyRunning = true;
    private WebApplicationChecker checker = new WebApplicationChecker();

    /**
     * Returns the application url used for checking whether application has started.
     *
     * @return url as String
     */
    public String getApplicationUrl() {
        return applicationUrl;
    }

    /**
     * Sets the application url used for checking whether application has started.
     *
     * @param applicationUrl absolute url
     */
    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
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
     * Returns the expected response code which will identify the web application as ready.
     *
     * @return expected HTTP status code
     */
    public int getExpectedResponseCode() {
        return expectedResponseCode;
    }

    /**
     * Sets the expected response code which will identify the web application as ready.
     *
     * @param expectedResponseCode expected HTTP status code
     */
    public void setExpectedResponseCode(int expectedResponseCode) {
        this.expectedResponseCode = expectedResponseCode;
    }

    /**
     * Returns true when task should fail when application is already running.
     *
     * @return true if task will fail for already running application
     */
    public boolean isFailIfAlreadyRunning() {
        return failIfAlreadyRunning;
    }

    /**
     * Controls whether task should fail when application is already running.
     *
     * @param failIfAlreadyRunning set to true if task should fail for already running application
     */
    public void setFailIfAlreadyRunning(boolean failIfAlreadyRunning) {
        this.failIfAlreadyRunning = failIfAlreadyRunning;
    }

    @Override
    protected void exec() {
        if (checker.isUrlAccessible(applicationUrl, expectedResponseCode)) {
            if (failIfAlreadyRunning) {
                throw new WebApplicationIsAlreadyAvailableException(applicationUrl);
            } else {
                LOGGER.info("Application url " + applicationUrl + " is already accessible, the application won't be started");
                return;
            }
        }

        LOGGER.info("Application url " + applicationUrl + " is not already accessible, starting the application...");
        super.exec();

        LOGGER.info("Waiting for the application url " + applicationUrl + " to become available...");
        if (checker.waitForUrlToBeAccessible(applicationUrl, expectedResponseCode, timeout)) {
            LOGGER.info("The application url " + applicationUrl + " is now accessible");
        } else {
            throw new WebApplicationTimeoutException(applicationUrl);
        }
    }

    protected void setChecker(WebApplicationChecker checker) {
        this.checker = checker;
    }
}
