package life.majiang.community.community.controller;

import life.majiang.community.community.dto.NotificationDTO;
import life.majiang.community.community.enums.NotificationEnum;
import life.majiang.community.community.exception.CustomizeErrorCode;
import life.majiang.community.community.exception.CustomizeException;
import life.majiang.community.community.mapper.CommentMapper;
import life.majiang.community.community.model.Comment;
import life.majiang.community.community.model.CommentExample;
import life.majiang.community.community.model.Users;
import life.majiang.community.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class NotificationController {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private NotificationService notificationService;
    @GetMapping("/notification/{type}/{outerId}/{id}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "type")int type,
                          @PathVariable(name = "outerId")Long outerId,
                          @PathVariable(name = "id")Long id){
        Users users = (Users) request.getSession().getAttribute("users");
        if(users == null){
            return "redirect:/";
        }
        notificationService.read(id,users);
        if(NotificationEnum.REPLY_QUESTION.getType() == type){
            //outerId ：问题id
            return "redirect:/question/"+outerId;
        }else if(NotificationEnum.REPLY_COMMENT.getType() == type){
            //outerId：一级评论id
                //需要根据一级评论id获取问题id
            Comment dbComment = commentMapper.selectByPrimaryKey(outerId);
            if(dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            return "redirect:/question/"+dbComment.getParentId();
        }else {
            return "redirect:/";
        }
    }
}
















