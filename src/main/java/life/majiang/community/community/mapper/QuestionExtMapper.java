package life.majiang.community.community.mapper;

import life.majiang.community.community.dto.QuestionQueryDTO;
import life.majiang.community.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record);
    int incCommentCount(Question record);
    List<Question> selectRelated(Question question);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);
}
