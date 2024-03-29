package life.majiang.community.community.mapper;

import java.util.List;
import life.majiang.community.community.model.UserThirdAuth;
import life.majiang.community.community.model.UserThirdAuthExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface UserThirdAuthMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    long countByExample(UserThirdAuthExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    int deleteByExample(UserThirdAuthExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    int deleteByPrimaryKey(Long authId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    int insert(UserThirdAuth record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    int insertSelective(UserThirdAuth record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    List<UserThirdAuth> selectByExampleWithRowbounds(UserThirdAuthExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    List<UserThirdAuth> selectByExample(UserThirdAuthExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    UserThirdAuth selectByPrimaryKey(Long authId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    int updateByExampleSelective(@Param("record") UserThirdAuth record, @Param("example") UserThirdAuthExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    int updateByExample(@Param("record") UserThirdAuth record, @Param("example") UserThirdAuthExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    int updateByPrimaryKeySelective(UserThirdAuth record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_third_auth
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    int updateByPrimaryKey(UserThirdAuth record);
}