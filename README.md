eAlvaLog
========

*eAlvaLog* is a Java logging facade and implementation inspired by various other logging implementations

Libraries
=========
- ealvalog

Facade API only. Used by library writers to defer all logging setup and handling to the library client
    
- ealvalog-core
    
Base implementation used by Facade implementation developer. Depends on ealvalog library
    
- ealvalog-android

Android only implementation used in Android applications. Depends on ealvalog and ealvalog-core.
    
- ealvalog-jdk

Facade implementation that logs to the JDK java.util.logging logger framework. Depends on ealvalog and ealvalog-core

- ealvalog-jdk-android

Contains a Handler for ealvalog-jdk client that also wants to log to the Android logger. Used with ealvalog-jdk on Android. Depends on 
ealvalog, ealvalog-core, and ealvalog-jdk 

Quick Start
===========
- Android Setup
```java
public class MyApp extends Application {
  public void onCreate() {
    AndroidLogger.setHandler(new DebugLogHandler());
    Loggers.setFactory(new AndroidLoggerFactory());
  }
}  
```  
    
- Facade Over java.util.logging Setup (example also contains Android code)
```java
public class MyApp extends Application {
  public void onCreate() {
    final JdkLoggerFactory jdkLoggerFactory = JdkLoggerFactory.instance();
    jdkLoggerFactory.reset(); // completely resets java.util.logging Loggers and Handlers
    Loggers.setFactory(jdkLoggerFactory);
    
    final JdkLogger rootLogger = jdkLoggerFactory.get("");
    if (BuildConfig.DEBUG) {
      // Set up Crashlytics, disabled for debug builds
      Fabric.with(this, new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build());
      rootLogger.addHandler(AndroidLoggerHandler.builder()
                                                .build());
      rootLogger.setLogLevel(LogLevel.ALL);
      rootLogger.setIncludeLocation(true);
    } else {
      Fabric.with(this, new CrashlyticsCore(), new Answers(), new Crashlytics());
      rootLogger.addHandler(new CrashlyticsLogHandler());
      rootLogger.setLogLevel(LogLevel.ERROR);
    }
  }
}
  
class CrashlyticsLogHandler extends BaseLoggerHandler {
  private ExtRecordFormatter formatter;

  CrashlyticsLogHandler() {
    formatter = new ExtRecordFormatter();
  }

  @Override
  public FilterResult isLoggable(@NotNull final Logger logger,
                                 @NotNull final LogLevel level,
                                 @NotNull final Marker marker,
                                 @NotNull final Throwable throwable) {
    if (level.isAtLeast(LogLevel.ERROR)) {
      return FilterResult.ACCEPT;
    } else {
      return FilterResult.DENY;
    }
  }

  @Override public void publish(final LogRecord record) {
    Crashlytics.log(Levels.toAndroidLevel(record.getLogLevel()),
                    "MyApplicationTag",
                    formatter.format(record));
    final Throwable thrown = record.getThrown();
    if (thrown != null) {
      Crashlytics.logException(thrown);
    }
  }

  @Override public void flush() {}

  @Override public void close() throws SecurityException {}
}
```  
- Logger use (canonical)
```java
public class NowPlayingActivity extends Activity  {
  private static final Logger LOG = Loggers.get();
  
  public void someMethod() {
    LOG.log(LogLevel.ERROR, "Widget %s too large, height=%d", widget, heightCentimeters); 
  }
}     
```     

Why?
----

 In developing both a general purpose library (eAlvaTag), forked from an older library, and an Android application, we used 2 
 different loggers. The Android specific logger was a very thin veneer over String.format() and android.util.log. The general purpose 
 library used java.util.logging and was very heavy: lots of string concatenation even when logging was never occurring, string-number 
 formatting when nothing was being logged, enormous number of StringBuilder instances being created - many totally unnecessary...
 
 We know of 3 very good logging frameworks, 1 Android specific and 2 general purpose. None of these provided what we wanted. The Android 
 specific lib is very nice in both API and implementation, but does not provide anything close to our requirements. The 2 general purpose 
 libraries, one widely used and the other state-of-art, are very heavy in both API and code. While tools such as Proguard help in this 
 area, we want to start with something that is light (not much code) and thin (not much API).
 
 We decided we wanted a facade over various other logging implementations that provides:
   1. Aggressively minimize object creation, especially when no logging will actually occur.
   2. Powerful string formatting and parameterized substitution, in support of #1.
   3. Keep the API relatively small. In our opinion, hundreds of convenience methods as found in other frameworks, add very little in 
   readability or ease of use, yet can creep into lower layers of the framework and bulk the code dramatically.
   4. Provide a base class and example in case a client desires some of the "convenience" methods mentioned in #3. This not only keeps 
   the library much smaller, but we've also found the number of these "convenience" methods per app/project is very small in practice.
   5. Push configuration/dependency management/dependency injection into the client, while providing base classes and configuration classes.
   6. Minimize framework layers and only add heavier lower layers as needed (async logging, logging to file, database, etc.)
   7. Support Android logger and java.util.logging initially, with others added if/when necessary. 
   
 Result
 ------
 
  1. Object creation at the log site is zero (client code aside). If a custom LoggerWrapper subclass is developed by the client, even 
  logging primitives as format arguments will create zero objects unless the logging passes level checks and filters. A LoggerWrapper 
  subclass requires minimal code and is entirely optional.
  2. eAlvaLog provides filtering using LogLevel, Throwable, and Markers at the most outer layer to short-circuit logging as quickly as 
  possible. If the statement will not ultimately be logged, objects are not created. 
  3. Object creation inside the framework is minimized via thread local Formatter/StringBuilder/log records.
  4. Android clients may use the eAlvaLog-android library alone, which is a very thin facade over Android logging. It handles such 
  things as tag creation, log statement formatting, call site location... A debug log handler is provided along with an example of 
  creating a release handler for things such as Crashlytics integration.
  5. Android clients may also use a the eAlvaLog-jdk facade along with an Android LoggerHandler. This provides the ability to mix logging
  styles using the eAlvaLog facade. For example, one part of the application can log straight to the Android logger and another part, 
  such as a library, can log to a rotating set of files. This is easily configured using some combination of Handlers, Filters, and/or 
  Markers.
  6. Markers are provided in each facade implementation and are available to filtering and formatters to be presented in log output.
  7. One design goal of eAlvaLog is to keep as much configuration code out of the framework as possible. Instead it is expected 
  dependencies will be injected either by provided helper classes or client code. This area is still largely TBD, but separation of 
  concern keeps the logging code much more simple.
  8. The underlying formatting code uses java.util.Formatter printf style formatting. We chose this for much greater flexibility in 
  formatting and to remove client code we found in applications and libraries that formats information specifically for logging. The 
  result is pushing formatting down into the framework, providing very flexible formatting options, but this implementation is heavier 
  than very simple parameter substitution found in other logging frameworks. We have found our contention with logging being unnecessary 
  object creation at the call site when logging does not occur and at lower framework levels. Once it is determined logging will occur, 
  the I/O processing greatly exceeds the formatting code. Async logging (TBD) greatly removes any concern over this style of formatting 
  as it moves formatting off to another thread, process, or computer.
  9. The resulting libraries are very small.
 
Libraries
---------

  - elavalog             - the API and some framework functionality. Required for all clients
  - ealvalog-core        - adds a little more as a base for some facade implementations. Required for clients using the following
  - ealvalog-android     - very thin facade over the Android logger. Requires ealvalog and ealvalog-core libs
  - ealvalog-jdk         - facade implementation using java.util.logging. Requires ealvalog and ealvalog-core libs
  - ealvalog-jdk-android - adds an Android handler to be used with ealvalog-jdk when more functionality is required over ealvalog-android
  
  If you wish to create another facade implementation, it's recommended you start at the ealvalog-core level. We also hope you'd 
  contribute it back to this library.
