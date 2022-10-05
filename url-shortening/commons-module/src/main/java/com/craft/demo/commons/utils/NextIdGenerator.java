package com.craft.demo.commons.utils;

import com.craft.demo.commons.exceptions.TimeMovingBackwardException;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class NextIdGenerator {

    private static final long MAX_SEQUENCE = ~(-1L << 12);  //Its is -> 2^12 -1 -> 4095
    private static final long START_STAMP = 1655317800000L; // Thu Jun 16 2022 00:00:00 in milliseconds
    private final Random random = new Random();
    private long lastStamp = -1L;
    private long sequence = 0L;

    public synchronized long nextId() {
        long currStamp = getNewStamp();
        if (currStamp < lastStamp) {
            throw new TimeMovingBackwardException("Impossible as last timestamp is bigger than current one. Unable to generate id");
        }

        //If multiple request in same timestamp, then sequence is incremented (current set Limit 2^12)
        if (currStamp == lastStamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;

            // If all limit is exhausted, move to next moment(millisecond)
            if (sequence == 0L) {
                currStamp = getNextMill();
            }
        } else {
            sequence = 0L;
        }

        lastStamp = currStamp;

        // For next moment sequence is regenerated to take a random value again.
        if (sequence == 0L) {
            sequence = random.nextInt(10);
        }

        return (currStamp - START_STAMP) << 22 | sequence; //if running on multiple locations and servers, we can use binary of server/datacenter/location number and 'OR' in final result
    }

    private long getNextMill() {
        long mill = getNewStamp();
        while (mill <= lastStamp) {
            mill = getNewStamp();
        }
        return mill;
    }

    private long getNewStamp() {
        return System.currentTimeMillis();
    }

}
