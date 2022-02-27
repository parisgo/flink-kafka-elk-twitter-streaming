package org.paris8.twitter.read.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TwitterItem {
    private String created_at;
    private String lang;
    private String text;

    @JsonIgnore
    public Long getTimestamp() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            LocalDateTime dateCreate = LocalDateTime.parse(created_at, formatter);

            return Timestamp.valueOf(dateCreate).getTime();
        }
        catch (Exception ex) {
            return System.currentTimeMillis();
        }
    };
}
