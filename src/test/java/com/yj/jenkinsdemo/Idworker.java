package com.yj.jenkinsdemo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Idworker {
    private static final AtomicInteger SEQ = new AtomicInteger(1000);
    private static final DateTimeFormatter DF_FMT_PREFIX = DateTimeFormatter.ofPattern("yyMMddHHmmssSS");
    private static ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    public static String generateOrderNo() {
        LocalDateTime dataTime = LocalDateTime.now(ZONE_ID);
        if (SEQ.intValue() > 9990) {
            SEQ.getAndSet(1000);
        }
        return dataTime.format(DF_FMT_PREFIX) + SEQ.getAndIncrement();
    }

    public static void main(String[] args) {
        List<String> orderNos = Collections.synchronizedList(new ArrayList<String>());
        IntStream.range(0,8000).parallel().forEach(i->{
            orderNos.add(generateOrderNo());
        });

        List<String> filterOrderNos = orderNos.stream().distinct().collect(Collectors.toList());

        System.out.println("生成订单数："+orderNos.size());
        System.out.println("过滤重复后订单数："+filterOrderNos.size());
        System.out.println("重复订单数："+(orderNos.size()-filterOrderNos.size()));
    }
}
