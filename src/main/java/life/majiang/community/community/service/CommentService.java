package life.majiang.community.community.service;

import com.alibaba.fastjson.JSONObject;
import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.enums.CommentTypeEnum;
import life.majiang.community.community.enums.NotificationEnum;
import life.majiang.community.community.enums.NotificationStatusEnum;
import life.majiang.community.community.exception.ErrorCodeEnum;
import life.majiang.community.community.exception.CustomizeException;
import life.majiang.community.community.mapper.*;
import life.majiang.community.community.model.*;
import life.majiang.community.community.provider.Constant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, Users commentator) {
        //如果没有父问题 则抛出 "未选择任何问题或者评论进行回复" 异常
        if(comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(ErrorCodeEnum.TARGET_PARAM_NOT_FOUND);
            //throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        //类型不存在
        if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(ErrorCodeEnum.TYPE_PARAM_WRONG);
        }
        //type = 2 回复一级评论
        /**
         * dbComment    : 根据回答人的id 去数据库搜索此回答 得到的对象
         * comment      : 回复评论的对象
         * parentComment: 发布回答的对象
         */
        if ( comment.getType() == CommentTypeEnum.Comment.getType() ) {
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                // 评论不存在
                throw new CustomizeException(ErrorCodeEnum.COMMENT_NOT_FOUND);
            }
            //回复问题
           /* Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }*/

            // 创建回复
            commentMapper.insert(comment);
            // 增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId()); // 根据父id 增加评论数
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);
            //查找父评论id
            /*CommentExample commentExample = new CommentExample();
            commentExample.createCriteria().andParentIdEqualTo(comment.getParentId());
            List<Comment> dbParentComment = commentMapper.selectByExample(commentExample);*/
            Comment dbParentComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            Users dbUser = usersMapper.selectByPrimaryKey(dbParentComment.getCommentator());
            if(dbParentComment != null){
                // 创建通知
                CreateNotify(comment.getCommentator(), dbComment.getCommentator(), commentator.getUserName(), dbParentComment.getContent(), NotificationEnum.REPLY_COMMENT,dbParentComment.getId(),dbUser.getUserName());
            }else {
                throw new CustomizeException(ErrorCodeEnum.QUESTION_NOT_FOUND);
            }
        }
        else {
            // type = 1 回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            Users users = usersMapper.selectByPrimaryKey(question.getCreator());
            if(question == null){
                throw new CustomizeException(ErrorCodeEnum.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);

            QuestionDTO questionDTO = new QuestionDTO();
            Question dbQuestion = questionMapper.selectByPrimaryKey(question.getId());
            BeanUtils.copyProperties(dbQuestion,questionDTO);
            questionDTO.setUsers(users);
            String string = JSONObject.toJSONString(questionDTO);
            stringRedisTemplate.opsForHash().put(Constant.QUESTION_INFO_KEY,Constant.TOPPING_QUESTION_ID,string);

            // 创建通知
            CreateNotify(comment.getCommentator(), question.getCreator(), commentator.getUserName(),question.getTitle(),NotificationEnum.REPLY_QUESTION,question.getId(),users.getUserName());
        }
    }

    private void CreateNotify(Long notifier, Long receiver, String notifierName, String outerTitle, NotificationEnum notificationEnum, Long outerId, String receiverName) {
        if(receiver == notifier){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationEnum.getType());
        notification.setOuterId(outerId);
        notification.setNotifier(notifier);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notification.setReceiverName(receiverName);
        notificationMapper.insert(notification);
    }

    /**
     *
     * @param id   当type = 1时 表示question表中问题的id
     *             当type = 2时 表示comment表中对应一级评论的id
     * @param type 当type = 1
     *             表示搜索当前问题下的所有一级评论
     *             当type = 2
     *             表示搜索一级评论下所有的二级评论
     * @return
     */
    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample example = new CommentExample();
        //根据id -- parentId 和 type 查找评论
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
        UsersExample example1 = new UsersExample();
        example1.createCriteria()
                .andUserIdIn(usersId);
        List<Users> users = usersMapper.selectByExample(example1);
        Map<Long, Users> userMap = users.stream().collect(Collectors.toMap(Users::getUserId, user -> user));
        //转换 comment 称为 commentDTO
        List<CommentDTO> collectDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        //最终返回所有的comment信息 加上 对应的User信息 包装在collectDTOS中
        return collectDTOS;
    }

    public int addLikeCount(Long commentId) {
        Comment comment = new Comment();
        comment.setId(commentId);
        int i = commentExtMapper.addLikeCount(comment);
        return i;
    }
}
