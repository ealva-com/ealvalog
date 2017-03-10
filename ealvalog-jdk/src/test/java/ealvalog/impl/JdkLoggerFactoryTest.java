package ealvalog.impl;

import ealvalog.LogLevel;
import ealvalog.Logger;
import ealvalog.LoggerFactory;
import ealvalog.LoggerFilter;
import ealvalog.Marker;
import ealvalog.TheLoggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test {@link LoggerFactory} implementation that use the java.util.logging.Logger as the underlying logger.
 * Created by Eric A. Snell on 3/7/17.
 */
public class JdkLoggerFactoryTest {
  private JdkLogger theRootLogger;
  private JdkBridge theRootBridge;

  @Before
  public void setup() {
    final JdkLoggerFactory loggerFactory = JdkLoggerFactory.instance();
    theRootLogger = JdkLoggerFactory.instance().get(LoggerFactory.ROOT_LOGGER_NAME);
    theRootBridge = theRootLogger.getBridge();
    TheLoggerFactory.setFactory(loggerFactory);
  }

  @Test
  public void testGetRootLogger() {
    final Logger rootByName = TheLoggerFactory.get("");
    assertThat(rootByName, is(notNullValue()));
    assertThat(theRootLogger, is(rootByName));
    assertThat(theRootLogger.getBridge(), is(theRootBridge));

    final Logger root = TheLoggerFactory.getRoot();
    assertThat(root, is(notNullValue()));

    assertThat(root, is(rootByName));
    assertThat(rootByName.getName(), is(equalTo(LoggerFactory.ROOT_LOGGER_NAME)));

    JdkLogger directRoot = JdkLoggerFactory.instance().get("");
    assertThat(directRoot, is(notNullValue()));
    assertThat(directRoot, is(root));

    JdkLogger anotherDirect = JdkLoggerFactory.instance().get("");
    assertThat(anotherDirect, is(notNullValue()));
    assertThat(anotherDirect, is(directRoot));

    final JdkBridge directBridge = directRoot.getBridge();
    assertThat(directBridge, is(notNullValue()));

    final JdkBridge anotherBridge = anotherDirect.getBridge();
    assertThat(anotherBridge, is(notNullValue()));
    assertThat(directBridge, is(anotherBridge));

    assertThat(directBridge.getName(), is(equalTo(LoggerFactory.ROOT_LOGGER_NAME)));
    assertThat(directBridge.getParent(), is(nullValue()));
  }

  @Test
  public void testBridgeIsRoot() {
    final Logger logger = TheLoggerFactory.get();

    final JdkLogger jdkLogger = JdkLoggerFactory.instance().get(this.getClass().getName());
    assertThat(jdkLogger, is(logger));

    final JdkBridge bridge = jdkLogger.getBridge();
    assertThat(bridge, is(theRootBridge));  // no filter set so will be the root
  }

  @Test
  public void testSetFilter() {
    final Logger logger = TheLoggerFactory.get();

    final JdkLogger jdkLogger = JdkLoggerFactory.instance().get(this.getClass().getName());
    assertThat(jdkLogger, is(logger));

    JdkBridge bridge = jdkLogger.getBridge();
    assertThat(bridge, is(theRootBridge));  // no filter set so will be the root

    LoggerFilter dummy = new LoggerFilter() {
      @Override public boolean isLoggable(@NotNull final LogLevel level) {
        return true;
      }

      @Override
      public boolean isLoggable(@NotNull final LogLevel level, @Nullable final Marker marker, @Nullable final Throwable throwable) {
        return true;
      }
    };
    jdkLogger.setFilter(dummy);

    bridge = jdkLogger.getBridge();
    assertThat(bridge, is(not(theRootBridge)));  // should have a new bridge
    assertThat(bridge.getFilter(), is(dummy));

    assertThat(bridge.getParent(), is(theRootBridge));

  }

  @Test
  public void testReset() {
    JdkLogger jdkLogger = JdkLoggerFactory.instance().get(this.getClass().getName());

    JdkBridge bridge = jdkLogger.getBridge();
    assertThat(bridge, is(theRootBridge));  // no filter set so will be the root

    LoggerFilter dummy = new LoggerFilter() {
      @Override public boolean isLoggable(@NotNull final LogLevel level) {
        return true;
      }

      @Override
      public boolean isLoggable(@NotNull final LogLevel level, @Nullable final Marker marker, @Nullable final Throwable throwable) {
        return true;
      }
    };
    jdkLogger.setFilter(dummy);
    bridge = jdkLogger.getBridge();
    assertThat(bridge, is(not(theRootBridge)));  // should have a new bridge
    assertThat(bridge.getFilter(), is(dummy));
    assertThat(bridge.getParent(), is(theRootBridge));

    JdkLoggerFactory.instance().reset();
    jdkLogger = JdkLoggerFactory.instance().get(this.getClass().getName());
    bridge = jdkLogger.getBridge();
    assertThat(bridge, is(theRootBridge));  // no filter set so will be the root
  }

}
