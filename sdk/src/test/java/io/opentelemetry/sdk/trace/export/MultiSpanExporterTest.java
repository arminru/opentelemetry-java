/*
 * Copyright 2019, OpenTelemetry Authors
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

package io.opentelemetry.sdk.trace.export;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

import io.opentelemetry.proto.trace.v1.Span;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Unit tests for {@link MultiSpanExporterTest}. */
@RunWith(JUnit4.class)
public class MultiSpanExporterTest {
  @Mock private SpanExporter spanExporter1;
  @Mock private SpanExporter spanExporter2;
  private static final List<Span> SPAN_LIST = Collections.singletonList(Span.newBuilder().build());

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void empty() {
    SpanExporter multiSpanExporter =
        MultiSpanExporter.create(Collections.<SpanExporter>emptyList());
    multiSpanExporter.export(SPAN_LIST);
    multiSpanExporter.shutdown();
  }

  @Test
  public void oneSpanExporter() {
    SpanExporter multiSpanExporter =
        MultiSpanExporter.create(Collections.singletonList(spanExporter1));
    multiSpanExporter.export(SPAN_LIST);
    verify(spanExporter1).export(same(SPAN_LIST));

    multiSpanExporter.shutdown();
    verify(spanExporter1).shutdown();
  }

  @Test
  public void twoSpanExporter() {
    SpanExporter multiSpanExporter =
        MultiSpanExporter.create(Arrays.asList(spanExporter1, spanExporter2));
    multiSpanExporter.export(SPAN_LIST);
    verify(spanExporter1).export(same(SPAN_LIST));
    verify(spanExporter2).export(same(SPAN_LIST));

    multiSpanExporter.shutdown();
    verify(spanExporter1).shutdown();
    verify(spanExporter2).shutdown();
  }
}
