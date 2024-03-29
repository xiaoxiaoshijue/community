package life.majiang.community.community.model;

public class UserThirdAuth {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_third_auth.auth_id
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    private Long authId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_third_auth.openid
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    private Integer openid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_third_auth.login_type
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    private String loginType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_third_auth.access_token
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    private String accessToken;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_third_auth.expire_in
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    private Long expireIn;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_third_auth.refresh_token
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    private String refreshToken;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_third_auth.auth_id
     *
     * @return the value of user_third_auth.auth_id
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public Long getAuthId() {
        return authId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_third_auth.auth_id
     *
     * @param authId the value for user_third_auth.auth_id
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_third_auth.openid
     *
     * @return the value of user_third_auth.openid
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public Integer getOpenid() {
        return openid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_third_auth.openid
     *
     * @param openid the value for user_third_auth.openid
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public void setOpenid(Integer openid) {
        this.openid = openid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_third_auth.login_type
     *
     * @return the value of user_third_auth.login_type
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public String getLoginType() {
        return loginType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_third_auth.login_type
     *
     * @param loginType the value for user_third_auth.login_type
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public void setLoginType(String loginType) {
        this.loginType = loginType == null ? null : loginType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_third_auth.access_token
     *
     * @return the value of user_third_auth.access_token
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_third_auth.access_token
     *
     * @param accessToken the value for user_third_auth.access_token
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken == null ? null : accessToken.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_third_auth.expire_in
     *
     * @return the value of user_third_auth.expire_in
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public Long getExpireIn() {
        return expireIn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_third_auth.expire_in
     *
     * @param expireIn the value for user_third_auth.expire_in
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public void setExpireIn(Long expireIn) {
        this.expireIn = expireIn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_third_auth.refresh_token
     *
     * @return the value of user_third_auth.refresh_token
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_third_auth.refresh_token
     *
     * @param refreshToken the value for user_third_auth.refresh_token
     *
     * @mbg.generated Wed Feb 23 23:32:30 CST 2022
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken == null ? null : refreshToken.trim();
    }
}