package com.modcreater.tmbiz;

//import com.modcreater.tmbeans.pojo.TestSingEvent;
import com.modcreater.tmbiz.config.EventUtil;
import com.modcreater.tmdao.mapper.BackerMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmbizApplicationTests {

    private Logger logger = LoggerFactory.getLogger(TmbizApplicationTests.class);

}
