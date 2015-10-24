package com.github.swierkosz.execasync.web;

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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

public class WebApplicationChecker {

    private static final int ATTEMPT_TIMEOUT = 5000;

    public boolean isUrlAccessible(String url, int expectedResponseCode) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(ATTEMPT_TIMEOUT);
            connection.setReadTimeout(ATTEMPT_TIMEOUT);
            connection.setRequestMethod("GET");
            return expectedResponseCode == connection.getResponseCode();
        } catch (IOException ignored) {
            return false;
        }
    }

    public boolean waitForUrlToBeAccessible(final String url, final int expectedResponseCode, int timeout) {
        try {
            await().atMost(timeout, SECONDS).until(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return isUrlAccessible(url, expectedResponseCode);
                }
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
