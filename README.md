eAlvaLog
========

*eAlvaLog* is a Kotlin/Java logging facade and implementation initially inspired by various other logging frameworks

Libraries
---------

  - elavalog             - the API and some framework functionality
  - ealvalog-java        - provides JLogger for java clients (fat interface, extensible). Requires ealvalog lib
  - ealvalog-core        - adds CoreLogger as base for bridging to other frameworks and Marker implementation. Requires ealvalog lib
  - ealvalog-android     - very thin facade over the Android logger. Requires ealvalog and ealvalog-core libs
  - ealvalog-jdk         - facade implementation using java.util.logging. Requires ealvalog and ealvalog-core libs
  - ealvalog-jdk-android - adds an Android handler to be used with ealvalog-jdk. Used with ealvalog-jdk when more functionality is required over ealvalog-android
  
  If you wish to create another facade implementation, it's recommended you start at the ealvalog-core level. We also hope you'd 
  contribute it back to this library.

  When to use each library:
  1. Kotlin Library: ealvalog. Add ealvalog-core if Markers are needed
  2. Java Library: ealvalog and ealvalog-java. Add ealvalog-core if Markers are needed
  3. Kotlin Android app with android logging only: ealvalog-android, ealvalog-core, and ealvalog
  4. Java Android app with android logging only: ealvalog-android, ealvalog-java, ealvalog-core, and ealvalog
  5. App requiring java.util.logging (JUL) functionality: ealvalog-jdk and #3 or #4
  6. App requiring JUL and android logging: ealvalog-jdk-android and #5
  7. Extending/customizing java logging: ealvalog-java, ealvalog-core, and ealvalog
  8. Implementing a facade over another logging framework: ealvalog-core and ealvalog. See ealvalog-jdk as an example.
  
  See the sample apps for use and required libs (build.gradle)  

Quick Start
===========



- Android Setup
```groovy
dependencies {
    compile 'com.ealva:ealvalog:0.02.00-SNAPSHOT'
    compile 'com.ealva:ealvalog-core:0.02.00-SNAPSHOT'
    compile 'com.ealva:ealvalog-android:0.02.00-SNAPSHOT'
}
```
```java
public class MyApp extends Application {
  public void onCreate() {
    AndroidLogger.setHandler(new DebugLogHandler());
    Loggers.setFactory(new AndroidLoggerFactory());
  }
}  
```  
    
- Setup for facade over java.util.logging and also logs to Android log 
```groovy
dependencies {
    implementation project(path: ':ealvalog')
    implementation project(path: ':ealvalog-java')
    implementation project(path: ':ealvalog-core')
    implementation project(path: ':ealvalog-jdk')
    implementation project(path: ':ealvalog-jdk-android')
}
```
```java
public class JavaApp extends Application {
  @Override public void onCreate() {
    super.onCreate();

    // Configure the Loggers singleton
    JdkLoggerFactory factory = JdkLoggerFactory.INSTANCE;
    Loggers.INSTANCE.setFactory(factory);

    // Configure the underlying root java.util.logging.Logger
    JdkLogger rootLogger = factory.get(JdkLoggerFactory.ROOT_LOGGER_NAME);
    // rootLogger.setIncludeLocation(true); // makes logging more expensive

    if (BuildConfig.DEBUG) {
      Fabric.with(this, CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build());
      rootLogger.addHandler(AndroidLoggerHandler.Companion.make());
      rootLogger.setLogLevel(LogLevel.WARN);
    } else {
      Fabric.with(this, CrashlyticsCore(), Answers(), Crashlytics());
      rootLogger.addHandler(CrashlyticsLogHandler());
      rootLogger.logLevel(LogLevel.ERROR);
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
public class MainActivity extends Activity  {
  private static final JLogger LOG = JLoggers.get(MainActivity.class);
  
  public void someMethod() {
    LOG.log(LogLevel.ERROR, "Widget %s too large, height=%d", widget, heightCentimeters); 
  }
}     
```
- Kotlin client
```kotlin
private val LOG by lazyLogger(ArtistTable::class) // don't get the logger unless we actually use it
class ArtistTable {
  fun someMethod(artistId: Long) {
    LOG.e { +it("No record for artist _id=%d", artistId) }  // + operator add call site information
  }
}

```     
Ensure you have the most recent version by checking [here](https://search.maven.org/search?q=ealvalog)

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
   
This library was originally written in Java but has been ported to Kotlin. The original Java API was very much like other logging
frameworks, very fat with lots of convenience methods. The Kotlin version strips away most of this API and moves the fat interface
into a separate module for Java clients. The Java interface, which is built on top of the Kotlin interface, is also extensible
to provide custom logging methods.
   
 Result
 ------
 
  1. Object creation at the log site is zero (client code aside). The Kotlin interface uses inline extension functions so no object
  creation, calculations for log info, or formatting, occurs until level checks and filters pass. For Java, if a custom JLogger subclass 
  is developed by the client, even logging primitives as format arguments will create zero objects unless the logging passes level 
  checks and filters. A JLogger subclass requires minimal code and is entirely optional.
  2. eAlvaLog provides filtering using LogLevel, Throwable, and Markers to short-circuit logging as quickly as 
  possible. If the statement will not ultimately be logged, objects are not created. 
  3. Object creation inside the framework is minimized via thread local Formatter/StringBuilder/log records.
  4. Android clients may use the eAlvaLog-android library alone, which is a very thin facade over Android logging. It handles such 
  things as tag creation, log statement formatting, call site location... A debug log handler is provided along with an example of 
  creating a release handler for things such as Crashlytics integration.
  5. Android clients may also use the eAlvaLog-jdk facade along with an Android LoggerHandler. This provides the ability to mix logging
  styles using the eAlvaLog facade. For example, one part of the application can log straight to the Android logger and another part, 
  such as a library, can log to a rotating set of files. This is easily configured using some combination of Handlers, Filters, and/or 
  Markers.
  6. Markers are provided in each facade implementation and are available to filtering and formatters to be presented in log output.
  7. One design goal of eAlvaLog is to keep as much configuration code out of the framework as possible. Instead it is expected 
  dependencies will be injected either by provided helper classes or client code. This area is still largely TBD, but separation of 
  concerns keeps the logging code much more simple.
  8. The underlying formatting code uses java.util.Formatter printf style formatting. We chose this for much greater flexibility in 
  formatting and to remove client code we found in applications and libraries that formats information specifically for logging. The 
  result is pushing formatting down into the framework, providing very flexible formatting options. The framework uses a thread local
  formatter/string builder combination to greatly reduce object creation. All formatting is done into a reused, per-thread, StringBuilder.
  9. The resulting libraries are very small.
  10. After a rewrite to directly support Kotlin style logging, the Logger interface was reduced to 6 properties and 3 functions. Kotlin
  clients use inline extension functions which push a very small amount of code into the client but provide even greater
  flexibility/control at the log site. For example, including log location information can be controlled per log statement and
  is done via the plus unary operator. A layer built on top of the Kotlin classes provides the standard Java style logging interface and
  it is also easily extended to provide even tighter control over object creation, eg. keeping primitives unboxed until the logging
  actually occurs. 
 
