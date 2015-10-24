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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

public class PatternMatchingOutputStream extends OutputStream {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(8096);
    private final Pattern pattern;
    private boolean matched = false;

    public PatternMatchingOutputStream(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public void write(int b) {
        if (matched) {
            return;
        }
        buffer.write(b);
        if (b == 10) {
            String line = buffer.toString();
            buffer.reset();
            if (pattern.matcher(line).find()) {
                matched = true;
            }
        }
    }

    public boolean isMatched() {
        return matched;
    }
}
