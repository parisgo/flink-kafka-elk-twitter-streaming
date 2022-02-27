package org.paris8.twitter.read.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCount {
    private String lang;
    private Long windowEnd;
    private Long count;
}
