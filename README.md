# ExecAsync

ExecAsync is a simple Gradle plugin for starting processes asynchronously. See the source code for more details.

## Including plugin in your project
Add to your build.gradle file with the following snippet
```
buildscript {
    dependencies {
        classpath group: 'oss.swierkosz:ExecAsync'
    }
}
```
## Available tasks
There are two tasks available:
* StartApplicationAsync - a task for executing a process in the background
* StartWebApplicationAsync - an extension to StartApplicationAsync that waits for URL to become available

### StartApplicationAsync
This task executes a process in the background. Parameters for starting a process are the same as for Gradle's [Exec].

```
task(startApp, type: oss.swierkosz.execasync.StartWebApplicationAsync) {
    commandLine "my-web-server", "--port=1234"
}
```

This task provides additional methods:
* `waitForFinish` - waits for the process to finish
* `terminate` - terminates the child process (note that it does not terminate its children)
* `isRunning` - returns true if the process is still running

### StartWebApplicationAsync
This task is an extension to StartApplicationAsync - waits for the specified URL to become available.

Additional parameters:
* `applicationUrl` - a URL of the web application that is to be started
* `timeout` - a number of seconds to wait for process to start, an exception will be thrown unless process started; 300 is the default
* `failIfAlreadyRunning` - indicates whether the task should fail if process is already running; true is the default

```
task(startAppForTesting, type: oss.swierkosz.execasync.StartWebApplicationAsync) {
    commandLine "my-web-server", "--port=1234"
    applicationUrl "http://localhost:1234"
    timeout 60
}
```

## Things to be aware of
* Gradle will automatically terminate the process when the build finishes.
* If the Gradle process is not gracefully terminated, child processes won't be terminated.
* If a child process spawns its own child processes, they won't be terminated by Gradle.

   [Exec]: <https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/Exec.html>
