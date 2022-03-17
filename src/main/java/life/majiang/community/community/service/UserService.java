package life.majiang.community.community.service;

import life.majiang.community.community.dto.GiteeAccessTokenDTO;
import life.majiang.community.community.dto.GiteeUser;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.exception.ErrorCodeEnum;
import life.majiang.community.community.exception.CustomizeException;
import life.majiang.community.community.mapper.UserAuthRelMapper;
import life.majiang.community.community.mapper.UserLocalAuthMapper;
import life.majiang.community.community.mapper.UserThirdAuthMapper;
import life.majiang.community.community.mapper.UsersMapper;
import life.majiang.community.community.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserThirdAuthMapper userThirdAuthMapper;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UserAuthRelMapper userAuthRelMapper;
    @Autowired
    private UserLocalAuthMapper userLocalAuthMapper;


    public void createOrUpdateGiteeUser(GiteeUser giteeUser, GiteeAccessTokenDTO giteeAccessTokenDTO, String token) {
        UserThirdAuth userThirdAuth = new UserThirdAuth();
        userThirdAuth.setAccessToken(giteeAccessTokenDTO.getAccess_token());
        userThirdAuth.setLoginType("gitee");
        userThirdAuth.setOpenid(giteeUser.getId());
        userThirdAuth.setRefreshToken(giteeAccessTokenDTO.getRefresh_token());
        userThirdAuth.setExpireIn(System.currentTimeMillis() + giteeAccessTokenDTO.getExpires_in());
//      首先查询第三方用户表 看是否是第一次登录
        UserThirdAuthExample userThirdAuthExample = new UserThirdAuthExample();
        userThirdAuthExample.createCriteria()
                .andOpenidEqualTo(giteeUser.getId())
                .andLoginTypeEqualTo("gitee");
        List<UserThirdAuth> isThirdUsersFirstLoginIn = userThirdAuthMapper.selectByExample(userThirdAuthExample);
//一、      如果是第一次登录，并且账号无绑定信息
        if(isThirdUsersFirstLoginIn.size() == 0){
//      1、把授权信息保存在user_third_auth表中
            userThirdAuthMapper.insert(userThirdAuth);
            List<UserThirdAuth> dbUserThirdAuth = userThirdAuthMapper.selectByExample(userThirdAuthExample);
//      2、如果是第一次登录    把第三方信息保存到Users表中
            Users users = new Users();
            users.setToken(token);
            users.setCreateTime(System.currentTimeMillis());
            users.setAvatarUrl(giteeUser.getAvatar_url());
            users.setUserName(giteeUser.getName());
            usersMapper.insert(users);

            UsersExample usersExample = new UsersExample();
            usersExample.createCriteria().andAvatarUrlEqualTo(giteeUser.getAvatar_url());
            List<Users> dbUsers = usersMapper.selectByExample(usersExample);
/*//      然后新建用户表保存第三方用户信息
            userThirdAuthMapper.insert(userThirdAuth);*/
//      3、获取Users表的id 和 Auth_Third的id 存入rel 关系标总
            UserAuthRel userAuthRel = new UserAuthRel();
            userAuthRel.setUserId(dbUsers.get(0).getUserId());
            userAuthRel.setAuthId(dbUserThirdAuth.get(0).getAuthId());
            userAuthRel.setAuthType("third");
            userAuthRelMapper.insert(userAuthRel);
        }else {
//二、      如果不是第一次登录 需要在user_third_auth表中更新access_token 和 expire_in 和 refresh_token
            //根据authid
            List<UserThirdAuth> dbUserThirdAuth = userThirdAuthMapper.selectByExample(userThirdAuthExample);
            userThirdAuth.setAuthId(dbUserThirdAuth.get(0).getAuthId());
            UserThirdAuthExample thirdAuthExample = new UserThirdAuthExample();
            thirdAuthExample.createCriteria()
                    .andOpenidEqualTo(giteeUser.getId())
                    .andLoginTypeEqualTo(userThirdAuth.getLoginType());
            userThirdAuthMapper.updateByExample(userThirdAuth,thirdAuthExample);
//      还需要在user表中更新token
            //先根据openid type 在rel表中获取userid
            UserAuthRelExample userAuthRelExample = new UserAuthRelExample();
            userAuthRelExample.createCriteria()
                    .andAuthIdEqualTo(dbUserThirdAuth.get(0).getAuthId())
                    .andAuthTypeEqualTo("third");
            List<UserAuthRel> dbRel = userAuthRelMapper.selectByExample(userAuthRelExample);
            if(dbRel == null){
                throw new CustomizeException(ErrorCodeEnum.AUTH_NOT_FOUND);
            }
            UsersExample usersExample = new UsersExample();
            usersExample.createCriteria().andUserIdEqualTo(dbRel.get(0).getUserId());
            List<Users> dbUsers = usersMapper.selectByExample(usersExample);
            if(dbUsers == null){
                throw new CustomizeException(ErrorCodeEnum.DB_USER_NOT_FOUND);
            }
            //如果users信息第一次保存的是第三方
            if(dbUsers.get(0).getUserName().equals(giteeUser.getName())){
                Users users = new Users();
                users.setToken(token);
                users.setUserId(dbUsers.get(0).getUserId());
                users.setAvatarUrl(giteeUser.getAvatar_url());
                users.setUserName(giteeUser.getName());
                usersMapper.updateByPrimaryKeySelective(users);
            }else {
                Users users = new Users();
                users.setToken(token);
                users.setUserId(dbUsers.get(0).getUserId());
                usersMapper.updateByPrimaryKeySelective(users);
            }
            //如果users信息第一次保存的是本地用户 则第三方为绑定用户 不需要更新
        }
//    三、如果是第一个登录并且账号存在绑定信息
    }
    public Boolean addGiteeUser(GiteeUser giteeUser, GiteeAccessTokenDTO giteeAccessTokenDTO,Users users) {
        UserThirdAuth userThirdAuth = new UserThirdAuth();
        userThirdAuth.setAccessToken(giteeAccessTokenDTO.getAccess_token());
        userThirdAuth.setLoginType("gitee");
        userThirdAuth.setOpenid(giteeUser.getId());
        userThirdAuth.setRefreshToken(giteeAccessTokenDTO.getRefresh_token());
        userThirdAuth.setExpireIn(System.currentTimeMillis() + giteeAccessTokenDTO.getExpires_in());
//      首先查询第三方用户表 看是否账号已绑定
        UserThirdAuthExample userThirdAuthExample = new UserThirdAuthExample();
        userThirdAuthExample.createCriteria()
                .andOpenidEqualTo(giteeUser.getId())
                .andLoginTypeEqualTo("gitee");
        List<UserThirdAuth> isThirdUsersFirstLoginIn = userThirdAuthMapper.selectByExample(userThirdAuthExample);
//一、      如果是并且账号无绑定信息
        if(isThirdUsersFirstLoginIn.size() == 0){
//      1、把授权信息保存在user_third_auth表中
            userThirdAuthMapper.insert(userThirdAuth);
            List<UserThirdAuth> dbUserThirdAuth = userThirdAuthMapper.selectByExample(userThirdAuthExample);
            UsersExample usersExample = new UsersExample();
            usersExample.createCriteria().andUserNameEqualTo(users.getUserName());
            List<Users> dbUsers = usersMapper.selectByExample(usersExample);
/*//      然后新建用户表保存第三方用户信息
            userThirdAuthMapper.insert(userThirdAuth);*/
//      3、获取Users表的id 和 Auth_Third的id 存入rel 关系标总
            UserAuthRel userAuthRel = new UserAuthRel();
            userAuthRel.setUserId(dbUsers.get(0).getUserId());
            userAuthRel.setAuthId(dbUserThirdAuth.get(0).getAuthId());
            userAuthRel.setAuthType("third");
            userAuthRelMapper.insert(userAuthRel);
            return true;
        }
//      如果此账号已经被绑定
        else {
            return false;
        }
    }
public Boolean addGithubUser(GithubUser githubUser, String access_token,Users users) {
        UserThirdAuth userThirdAuth = new UserThirdAuth();
        userThirdAuth.setAccessToken(access_token);
        userThirdAuth.setLoginType("github");
        userThirdAuth.setOpenid(githubUser.getId());
//      首先查询第三方用户表 看是否账号已绑定
        UserThirdAuthExample userThirdAuthExample = new UserThirdAuthExample();
        userThirdAuthExample.createCriteria()
                .andOpenidEqualTo(githubUser.getId())
                .andLoginTypeEqualTo("github");
        List<UserThirdAuth> isThirdUsersFirstLoginIn = userThirdAuthMapper.selectByExample(userThirdAuthExample);
//一、      如果是并且账号无绑定信息
        if(isThirdUsersFirstLoginIn.size() == 0){
//      1、把授权信息保存在user_third_auth表中
            userThirdAuthMapper.insert(userThirdAuth);
            List<UserThirdAuth> dbUserThirdAuth = userThirdAuthMapper.selectByExample(userThirdAuthExample);
            UsersExample usersExample = new UsersExample();
            usersExample.createCriteria().andUserNameEqualTo(users.getUserName());
            List<Users> dbUsers = usersMapper.selectByExample(usersExample);
/*//      然后新建用户表保存第三方用户信息
            userThirdAuthMapper.insert(userThirdAuth);*/
//      3、获取Users表的id 和 Auth_Third的id 存入rel 关系标总
            UserAuthRel userAuthRel = new UserAuthRel();
            userAuthRel.setUserId(dbUsers.get(0).getUserId());
            userAuthRel.setAuthId(dbUserThirdAuth.get(0).getAuthId());
            userAuthRel.setAuthType("third");
            userAuthRelMapper.insert(userAuthRel);
            return true;
        }
//      如果此账号已经被绑定
        else {
            return false;
        }
    }




    public void createOrUpdateGithubUser(GithubUser githubUser, String token, String access_token) {
        UserThirdAuth userThirdAuth = new UserThirdAuth();
        userThirdAuth.setAccessToken(access_token);
        userThirdAuth.setLoginType("github");
        userThirdAuth.setOpenid(githubUser.getId());
        //userThirdAuth.setRefreshToken(giteeAccessTokenDTO.getRefresh_token());
        userThirdAuth.setExpireIn(3600L);
//      首先查询第三方用户表 看是否是第一次登录
        UserThirdAuthExample userThirdAuthExample = new UserThirdAuthExample();
        userThirdAuthExample.createCriteria()
                .andOpenidEqualTo(githubUser.getId())
                .andLoginTypeEqualTo("github");
        List<UserThirdAuth> isThirdUsersFirstLoginIn = userThirdAuthMapper.selectByExample(userThirdAuthExample);
//      如果是第一次登录
        if(isThirdUsersFirstLoginIn.size() == 0){
//      1、把授权信息保存在user_third_auth表中
            userThirdAuthMapper.insert(userThirdAuth);
            List<UserThirdAuth> dbUserThirdAuth = userThirdAuthMapper.selectByExample(userThirdAuthExample);
//      2、如果是第一次登录    把第三方信息保存到Users表中
            Users users = new Users();
            users.setToken(token);
            users.setCreateTime(System.currentTimeMillis());
            users.setAvatarUrl(githubUser.getAvatarUrl());
            users.setUserName(githubUser.getName());
            usersMapper.insert(users);

            UsersExample usersExample = new UsersExample();
            usersExample.createCriteria().andAvatarUrlEqualTo(githubUser.getAvatarUrl());
            List<Users> dbUsers = usersMapper.selectByExample(usersExample);
/*//      然后新建用户表保存第三方用户信息
            userThirdAuthMapper.insert(userThirdAuth);*/
//      3、获取Users表的id 和 Auth_Third的id 存入rel 关系标总
            UserAuthRel userAuthRel = new UserAuthRel();
            userAuthRel.setUserId(dbUsers.get(0).getUserId());
            userAuthRel.setAuthId(dbUserThirdAuth.get(0).getAuthId());
            userAuthRel.setAuthType("third");
            userAuthRelMapper.insert(userAuthRel);
        }else {
//      如果不是第一次登录 需要在user_third_auth表中更新access_token 和 expire_in 和 refresh_token
            List<UserThirdAuth> dbUserThirdAuth = userThirdAuthMapper.selectByExample(userThirdAuthExample);
            userThirdAuth.setAuthId(dbUserThirdAuth.get(0).getAuthId());
            UserThirdAuthExample thirdAuthExample = new UserThirdAuthExample();
            thirdAuthExample.createCriteria()
                    .andOpenidEqualTo(githubUser.getId())
                    .andLoginTypeEqualTo(userThirdAuth.getLoginType());
            userThirdAuthMapper.updateByExample(userThirdAuth,thirdAuthExample);
//      还需要在user表中更新token
            UsersExample usersExample = new UsersExample();
            usersExample.createCriteria().andAvatarUrlEqualTo(githubUser.getAvatarUrl());
            List<Users> dbUsers = usersMapper.selectByExample(usersExample);
            Users users = new Users();
            users.setToken(token);
            users.setUserId(dbUsers.get(0).getUserId());
            users.setAvatarUrl(githubUser.getAvatarUrl());
            users.setUserName(githubUser.getName());
            UsersExample usersExample1 = new UsersExample();
            usersMapper.updateByPrimaryKeySelective(users);
        }
    }

    public String createOrUpdateLocalUser(UserLocalAuth localUser) {
        //1、将用户信息放入local表
        userLocalAuthMapper.insert(localUser);
        //2、将用户信息放入users表
        Users users = new Users();
        users.setCreateTime(System.currentTimeMillis());
        users.setAvatarUrl("/images/v2-03b55bc9f4f01425ba0abd9add009e25_r.jpg");
        users.setUserName(localUser.getUserName());
        users.setMobile(localUser.getMobile());
        usersMapper.insert(users);  
        //3、查询local表和 users表的 主键id 用rel来关联绑定关系
        /**
         * 后续可以通过generator.xml配置 让insert()返回主键值
         *
         * <table tableName="user_local_auth" domainObjectName="UserLocalAuth">
         *          <generatedKey column="id" sqlStatement="MySql" identity="true"/>
         * </table>
         */
        //查询local 的主键id
         UserLocalAuthExample userLocalAuthExample2 = new UserLocalAuthExample();
         userLocalAuthExample2.createCriteria()
                 .andUserNameEqualTo(localUser.getUserName())
                 .andUserPasswordEqualTo(localUser.getUserPassword());
         List<UserLocalAuth> dbLocalUsers = userLocalAuthMapper.selectByExample(userLocalAuthExample2);
         Long authId = dbLocalUsers.get(0).getAuthId();
         //查询users 的主键id
         UsersExample usersExample2 = new UsersExample();
         usersExample2.createCriteria()
                 .andUserNameEqualTo(localUser.getUserName());
         List<Users> dbUsers = usersMapper.selectByExample(usersExample2);
         Long userId = dbUsers.get(0).getUserId();
         //建立关系 将结果插入rel表
         UserAuthRel userAuthRel = new UserAuthRel();
         userAuthRel.setUserId(userId);
         userAuthRel.setAuthId(authId);
         userAuthRel.setAuthType("local");
         userAuthRelMapper.insert(userAuthRel);
         return "注册成功 您的用户 id 是：" + userId + "用户名是：" + localUser.getUserName();

    }
    public boolean isUserNameExist(String userName){
        //先查询是否存在此用户 如果存在则返回注册失败信息
        UserLocalAuthExample userLocalAuthExample1 =
                new UserLocalAuthExample();
        userLocalAuthExample1
                .createCriteria()
                .andUserNameEqualTo(userName);
        List<UserLocalAuth> userLocalAuths = userLocalAuthMapper.selectByExample(userLocalAuthExample1);
        if(userLocalAuths.size() != 0){
            return false;
        }
        return true;
    }
    public String login(String loginUserName, String loginUserPassword, String token) {
        UserLocalAuthExample userLocalAuthExample1 = new UserLocalAuthExample();
        userLocalAuthExample1.createCriteria()
                .andUserNameEqualTo(loginUserName)
                .andUserPasswordEqualTo(loginUserPassword);
        List<UserLocalAuth> localUser = userLocalAuthMapper.selectByExample(userLocalAuthExample1);
        if(localUser.size() != 0){
            //获取用户auth_id 在rel表中找到user_id 在users表中加入token
            Long auth_id = localUser.get(0).getAuthId();
            UserAuthRelExample userAuthRelExample = new UserAuthRelExample();
            userAuthRelExample.createCriteria()
                    .andAuthIdEqualTo(auth_id)
                    .andAuthTypeEqualTo("local");
            Long user_id = userAuthRelMapper.selectByExample(userAuthRelExample).get(0).getUserId();
            Users users = new Users();
            users.setToken(token);
            users.setUserId(user_id);
            //int R =
                    usersMapper.updateByPrimaryKeySelective(users);
            return "登录成功";
        }else {
            return "账号或者密码错误，登录失败";
        }
    }

    /**
     * 本质上在rel表中建立关系
     * @param userLocalAuth 本地用户
     * @param userThirdAuth 第三方用户
     * @param usersId 要绑定的用户id
     * @return
     */
    public String bindAccount(UserLocalAuth userLocalAuth,
                              UserThirdAuth userThirdAuth,
                              Long usersId){
        UserAuthRel userAuthRel = new UserAuthRel();
        if(userLocalAuth == null){
            userAuthRel.setAuthId(userThirdAuth.getAuthId());
            userAuthRel.setAuthType(userThirdAuth.getLoginType());
            userAuthRel.setUserId(usersId);
            userAuthRelMapper.insert(userAuthRel);
        }else if(userThirdAuth == null){
            userAuthRel.setAuthId(userLocalAuth.getAuthId());
            userAuthRel.setAuthType("local");
            userAuthRel.setUserId(usersId);
            userLocalAuthMapper.insert(userLocalAuth);
        }

        return "1";
    }

    /**
     * 获取users下所有第三方账户
     */
    public List<UserThirdAuth> getAllUsers(Users users) {
        List<UserThirdAuth> returnList = new LinkedList<>();
        UsersExample usersExample = new UsersExample();
        usersExample.createCriteria()
                .andUserNameEqualTo(users.getUserName());
        List<Users> dbUsers = usersMapper.selectByExample(usersExample);
        if(dbUsers != null && dbUsers.get(0) != null){
            UserAuthRelExample userAuthRelExample = new UserAuthRelExample();
            userAuthRelExample.createCriteria()
                    .andUserIdEqualTo(dbUsers.get(0).getUserId())
                    .andAuthTypeEqualTo("third");
            List<UserAuthRel> dbUserAuthRel = userAuthRelMapper.selectByExample(userAuthRelExample);
            Iterator iterator = dbUserAuthRel.iterator();
            while (iterator.hasNext()){
                UserAuthRel userAuthRel = (UserAuthRel) iterator.next();
                UserThirdAuthExample userThirdAuthExample = new UserThirdAuthExample();
                userThirdAuthExample.createCriteria()
                        .andAuthIdEqualTo(userAuthRel.getAuthId());
                List<UserThirdAuth> dbuserThirdAuth = userThirdAuthMapper.selectByExample(userThirdAuthExample);
                if(dbuserThirdAuth != null && dbuserThirdAuth.get(0) != null){
                    returnList.add(dbuserThirdAuth.get(0));
                }
            }
            return returnList;
        }
        return null;
    }

    /**
     * 删除对应authId authType的第三方用户数据
     * 设计 userThirdAuth 和 userAuthRel表
     */
    public String delUserByAuthIdAndAuthType(Long authId, String authType) {
        UserThirdAuthExample userThirdAuthExample = new UserThirdAuthExample();
        userThirdAuthExample.createCriteria().andAuthIdEqualTo(authId);
        int i = userThirdAuthMapper.deleteByExample(userThirdAuthExample);

        UserAuthRelExample userAuthRelExample = new UserAuthRelExample();
        userAuthRelExample.createCriteria().andAuthIdEqualTo(authId).andAuthTypeEqualTo("third");
        int i1 = userAuthRelMapper.deleteByExample(userAuthRelExample);
        if(i == 1 && i1 == 1){
            return "删除成功";
        }
        return "删除失败";
    }


    public Users getUserByToken(String token){
        UsersExample usersExample = new UsersExample();
        usersExample.createCriteria()
                .andTokenEqualTo(token);
        List<Users> users = usersMapper.selectByExample(usersExample);
        return users.get(0);
    }
}
