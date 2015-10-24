# ExecAsync

ExecAsync is a simple Gradle plugin for starting processes asynchronously. See the source code for more details.

## Including plugin in your project
Add to your build.gradle file with the following snippet
```
buildscript {
    dependencies {
        classpath "com.github.swierkosz:ExecAsync:1.2.0"
    }
}
```
## Available tasks
There are three tasks available:
* StartApplicationAsync - a task for executing a process in the background
* StartWebApplicationAsync - an extension to StartApplicationAsync that waits for URL to become available
* StartConsoleApplicationAsync - an extension to StartApplicationAsync that waits for the specified pattern to match against console output

### StartApplicationAsync
This task executes a process in the background. Parameters for starting a process are the same as for Gradle's [Exec].

```
task(startApp, type: com.github.swierkosz.execasync.StartApplicationAsync) {
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
* `expectedResponseCode` - an expected HTTP status code that will identify the application as ready; 200 is the default
* `timeout` - a number of seconds to wait for process to start, an exception will be thrown unless process started; 300 is the default
* `failIfAlreadyRunning` - indicates whether the task should fail if process is already running; true is the default

```
task(startAppForTesting, type: com.github.swierkosz.execasync.StartWebApplicationAsync) {
    commandLine "my-web-server", "--port=1234"
    applicationUrl "http://localhost:1234"
    expectedResponseCode 302
    timeout 60
}
```

### StartConsoleApplicationAsync
This task is an extension to StartApplicationAsync - waits for the specified pattern to match against console output

Additional parameters:
* `pattern` - a regular expression to be used against console output to check whether application is ready or not
* `timeout` - a number of seconds to wait for process to start, an exception will be thrown unless process started; 300 is the default

```
task(startAppForTesting, type: com.github.swierkosz.execasync.StartConsoleApplicationAsync) {
    commandLine "my-web-server", "--port=1234"
    pattern "Application has started in \\d+ seconds"
    timeout 60
}
```

## Things to be aware of
* Gradle will automatically terminate the process when the build finishes.
* If the Gradle process is not gracefully terminated, child processes won't be terminated.
* If a child process spawns its own child processes, they won't be terminated by Gradle.

   [Exec]: <https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/Exec.html>
