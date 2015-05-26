/**
 * Copyright (C) 2014-2015 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.pinot.core.segment.creator.impl.stats;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.linkedin.pinot.common.data.FieldSpec;
import com.linkedin.pinot.core.segment.creator.AbstractColumnStatisticsCollector;


/**
 * Nov 7, 2014
 */

public class DoubleColumnPreIndexStatsCollector extends AbstractColumnStatisticsCollector {

  private Double min = null;
  private Double max = null;
  private final Set<Double> doubleSet;
  private Double[] sortedDoubleList;
  private boolean hasNull = false;
  private boolean sealed = false;

  public DoubleColumnPreIndexStatsCollector(FieldSpec spec) {
    super(spec);
    doubleSet = new HashSet<Double>();
  }

  @Override
  public void collect(Object entry) {
    if (entry instanceof Object[]) {
      for (final Object e : (Object[]) entry) {
        doubleSet.add(((Double) e).doubleValue());
      }
      if (maxNumberOfMultiValues < ((Object[]) entry).length) {
        maxNumberOfMultiValues = ((Object[]) entry).length;
      }
      updateTotalNumberOfEntries((Object[]) entry);
      return;
    }

    addressSorted(entry);
    doubleSet.add(((Double) entry).doubleValue());
  }

  @Override
  public Double getMinValue() throws Exception {
    if (sealed) {
      return min;
    }
    throw new IllegalAccessException("you must seal the collector first before asking for min value");
  }

  @Override
  public Double getMaxValue() throws Exception {
    if (sealed) {
      return max;
    }
    throw new IllegalAccessException("you must seal the collector first before asking for min value");
  }

  @Override
  public Object[] getUniqueValuesSet() throws Exception {
    if (sealed) {
      return sortedDoubleList;
    }
    throw new IllegalAccessException("you must seal the collector first before asking for min value");
  }

  @Override
  public int getCardinality() throws Exception {
    if (sealed) {
      return doubleSet.size();
    }
    throw new IllegalAccessException("you must seal the collector first before asking for min value");
  }

  @Override
  public boolean hasNull() {
    return false;
  }

  @Override
  public void seal() {
    sealed = true;
    sortedDoubleList = new Double[doubleSet.size()];
    doubleSet.toArray(sortedDoubleList);

    Arrays.sort(sortedDoubleList);

    if (sortedDoubleList.length == 0) {
      min = null;
      max = null;
      return;
    }

    min = sortedDoubleList[0];
    if (sortedDoubleList.length == 0) {
      max = sortedDoubleList[0];
    } else {
      max = sortedDoubleList[sortedDoubleList.length - 1];
    }

  }
}
