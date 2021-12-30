package life.majiang.community.community.mapper;

import java.util.List;
import life.majiang.community.community.model.UserAuthRel;
import life.majiang.community.community.model.UserAuthRelExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface UserAuthRelMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    long countByExample(UserAuthRelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    int deleteByExample(UserAuthRelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    int insert(UserAuthRel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    int insertSelective(UserAuthRel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    List<UserAuthRel> selectByExampleWithRowbounds(UserAuthRelExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    List<UserAuthRel> selectByExample(UserAuthRelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    UserAuthRel selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    int updateByExampleSelective(@Param("record") UserAuthRel record, @Param("example") UserAuthRelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    int updateByExample(@Param("record") UserAuthRel record, @Param("example") UserAuthRelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    int updateByPrimaryKeySelective(UserAuthRel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_auth_rel
     *
     * @mbg.generated Wed Dec 29 01:41:58 CST 2021
     */
    int updateByPrimaryKey(UserAuthRel record);
}