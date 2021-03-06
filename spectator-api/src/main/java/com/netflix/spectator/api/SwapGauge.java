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

import com.netflix.spectator.impl.SwapMeter;

/** Wraps another gauge allowing the underlying type to be swapped. */
final class SwapGauge implements Gauge, SwapMeter<Gauge> {

  private final Registry registry;
  private final Id id;
  private volatile Gauge underlying;

  /** Create a new instance. */
  SwapGauge(Registry registry, Id id, Gauge underlying) {
    this.registry = registry;
    this.id = id;
    this.underlying = underlying;
  }

  @Override public Id id() {
    return id;
  }

  @Override public Iterable<Measurement> measure() {
    return get().measure();
  }

  @Override public boolean hasExpired() {
    Gauge g = underlying;
    return g == null || g.hasExpired();
  }

  @Override public void set(double value) {
    get().set(value);
  }

  @Override public double value() {
    return get().value();
  }

  @Override public void set(Gauge g) {
    underlying = g;
  }

  @Override public Gauge get() {
    Gauge g = underlying;
    if (g == null) {
      g = unwrap(registry.gauge(id));
      underlying = g;
    }
    return g;
  }

  private Gauge unwrap(Gauge g) {
    Gauge tmp = g;
    while (tmp instanceof SwapGauge) {
      tmp = ((SwapGauge) tmp).get();
    }
    return tmp;
  }
}
