package life.majiang.community.community.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import life.majiang.community.community.dto.PaginationDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.dto.QuestionQueryDTO;
import life.majiang.community.community.exception.ErrorCodeEnum;
import life.majiang.community.community.exception.CustomizeException;
import life.majiang.community.community.mapper.QuestionExtMapper;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.UsersMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.QuestionExample;
import life.majiang.community.community.model.Users;
import life.majiang.community.community.provider.Constant;
import life.majiang.community.community.provider.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;
    private QuestionDTO questionDTO;


    public PaginationDTO<QuestionDTO> list(String search, Integer page, Integer size) {
        if(StringUtils.isNotBlank(search)){
            String[] tags = StringUtils.split(search, " ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }else {
            search = null;
        }

        Integer totalPage;
        PaginationDTO paginationDTO = new PaginationDTO();

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        if(totalCount % size == 0 && totalCount != 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }
        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }
        paginationDTO.setPagination(page,totalPage);   //入口
        Integer offset = size * (page - 1);

        QuestionExample example = new QuestionExample();
        example.setOrderByClause("gmt_create desc");
        questionQueryDTO.setPage(offset);
        questionQueryDTO.setSize(size);
        List<Question> questionList = questionExtMapper.selectBySearch(questionQueryDTO);
        //List<Question> questionList = questionMapper.selectByExample(example)   //查询question表中的信息 保存在List集合中ID
        List<QuestionDTO> questionDTOList = new ArrayList<>(); //创建一个QuestionDTO类型集合，用来保存结果
        for (Question question : questionList) {        //循环遍历 把结果保存在questionDTO对象中
        Users users = usersMapper.selectByPrimaryKey(question.getCreator());
/* 2021109
            User user = userMapper.selectByPrimaryKey(question.getCreator()); //通过question中的Creator属性对应user中的id属性 在数据库中查找对应的uesr
*/
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);         //把question对象中的属性赋值给questionDTO对象
            questionDTO.setUsers(users);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return paginationDTO;
    }

    public PaginationDTO listByUserId(Long id, Integer page, Integer size) {
        Integer totalPage;
        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(id);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }
        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }
        paginationDTO.setPagination(page,totalPage);   //入口
        Integer offset = size * (page - 1);
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(id);
        example.setOrderByClause("gmt_create desc");
        List<Question> questionList = questionMapper.selectByExampleWithBLOBsWithRowbounds(example, new RowBounds(offset, size));
        //List<Question> questionList = questionMapper.listByUserId(id,offset,size);   //查询question表中的信息 保存在List集合中
        List<QuestionDTO> questionDTOList = new ArrayList<>(); //创建一个QuestionDTO类型集合，用来保存结果
        for (Question question : questionList) {        //循环遍历 把结果保存在questionDTO对象中
            Users users = usersMapper.selectByPrimaryKey(question.getCreator()); //通过question中的Creator属性对应user中的id属性 在数据库中查找对应的uesr
            //2021109
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);         //把question对象中的属性赋值给questionDTO对象
            questionDTO.setUsers(users);
            //2021109
            questionDTOList.add(questionDTO);//所有的问题放入此集合中 传到paginationDTO的data属性中返回前端
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }


    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null){
            throw new CustomizeException(ErrorCodeEnum.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        Users users = usersMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUsers(users);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        Users users = usersMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        if(question.getId() == null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }else {
            question.setGmtModified(System.currentTimeMillis());
            /*Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setId(question.getId());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            int update = questionMapper.updateByExampleSelective(question, example);*/
            int update = questionMapper.updateByPrimaryKeySelective(question);
            Question dbQuestion = questionMapper.selectByPrimaryKey(question.getId());
            BeanUtils.copyProperties(dbQuestion,questionDTO);
            questionDTO.setUsers(users);
            String string = JSONObject.toJSONString(questionDTO);
            stringRedisTemplate.opsForHash().put(Constant.QUESTION_INFO_KEY,Constant.TOPPING_QUESTION_ID,string);
            if(update != 1){
                throw new CustomizeException(ErrorCodeEnum.QUESTION_NOT_FOUND);
                //你寻找的问题不见了 后续增加吧主删帖功能
            }
        }
    }

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
        /*Question question = questionMapper.selectByPrimaryKey(id);
        Question updateQuestion = new Question();
        updateQuestion.setViewCount(question.getViewCount() + 1);
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andIdEqualTo(id);
        questionMapper.updateByExampleSelective(updateQuestion, example);*/
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }

    public QuestionDTO listFromRedis(String id) {

        //7、返回数据
        QuestionDTO questionDTO = listFromRedisWithPassThrough(id);
        /*if(questionDTO == null){
            throw new CustomizeException(ErrorCodeEnum.REDIS_NOT_FOUND);
        }*/
        return questionDTO;
    }

    /**
     *  互斥锁解决
     */
    public QuestionDTO listFromRedisWithMutex(String id){
        //1、从redis中查询问题缓存
        try{
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(id);
            if(map.size() != 0){
                //2、判断是否存在
                //3、存在直接返回
                //List<QuestionDTO> list = new ArrayList<>();
                Iterator<Map.Entry<Object, Object>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<Object,Object> m = iterator.next();
                    if(m.getKey() == id){
                        QuestionDTO questionDTO = JSON.parseObject((String) m.getValue(),QuestionDTO.class);
                        return questionDTO;
                    }
                    //判断命中的是否是空值(缓存穿透
                    if(m.getValue() == null){
                        return null;
                    }
                }
            }
        }catch (Exception e){
            throw new CustomizeException(ErrorCodeEnum.REDIS_NOT_FOUND);
        }
        //4、实现缓存重建
        //4.1、获取互斥锁
        String lockKey = "lock:shop" + id;
        //4.2、判断是否获取成功
        try {
            boolean isLock = redisUtil.tryLock(lockKey);
            //4.3、失败，则休眠并重试
            if(!isLock){
                Thread.sleep(50);
                return listFromRedisWithMutex(id);
            }
            //4.4、成功，根据id查询数据库
            //5、不存在，查询数据库
            Question question = questionMapper.selectByPrimaryKey(1L);
            if(question == null){
                //6、数据库不存在
                stringRedisTemplate.opsForHash().put(Constant.QUESTION_INFO_KEY,id,"");
                return null;
            }
            Users users = usersMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUsers(users);
            //7、存在 先吧数据写入redis
            String string = JSONObject.toJSONString(questionDTO);
            stringRedisTemplate.opsForHash().put(Constant.QUESTION_INFO_KEY,Constant.TOPPING_QUESTION_ID,string);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //8、释放互斥锁
            redisUtil.unLock(lockKey);
        }
        return questionDTO;
    }
    public QuestionDTO listFromRedisWithPassThrough(String id){
        //1、从redis中查询问题缓存
        try{
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(id);
            if(map.size() != 0){
                //2、判断是否存在
                //3、存在直接返回
                //List<QuestionDTO> list = new ArrayList<>();
                Iterator<Map.Entry<Object, Object>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<Object,Object> m = iterator.next();
                    if(m.getKey() == id){
                        QuestionDTO questionDTO = JSON.parseObject((String) m.getValue(),QuestionDTO.class);
                        return questionDTO;
                    }
                    //判断命中的是否是空值(缓存穿透
                    if(m.getValue() == null){
                        return null;
                    }
                }
            }
        }catch (Exception e){
            throw new CustomizeException(ErrorCodeEnum.REDIS_NOT_FOUND);
        }
        //4、不存在，查询数据库
        Long dbId = Long.parseLong(id);
        Question question = questionMapper.selectByPrimaryKey(dbId);
        if(question == null){
            //5、数据库不存在
            stringRedisTemplate.opsForHash().put(Constant.QUESTION_INFO_KEY,id,"");
            return null;
        }
        Users users = usersMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUsers(users);
        //6、存在 先吧数据写入redis
        String string = JSONObject.toJSONString(questionDTO);
        stringRedisTemplate.opsForHash().put(Constant.QUESTION_INFO_KEY,Constant.TOPPING_QUESTION_ID,string);
        return questionDTO;
    }
}
