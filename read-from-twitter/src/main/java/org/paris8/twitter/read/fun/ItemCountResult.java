package org.paris8.twitter.read.fun;

import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.paris8.twitter.read.bean.ItemCount;

public class ItemCountResult implements WindowFunction<Long, ItemCount, String, TimeWindow> {
    @Override
    public void apply(String text, TimeWindow window, Iterable<Long> input, Collector<ItemCount> out) throws Exception {
        out.collect(new ItemCount(text, window.getEnd(), input.iterator().next()));
    }
}

