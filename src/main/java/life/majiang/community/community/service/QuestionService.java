package life.majiang.community.community.service;


import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QDecoderStream;
import life.majiang.community.community.dto.PaginationDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Integer page, Integer size) {

        Integer totalPage;
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.count();
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

        List<Question> questionList = questionMapper.list(offset,size);   //查询question表中的信息 保存在List集合中
        List<QuestionDTO> questionDTOList = new ArrayList<>(); //创建一个QuestionDTO类型集合，用来保存结果
        for (Question question : questionList) {        //循环遍历 把结果保存在questionDTO对象中
            User user = userMapper.findById(question.getCreator()); //通过question中的Creator属性对应user中的id属性 在数据库中查找对应的uesr
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);         //把question对象中的属性赋值给questionDTO对象
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }

    public PaginationDTO listByUserId(Integer id, Integer page, Integer size) {
        Integer totalPage;
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.countByUserId(id);
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

        List<Question> questionList = questionMapper.listByUserId(id,offset,size);   //查询question表中的信息 保存在List集合中
        List<QuestionDTO> questionDTOList = new ArrayList<>(); //创建一个QuestionDTO类型集合，用来保存结果
        for (Question question : questionList) {        //循环遍历 把结果保存在questionDTO对象中
            User user = userMapper.findById(question.getCreator()); //通过question中的Creator属性对应user中的id属性 在数据库中查找对应的uesr
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);         //把question对象中的属性赋值给questionDTO对象
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }


    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.findById(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.create(question);
        }else {
            question.setGmtModified(question.getGmtCreate());
            questionMapper.update(question);
        }
    }
}
