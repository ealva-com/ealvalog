package ealvalog.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * Ensure MessageFormatter correctly handles the internal StringBuilder
 * <p>
 * Created by Eric A. Snell on 3/1/17.
 */
public class LogMessageFormatterTest {
  private static final String FORMAT = "%s";
  private static final String ARG = "Arrrrgh";
  private static final String RESULT = ARG;
  private static final String PREFIX = "Prefixin's";
  private static final String SUFFIX = "Suffixilicous";

  @Test
  public void testFormat() {
    LogMessageFormatter format = new LogMessageFormatter();
    assertThat(format.format(FORMAT, ARG).toString(), is(equalTo(RESULT)));
  }

  @Test
  public void testBuilderUse() {
    LogMessageFormatter format = new LogMessageFormatter();
    format.getBuilder().append(PREFIX);
    format.format(FORMAT, ARG);
    format.getBuilder().append(SUFFIX);
    assertThat(format.toString(), is(equalTo(PREFIX + RESULT + SUFFIX)));
  }

  @Test
  public void testReset() {
    LogMessageFormatter format = new LogMessageFormatter();
    format.getBuilder().append(PREFIX);
    assertThat(format.toString(), is(equalTo(PREFIX)));
    format.reset();
    format.format(FORMAT, ARG);
    assertThat(format.toString(), is(equalTo(RESULT)));
    format.reset();
    format.getBuilder().append(SUFFIX);
    assertThat(format.toString(), is(equalTo(SUFFIX)));
  }

  @Test
  public void testMultipleToString() {
    LogMessageFormatter format = new LogMessageFormatter();
    format.format(FORMAT, ARG);
    assertThat(format.toString(), is(equalTo(RESULT)));
    assertThat(format.toString(), is(equalTo(RESULT)));
    format.getBuilder().append(SUFFIX);
    assertThat(format.toString(), is(equalTo(RESULT + SUFFIX)));
    assertThat(format.toString(), is(equalTo(RESULT + SUFFIX)));
    format.reset();
    assertThat(format.toString(), is(equalTo("")));
    assertThat(format.toString(), is(equalTo("")));
    format.format(FORMAT, ARG);
    assertThat(format.toString(), is(equalTo(RESULT)));
    assertThat(format.toString(), is(equalTo(RESULT)));
  }


}
