package life.majiang.community.community.controller;

import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.enums.CommentTypeEnum;
import life.majiang.community.community.service.CommentService;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    /**
     * 根据id显示问题信息
     * 入口：1、首页点击问题进入。 2、通知页面点击问题
     * @param id 问题id
     * @param model 携带问题信息 相关问题信息 评论信息
     * @return 问题详细页面
     */
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id")Long id,
                           Model model){
        QuestionDTO questionDTO = questionService.getById(id);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        /**
         *  由 parentID 和 type 标识一个回答/回答下的评论
         *  如果是回答 则 parentId指向question.id
         *  如果是回答下的评论 则parentId指向comment.id
         */
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.Question);
        //累加阅读数
        questionService.incView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("relatedQuestions",relatedQuestions);
        model.addAttribute("comments",comments);
        return "question";
    }
}
