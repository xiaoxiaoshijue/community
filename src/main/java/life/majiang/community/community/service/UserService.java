package life.majiang.community.community.service;

import life.majiang.community.community.dto.GiteeAccessTokenDTO;
import life.majiang.community.community.dto.GiteeUser;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.mapper.UserAuthRelMapper;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.mapper.UserThirdAuthMapper;
import life.majiang.community.community.mapper.UsersMapper;
import life.majiang.community.community.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserThirdAuthMapper userThirdAuthMapper;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UserAuthRelMapper userAuthRelMapper;

    public void createOrUpdate(User user){
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(new UserExample());
        if(users.size() == 0){
            //不存在此用户插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            //更新
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setName(user.getName());
            user.setAvatarUrl(user.getAvatarUrl());
/*
            userMapper.update(user);
*/
            User updateUser = new User();
            updateUser.setGmtCreate(System.currentTimeMillis());
            updateUser.setGmtModified(user.getGmtCreate());
            updateUser.setName(user.getName());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            //传入updateUser会产生bug----
            UserExample userExample1 = new UserExample();
            userExample1.createCriteria().
                    andAccountIdEqualTo(user.getAccountId());
            userMapper.updateByExampleSelective(user, userExample1);
        }
    }

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
//      如果不是第一次登录 需要在user_third_auth表中更新access_token 和 expire_in 和 refresh_token
            List<UserThirdAuth> dbUserThirdAuth = userThirdAuthMapper.selectByExample(userThirdAuthExample);
            userThirdAuth.setAuthId(dbUserThirdAuth.get(0).getAuthId());
            UserThirdAuthExample thirdAuthExample = new UserThirdAuthExample();
            thirdAuthExample.createCriteria()
                    .andOpenidEqualTo(giteeUser.getId())
                    .andLoginTypeEqualTo(userThirdAuth.getLoginType());
            userThirdAuthMapper.updateByExample(userThirdAuth,thirdAuthExample);
//      还需要在user表中更新token
            UsersExample usersExample = new UsersExample();
            usersExample.createCriteria().andAvatarUrlEqualTo(giteeUser.getAvatar_url());
            List<Users> dbUsers = usersMapper.selectByExample(usersExample);
            Users users = new Users();
            users.setToken(token);
            users.setUserId(dbUsers.get(0).getUserId());
            users.setAvatarUrl(giteeUser.getAvatar_url());
            users.setUserName(giteeUser.getName());
            UsersExample usersExample1 = new UsersExample();
            usersMapper.updateByPrimaryKeySelective(users);
        }
    }

}
