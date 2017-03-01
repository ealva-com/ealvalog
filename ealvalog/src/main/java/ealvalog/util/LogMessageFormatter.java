package ealvalog.util;

import org.jetbrains.annotations.NotNull;

import java.util.Formatter;
import java.util.Locale;

/**
 * Helper class for formatting a string. This is likely to be a {@link ThreadLocal} passed down into logging layers to help build the
 * message. It's purpose is to reduce the number of {@link StringBuilder} instances and character array allocations/copies
 * <p>
 * Created by Eric A. Snell on 3/1/17.
 */
@SuppressWarnings("WeakerAccess")
public class LogMessageFormatter {
  private final StringBuilder builder;
  private final Formatter formatter;

  public LogMessageFormatter() {
    builder = new StringBuilder(1024);
    formatter = new Formatter(builder);
  }

  /**
   * Reset the contained {@link StringBuilder}, ie. {@code setLength(0)}
   */
  public void reset() {
    builder.setLength(0);
  }

  /**
   * Get the builder used by this formatter. Typically used before or after invoking {@link #format(String, Object...)} or
   * {@link #format(Locale, String, Object...)} to add text to the message other than what the original caller specified (eg. Marker or
   *
   * @return this formatter's internal {@link StringBuilder}
   */
  public StringBuilder getBuilder() {
    // TODO: 3/1/17 Expose this or add append() methods to this class? Feels wrong to directly expose the StringBuilder
    return builder;
  }

  /**
   * Format the {@code format} string with the given set of {@code args} into the contained {@link StringBuilder}
   *
   * @param format the format string as defined in {@link Formatter}
   * @param args   arguments pass for {@link Formatter#format(String, Object...)}
   *
   * @return self
   */
  public LogMessageFormatter format(@NotNull final String format,
                                    @NotNull final Object... args) {
    formatter.format(format, args);
    return this;
  }

  /**
   * Format the {@code format} string with the given set of {@code args} into the contained {@link StringBuilder}
   *
   * @param locale the {@link Locale} to use during formatting
   * @param format the format string as defined in {@link Formatter}
   * @param args   arguments pass for {@link Formatter#format(String, Object...)}
   */
  @SuppressWarnings("unused")
  public void format(@NotNull Locale locale,
                     @NotNull final String format,
                     @NotNull final Object... args) {
    formatter.format(locale, format, args);
  }

  /**
   * @return the result of formatting and appending to the contained {@link StringBuilder}
   */
  public String toString() {
    return builder.toString();
  }
}
