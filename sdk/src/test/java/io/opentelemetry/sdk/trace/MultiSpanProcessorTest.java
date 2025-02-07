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

package io.opentelemetry.sdk.trace;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Unit tests for {@link MultiSpanProcessorTest}. */
@RunWith(JUnit4.class)
public class MultiSpanProcessorTest {
  @Mock private SpanProcessor spanProcessor1;
  @Mock private SpanProcessor spanProcessor2;
  @Mock private ReadableSpan readableSpan;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void empty() {
    SpanProcessor multiSpanProcessor =
        MultiSpanProcessor.create(Collections.<SpanProcessor>emptyList());
    multiSpanProcessor.onStartSync(readableSpan);
    multiSpanProcessor.onEndSync(readableSpan);
    multiSpanProcessor.shutdown();
  }

  @Test
  public void oneSpanProcessor() {
    SpanProcessor multiSpanProcessor =
        MultiSpanProcessor.create(Collections.singletonList(spanProcessor1));
    multiSpanProcessor.onStartSync(readableSpan);
    verify(spanProcessor1).onStartSync(same(readableSpan));

    multiSpanProcessor.onEndSync(readableSpan);
    verify(spanProcessor1).onEndSync(same(readableSpan));

    multiSpanProcessor.shutdown();
    verify(spanProcessor1).shutdown();
  }

  @Test
  public void twoSpanProcessor() {
    SpanProcessor multiSpanProcessor =
        MultiSpanProcessor.create(Arrays.asList(spanProcessor1, spanProcessor2));
    multiSpanProcessor.onStartSync(readableSpan);
    verify(spanProcessor1).onStartSync(same(readableSpan));
    verify(spanProcessor2).onStartSync(same(readableSpan));

    multiSpanProcessor.onEndSync(readableSpan);
    verify(spanProcessor1).onEndSync(same(readableSpan));
    verify(spanProcessor2).onEndSync(same(readableSpan));

    multiSpanProcessor.shutdown();
    verify(spanProcessor1).shutdown();
    verify(spanProcessor2).shutdown();
  }
}
