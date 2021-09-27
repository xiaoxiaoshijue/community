package life.majiang.community.community.controller;


import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.model.User;
import life.majiang.community.community.provider.GithubProvider;
import life.majiang.community.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    private UserService userService;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code")String code,
                           @RequestParam(name="state")String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirct_uri(redirectUri);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);

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
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

}
