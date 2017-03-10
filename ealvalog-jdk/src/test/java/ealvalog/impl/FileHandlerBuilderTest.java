package ealvalog.impl;

import ealvalog.LogLevel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Test to see if the handle is configured correctly
 * <p>
 * Created by Eric A. Snell on 3/8/17.
 */
public class FileHandlerBuilderTest {

  private static final String MSG = "msg";
  private ExtLogRecord record;

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Before
  public void setup() {
    record = ExtLogRecord.get(LogLevel.CRITICAL,
                              MSG,
                              "LoggerName",
                              null,
                              null);
  }

  @Test(expected = IllegalStateException.class)
  public void testNoPattern() throws Exception {
    FileHandlerBuilder builder = new FileHandlerBuilder();
    //noinspection unused
    final LoggerHandler handler = builder.build();
  }

  @Test
  public void testWithPattern() throws Exception {
    final File root = folder.getRoot();
    final String fileNamePattern = new File(root, "ealvalog.%g.%u.log").getAbsolutePath();
    final LoggerHandler handler = new FileHandlerBuilder().fileNamePattern(fileNamePattern)
                                                          .build();
    handler.publish(record);

    final String[] array = root.list();
    assertThat(array, is(notNullValue()));

    assert array != null; // shut up IntelliJ
    final List<String> list = Arrays.asList(array);

    //noinspection ConstantConditions
    assertThat(list, is(hasItem("ealvalog.0.0.log")));
  }

}
