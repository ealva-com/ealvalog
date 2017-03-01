ealvalog
========

*ealvalog* is a Java logging facade and implementation inspired by various other logging implementations

Why?
----

 In developing both a general purpose library, forked from an earlier incarnation, and an Android application, I ended up using 2 different 
 loggers. The Android specific logger was a very thin veneer over String.format() and android.util.log. The general purpose library used 
 java.util.logging and was very heavy: lots of string concatenation even when logging was never occurring, string-number formatting when 
 nothing was being logged, enormous amount StringBuilder instances being created - many totally unnecessary.
 
 I decided I wanted a facade over various other logging implementations that provides:
   1. Aggressively minimize object creation, especially when no logging will actually occur.
   2. Powerful string formatting and parameterized substitution, in support of #1.
   3. Push configuration/dependency management/dependency injection into the client, while providing base classes or configuration classes.
   4. Keep facade thin and only add heavier lower layers as needed (logging to file, database, etc.)
 
 
