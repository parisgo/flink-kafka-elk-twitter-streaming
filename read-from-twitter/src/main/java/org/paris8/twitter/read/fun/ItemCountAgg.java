package org.paris8.twitter.read.fun;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.paris8.twitter.read.bean.TwitterItem;

public class ItemCountAgg implements AggregateFunction<TwitterItem, Long, Long> {
    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(TwitterItem value, Long accumulator) {
        return accumulator + 1;
    }

    @Override
    public Long getResult(Long accumulator) {
        return accumulator;
    }

    @Override
    public Long merge(Long a, Long b) {
        return a + b;
    }
}
