package com.modcreater.tmtrade;

import com.modcreater.tmtrade.service.OrderService;
import com.modcreater.tmtrade.service.impl.OrderServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@Component
public class TmtradeApplicationTests {

    @Resource
    private OrderServiceImpl orderService;

    @Test
    public void contextLoads() {
    }

}
