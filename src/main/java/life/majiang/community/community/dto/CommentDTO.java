package life.majiang.community.community.dto;

import life.majiang.community.community.model.User;
import life.majiang.community.community.model.Users;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private String content;
    private Integer commentCount;
    private Users user;
}
