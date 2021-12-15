package life.majiang.community.community.controller;


import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GiteeAccessTokenDTO;
import life.majiang.community.community.dto.GiteeUser;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.model.User;
import life.majiang.community.community.provider.GiteeProvider;
import life.majiang.community.community.provider.GithubProvider;
import life.majiang.community.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private GiteeProvider giteeProvider;

    @Autowired
    private UserService userService;

    @Value("${github.client.id}")
    private String githubClientId;
    @Value("${github.client.secret}")
    private String githubClientSecret;
    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

    @Value("${gitee.client.id}")
    private String giteeClientId;
    @Value("${gitee.client.secret}")
    private String giteeClientSecret;
    @Value("${gitee.redirect.uri}")
    private String giteeRedirectUri;

    @RequestMapping("/login")
    public String login(){
        System.out.println("进入login");
        return "redirect:/" ;
    }
    @GetMapping("/giteeCallback")
    public String giteeCallback(@RequestParam("code")String code,
                                @RequestParam(name="state",required = false)String state,
                                HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(giteeRedirectUri);
        accessTokenDTO.setClient_id(giteeClientId);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_secret(giteeClientSecret);
        accessTokenDTO.setGrant_type("authorization_code");

        GiteeAccessTokenDTO giteeAccessTokenDTO = giteeProvider.getAccessToken(accessTokenDTO);

        System.out.println("giteeAccessTokenDTO = " + giteeAccessTokenDTO);

        //通过accessToken获取giiEE用户信息:name id avatarUrl
        GiteeUser giteeUser = giteeProvider.getUser(giteeAccessTokenDTO.getAccess_token());
        System.out.println(giteeUser);


        if(giteeUser != null){
            String token = UUID.randomUUID().toString();
            userService.createOrUpdateGiteeUser(giteeUser,giteeAccessTokenDTO,token);
            response.addCookie(new Cookie("token",token));
        }
        else {
            //登陆失败
            log.error("callbakck get githee error,{}" , giteeUser );
        }
        return "redirect:/";
    }


    @GetMapping("/callback")
    public String callback(@RequestParam(name="code")String code,
                           @RequestParam(name="state")String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(githubRedirectUri);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(githubClientId);
        accessTokenDTO.setClient_secret(githubClientSecret);

        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        System.out.println(githubUser.getName());

        if(githubUser != null){

            User user = new User();
            String token = UUID.randomUUID().toString();
            System.out.println("---------------------------------");
            System.out.println("token = " + token);
            System.out.println("---------------------------------");
            user.setToken(token);
            //此处的token有两种情况
            //当新建用户：       会将此token保存到用户表中
            //当更新用户信息：   会将此token作为新的token更新到用户表中，以便于保持登录态
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatarUrl());
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            //userService.createOrUpdateGihubUser(user,);
            userService.createOrUpdate(user);

            response.addCookie(new Cookie("token",token));

            //登陆成功，写cookie 和 session
            //request.getSession().setAttribute("user",user);//此处穿uesr会报错 会导致登陆后user.id=null 怀疑时githubUser.id 类型是long 二user.id未Integer
            return "redirect:/";

        }else {
            //登陆失败
            log.error("callbakc get github error,{}" , githubUser );
            return "redirect:/";
        }
        //System.out.println(user.getName());
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("users");
        request.getSession().removeAttribute("unreadMessage");
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

}
