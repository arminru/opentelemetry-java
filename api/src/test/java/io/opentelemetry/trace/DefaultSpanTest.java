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

package io.opentelemetry.trace;

import static com.google.common.truth.Truth.assertThat;

import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link DefaultSpan}. */
@RunWith(JUnit4.class)
public class DefaultSpanTest {
  @Test
  public void hasInvalidContextAndDefaultSpanOptions() {
    SpanContext context = DefaultSpan.create().getContext();
    assertThat(context.getTraceOptions()).isEqualTo(TraceOptions.getDefault());
    assertThat(context.getTracestate()).isEqualTo(Tracestate.getDefault());
  }

  @Test
  public void hasUniqueTraceIdAndSpanId() {
    DefaultSpan span1 = DefaultSpan.create();
    DefaultSpan span2 = DefaultSpan.create();
    assertThat(span1.getContext().getTraceId()).isNotEqualTo(span2.getContext().getTraceId());
    assertThat(span1.getContext().getSpanId()).isNotEqualTo(span2.getContext().getSpanId());
  }

  @Test
  public void doNotCrash() {
    DefaultSpan span = DefaultSpan.create();
    span.setAttribute(
        "MyStringAttributeKey", AttributeValue.stringAttributeValue("MyStringAttributeValue"));
    span.setAttribute("MyBooleanAttributeKey", AttributeValue.booleanAttributeValue(true));
    span.setAttribute("MyLongAttributeKey", AttributeValue.longAttributeValue(123));
    span.addEvent("event");
    span.addEvent(
        "event",
        Collections.singletonMap(
            "MyBooleanAttributeKey", AttributeValue.booleanAttributeValue(true)));
    span.addEvent(SpanData.Event.create("event"));
    span.addLink(SpanData.Link.create(SpanContext.getInvalid()));
    span.addLink(SpanContext.getInvalid());
    span.addLink(SpanContext.getInvalid(), Collections.<String, AttributeValue>emptyMap());
    span.setStatus(Status.OK);
    span.end();
  }

  @Test
  public void defaultSpan_ToString() {
    DefaultSpan span = DefaultSpan.create();
    assertThat(span.toString()).isEqualTo("DefaultSpan");
  }
}
