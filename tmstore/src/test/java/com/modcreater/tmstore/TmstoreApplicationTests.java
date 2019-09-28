package com.modcreater.tmstore;

import com.modcreater.tmdao.mapper.BackerMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmdao.mapper.GoodsMapper;
import com.modcreater.tmdao.mapper.StoreMapper;
import com.modcreater.tmstore.service.GoodsService;
import com.modcreater.tmstore.service.impl.GoodsServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Component
public class TmstoreApplicationTests {

/*    @Resource
    private GoodsMapper goodsMapper;*/

    @Test
    public void test1() {
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map1 = new HashMap<>(2);
        map1.put("goodsId", "13");
        map1.put("goodsNum", "100");
        Map<String, String> map2 = new HashMap<>(2);
        map2.put("goodsId", "17");
        map2.put("goodsNum", "500");
        mapList.add(map1);
        mapList.add(map2);
//        goodsMapper.deductionStock("10",mapList);
    }
}
