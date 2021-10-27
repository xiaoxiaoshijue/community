package life.majiang.community.community.dto;

import life.majiang.community.community.model.User;
import life.majiang.community.community.model.Users;
import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private Users users;
}
