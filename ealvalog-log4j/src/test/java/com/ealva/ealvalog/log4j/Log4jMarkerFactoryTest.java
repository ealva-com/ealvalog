/*
 * Copyright 2017 Eric A. Snell
 *
 * This file is part of eAlvaLog.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ealva.ealvalog.log4j;

import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.Markers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Eric A. Snell on 9/12/18.
 */
public class Log4jMarkerFactoryTest {

  @Test
  public void testMarkerSerialization() throws IOException, ClassNotFoundException {
    new Log4jConfiguration().configure();
    Marker first = Markers.INSTANCE.get("TestIt");
    Marker second = deserialize(serialize(first), Marker.class);
    assertThat(second, is(not(sameInstance(first))));
    assertThat(second, is(equalTo(first)));
  }

  private static <T extends Serializable> byte[] serialize(T obj) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(obj);
    oos.close();
    return baos.toByteArray();
  }

  private static <T extends Serializable> T deserialize(byte[] b, Class<T> cl)
      throws IOException, ClassNotFoundException {
    ByteArrayInputStream bais = new ByteArrayInputStream(b);
    ObjectInputStream ois = new ObjectInputStream(bais);
    Object o = ois.readObject();
    return cl.cast(o);
  }

}
