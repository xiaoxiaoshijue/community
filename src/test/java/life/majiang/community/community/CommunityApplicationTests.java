package life.majiang.community.community;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.Users;
import life.majiang.community.community.provider.Constant;
import life.majiang.community.community.provider.RedisUtil;
import life.majiang.community.community.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
class CommunityApplicationTests {
    //@Autowired
    //private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private QuestionService questionService;

    @Test
    void t(){
        QuestionDTO questionDTOList = questionService.listFromRedis("100");
        System.out.println(questionDTOList);
    }

    @Test
    void contextLoads() {
        Users users = new Users();
        users.setUserName("xurenhai");
        users.setUserId(1L);
        String json = JSONObject.toJSONString(users);
        stringRedisTemplate.opsForValue().set("users",json);
        //jedis.auth("12321");
        String obj = stringRedisTemplate.opsForValue().get("users");
        Users u = JSONObject.parseObject(obj,Users.class);
        System.out.println(u.getUserName());
        System.out.println(u.getUserId());
        System.out.println(u.getUserInfo());
    }
    @Test
    void contextLoads1() {
        Users users = new Users();
        users.setUserName("xurenhai");
        users.setUserId(1L);
        stringRedisTemplate.opsForHash().put("uu1:400","name","laoyi1");
        stringRedisTemplate.opsForHash().put("uu1:400","age","1111");
        stringRedisTemplate.opsForHash().put("uu1:400","name2","1");

        String name = (String)stringRedisTemplate.opsForHash().get("uu1:400", "name");
        System.out.println(name);
        Map<Object, Object> uu1 = stringRedisTemplate.opsForHash().entries("uu1:400");
        System.out.println(uu1);
    }

}
