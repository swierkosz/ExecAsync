package com.github.swierkosz.execasync.polling;

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

import com.jayway.awaitility.core.ConditionTimeoutException;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;

public class Poller {

    public void awaitAtMost(int timeout, TimeUnit unit, Callable<Boolean> callable) {
        try {
            await()
                    .atMost(timeout, unit)
                    .until(callable);
        } catch (ConditionTimeoutException ignored) {
            throw new ApplicationTimeoutException();
        }
    }
}
