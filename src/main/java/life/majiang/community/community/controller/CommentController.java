package life.majiang.community.community.controller;

import life.majiang.community.community.dto.CommentCreateDTO;
import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.dto.ResultDTO;
import life.majiang.community.community.enums.CommentTypeEnum;
import life.majiang.community.community.exception.CustomizeErrorCode;
import life.majiang.community.community.model.Comment;
import life.majiang.community.community.model.Users;
import life.majiang.community.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;
    @ResponseBody
    @PostMapping("/addCommentLikeCount")
    public Integer addLikeCount(@RequestBody Map<String,Long> map){
        return commentService.addLikeCount(map.get("commentId"));
    }
    @ResponseBody
    @RequestMapping(value = "/comment")
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,//传输的json对象
                       HttpServletRequest request){
        Users users = (Users)request.getSession().getAttribute("users");
        if(users == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if(commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())){ // commentCreateDTO.getContent() == null || commentCreateDTO.getContent() == ""
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());          //1为
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(users.getUserId());
        comment.setLikeCount(0L);
        comment.setCommentCount(0);
        commentService.insert(comment, users);
        return ResultDTO.okOf(null);
    }


    @ResponseBody
    @RequestMapping(value = "/comment/{id}")
    public ResultDTO <List> comments(@PathVariable(name = "id")Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.Comment);/*二级评论*/
        return ResultDTO.okOf(commentDTOS);
    }
}
