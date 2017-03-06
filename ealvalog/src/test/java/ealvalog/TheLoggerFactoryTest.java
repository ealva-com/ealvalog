package ealvalog;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;


/**
 * Simple tests for logger factory singleton
 *
 * Created by Eric A. Snell on 3/5/17.
 */
@SuppressWarnings("WeakerAccess")
public class TheLoggerFactoryTest {
  @Mock LoggerFactory mockFactory;
  @Mock Logger mockLogger;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    TheLoggerFactory.setFactory(mockFactory);
  }

  @Test
  public void testUnspecifiedLoggerName() {
    given(mockFactory.get(TheLoggerFactoryTest.class.getName())).willReturn(mockLogger);

    // when
    final Logger logger = TheLoggerFactory.get();

    then(mockFactory).should(atLeastOnce()).get(TheLoggerFactoryTest.class.getName());
    assertThat(logger, is(mockLogger));  // not really testing anything, but using that variable
  }


}
