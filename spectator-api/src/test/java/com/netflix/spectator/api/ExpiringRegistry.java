/*
 * Copyright 2014-2018 Netflix, Inc.
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
package com.netflix.spectator.api;

import java.util.Collections;

public class ExpiringRegistry extends AbstractRegistry {

  public ExpiringRegistry(Clock clock) {
    super(clock);
  }

  @Override protected Counter newCounter(Id id) {
    return new Counter() {
      private final long creationTime = clock().wallTime();
      private long count = 0;

      @Override public void increment() {
        ++count;
      }

      @Override public void increment(long amount) {
        count += amount;
      }

      @Override public long count() {
        return count;
      }

      @Override public Id id() {
        return id;
      }

      @Override public Iterable<Measurement> measure() {
        return Collections.emptyList();
      }

      @Override public boolean hasExpired() {
        return clock().wallTime() > creationTime;
      }
    };
  }

  @Override protected DistributionSummary newDistributionSummary(Id id) {
    return null;
  }

  @Override protected Timer newTimer(Id id) {
    return null;
  }

  @Override protected Gauge newGauge(Id id) {
    return null;
  }

  @Override public void removeExpiredMeters() {
    super.removeExpiredMeters();
  }
}
