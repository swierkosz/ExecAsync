package oss.swierkosz.execasync;

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

import org.gradle.api.Task;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.internal.project.AbstractProject;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.internal.project.taskfactory.ITaskFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.util.GUtil;

import java.io.File;

public abstract class AbstractTaskTest {

    private final AbstractProject project;

    public AbstractTaskTest() {
        project = (DefaultProject) ProjectBuilder.builder()
                .withProjectDir(new File("/tmp"))
                .build();
    }

    protected <T extends AbstractTask> T createTask(Class<T> type) {
        Task task = project
                .getServices()
                .get(ITaskFactory.class)
                .createTask(GUtil.map(Task.TASK_TYPE, type, Task.TASK_NAME, "testTask"));
        return type.cast(task);
    }
}
