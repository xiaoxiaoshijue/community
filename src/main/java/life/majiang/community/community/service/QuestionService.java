package life.majiang.community.community.service;


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

    public List<QuestionDTO> list() {
        List<Question> questionList = questionMapper.list();   //查询question表中的信息 保存在List集合中
        List<QuestionDTO> questionDTOList = new ArrayList<>(); //创建一个QuestionDTO类型集合，用来保存结果
        //循环遍历 把结果保存在questionDTO对象中
        for (Question question : questionList) {
            User user = userMapper.findById(question.getCreator()); //通过question中的Creator属性对应user中的id属性 在数据库中查找对应的uesr
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);         //把question对象中的属性赋值给questionDTO对象
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        return questionDTOList;
    }
}
