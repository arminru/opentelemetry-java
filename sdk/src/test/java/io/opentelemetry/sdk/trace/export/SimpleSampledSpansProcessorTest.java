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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.trace.SpanContext;
import io.opentelemetry.trace.SpanId;
import io.opentelemetry.trace.TraceId;
import io.opentelemetry.trace.TraceOptions;
import io.opentelemetry.trace.Tracestate;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Unit tests for {@link SimpleSampledSpansProcessor}. */
@RunWith(JUnit4.class)
public class SimpleSampledSpansProcessorTest {
  @Mock private ReadableSpan readableSpan;
  @Mock private SpanExporter spanExporter;
  private static final SpanContext SAMPLED_SPAN_CONTEXT =
      SpanContext.create(
          TraceId.getInvalid(),
          SpanId.getInvalid(),
          TraceOptions.builder().setIsSampled(true).build(),
          Tracestate.builder().build());
  private static final SpanContext NOT_SAMPLED_SPAN_CONTEXT =
      SpanContext.create(
          TraceId.getInvalid(),
          SpanId.getInvalid(),
          TraceOptions.builder().build(),
          Tracestate.builder().build());

  private SimpleSampledSpansProcessor simpleSampledSpansProcessor;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    simpleSampledSpansProcessor = SimpleSampledSpansProcessor.newBuilder(spanExporter).build();
  }

  @Test
  public void onStartSync() {
    simpleSampledSpansProcessor.onStartSync(readableSpan);
    verifyZeroInteractions(spanExporter);
  }

  @Test
  public void onEndSync_SampledSpan() {
    when(readableSpan.getSpanContext()).thenReturn(SAMPLED_SPAN_CONTEXT);
    when(readableSpan.toSpanProto())
        .thenReturn(Span.getDefaultInstance())
        .thenThrow(new RuntimeException());
    simpleSampledSpansProcessor.onEndSync(readableSpan);
    verify(spanExporter).export(Collections.singletonList(Span.getDefaultInstance()));
  }

  @Test
  public void onEndSync_NotSampledSpan() {
    when(readableSpan.getSpanContext()).thenReturn(NOT_SAMPLED_SPAN_CONTEXT);
    when(readableSpan.toSpanProto())
        .thenReturn(Span.getDefaultInstance())
        .thenThrow(new RuntimeException());
    simpleSampledSpansProcessor.onEndSync(readableSpan);
    verifyZeroInteractions(spanExporter);
  }

  @Test
  public void onEndSync_ExporterReturnError() {
    when(readableSpan.getSpanContext()).thenReturn(SAMPLED_SPAN_CONTEXT);
    when(readableSpan.toSpanProto())
        .thenReturn(Span.getDefaultInstance())
        .thenReturn(Span.getDefaultInstance())
        .thenThrow(new RuntimeException());
    doThrow(new RuntimeException())
        .doNothing()
        .when(spanExporter)
        .export(ArgumentMatchers.<Span>anyList());
    simpleSampledSpansProcessor.onEndSync(readableSpan);
    // Try again, now will no longer return error.
    simpleSampledSpansProcessor.onEndSync(readableSpan);
    verify(spanExporter, times(2)).export(Collections.singletonList(Span.getDefaultInstance()));
  }

  @Test
  public void shutdown() {
    simpleSampledSpansProcessor.shutdown();
    verify(spanExporter).shutdown();
  }
}
