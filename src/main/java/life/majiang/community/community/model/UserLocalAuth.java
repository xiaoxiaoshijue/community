package life.majiang.community.community.model;

public class UserLocalAuth {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_local_auth.auth_id
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    private Integer authId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_local_auth.user_name
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    private String userName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_local_auth.user_password
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    private String userPassword;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_local_auth.mobile
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    private Integer mobile;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_local_auth.auth_id
     *
     * @return the value of user_local_auth.auth_id
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    public Integer getAuthId() {
        return authId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_local_auth.auth_id
     *
     * @param authId the value for user_local_auth.auth_id
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    public void setAuthId(Integer authId) {
        this.authId = authId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_local_auth.user_name
     *
     * @return the value of user_local_auth.user_name
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    public String getUserName() {
        return userName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_local_auth.user_name
     *
     * @param userName the value for user_local_auth.user_name
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_local_auth.user_password
     *
     * @return the value of user_local_auth.user_password
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_local_auth.user_password
     *
     * @param userPassword the value for user_local_auth.user_password
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword == null ? null : userPassword.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_local_auth.mobile
     *
     * @return the value of user_local_auth.mobile
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    public Integer getMobile() {
        return mobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_local_auth.mobile
     *
     * @param mobile the value for user_local_auth.mobile
     *
     * @mbg.generated Fri Oct 08 07:36:31 CST 2021
     */
    public void setMobile(Integer mobile) {
        this.mobile = mobile;
    }
}