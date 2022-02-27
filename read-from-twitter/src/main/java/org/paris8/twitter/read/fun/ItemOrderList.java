package org.paris8.twitter.read.fun;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.paris8.twitter.read.bean.ItemCount;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;

public class ItemOrderList extends KeyedProcessFunction<Long, ItemCount, String> {
    ListState<ItemCount> itemViewCountListState;

    @Override
    public void open(Configuration parameters) throws Exception {
        itemViewCountListState = getRuntimeContext().getListState(new ListStateDescriptor<ItemCount>("twitter-item-count-list", ItemCount.class));
    }

    @Override
    public void processElement(ItemCount value, Context ctx, Collector<String> out) throws Exception {
        itemViewCountListState.add(value);
        ctx.timerService().registerEventTimeTimer( value.getWindowEnd() + 1 );
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
        ArrayList<ItemCount> itemViewCounts = Lists.newArrayList(itemViewCountListState.get().iterator());

        itemViewCounts.sort(new Comparator<ItemCount>() {
            @Override
            public int compare(ItemCount o1, ItemCount o2) {
                return o2.getCount().intValue() - o1.getCount().intValue();
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("*********************************\n");
        sb.append("Window time：").append( new Timestamp(timestamp - 1)).append("\n");

        for( int i = 0; i < Math.min(4, itemViewCounts.size()); i++ ){
            ItemCount currentItemViewCount = itemViewCounts.get(i);
            sb.append("NO ").append(i+1).append(": ")
                    .append(StringUtils.rightPad(currentItemViewCount.getLang(), 4, ' '))
                    .append(" count = ").append(currentItemViewCount.getCount())
                    .append("\n");
        }
        sb.append("*********************************\n");

        Thread.sleep(1000);
        out.collect(sb.toString());
    }
}