package life.majiang.community.community.service;

import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.enums.CommentTypeEnum;
import life.majiang.community.community.enums.NotificationEnum;
import life.majiang.community.community.enums.NotificationStatusEnum;
import life.majiang.community.community.exception.CustomizeErrorCode;
import life.majiang.community.community.exception.CustomizeException;
import life.majiang.community.community.mapper.*;
import life.majiang.community.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User commentator) {
        //如果没有父问题 则抛出 "未选择任何问题或者评论进行回复" 异常
        if(comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
            //throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        //类型不存在
        if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        //type = 2 回复评论
        if(comment.getType() == CommentTypeEnum.Comment.getType()){

            /**
             * dbComment    : 根据回答人的id 去数据库搜索此回答 得到的对象
             * comment      : 回复评论的对象
             * parentComment: 发布回答的对象
             */
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                // 评论不存在
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            // 创建回复
            commentMapper.insert(comment);
            // 增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId()); // 根据父id 增加评论数
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);
            // 创建通知
            CreateNotify(comment, dbComment.getCommentator(), commentator.getName(), comment.getContent(), NotificationEnum.REPLY_COMMENT,question.getId());
        }else {
            // type = 1 回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
            // 创建通知
            CreateNotify(comment, question.getCreator(), commentator.getName(),question.getTitle(),NotificationEnum.REPLY_QUESTION,question.getId());
        }
    }

    private void CreateNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationEnum notificationEnum, Long outerId) {
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationEnum.getType());
        notification.setOuterId(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(example);
        if(comments.size() == 0){
            return new ArrayList<>();
        }
        //获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());/*java8 未get*/
        List<Long> usersId = new ArrayList<>();
        usersId.addAll(commentators);
        //获取评论人并转换为Map
        UserExample example1 = new UserExample();
        example1.createCriteria()
                .andIdIn(usersId);
        List<User> users = userMapper.selectByExample(example1);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //转换 comment 称为 commentDTO
        List<CommentDTO> collectDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return collectDTOS;
    }
}