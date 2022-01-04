package life.majiang.community.community.controller;


import life.majiang.community.community.dto.*;
import life.majiang.community.community.model.UserLocalAuth;
import life.majiang.community.community.model.Users;
import life.majiang.community.community.provider.GiteeProvider;
import life.majiang.community.community.provider.GithubProvider;
import life.majiang.community.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
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


    @GetMapping("/giteeCallback")
    public String giteeCallback(@RequestParam("code")String code,
                                @RequestParam(name="state",required = false)String state,
                                HttpServletResponse response,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request){
        GiteeAccessTokenDTO giteeAccessTokenDTO = giteeProvider.getAccessToken(code, state);
        GiteeUser giteeUser = giteeProvider.getUser(giteeAccessTokenDTO.getAccess_token());//通过accessToken获取giiEE用户信息:name id avatarUrl
        if(giteeUser != null){
            Object isLogin = request.getSession().getAttribute("users");
            System.out.println(isLogin);
            //没登陆 进行登录操作
            if(isLogin == null || isLogin.equals("")){
                String token = UUID.randomUUID().toString();
                //giteeProvider.login(giteeUser,giteeAccessTokenDTO,token);
                userService.createOrUpdateGiteeUser(giteeUser,giteeAccessTokenDTO,token);
                response.addCookie(new Cookie("token",token));
            }
            //已经登录了 进行绑定操作
            else {
                Users users = (Users)request.getSession().getAttribute("users");
                if(!userService.addGiteeUser(giteeUser,giteeAccessTokenDTO,users)){
                    redirectAttributes.addFlashAttribute("msg","此账号已被绑定");
                }else {
                    redirectAttributes.addFlashAttribute("msg","账号绑定成功，您可以使用此账号登录您的账户");
                }
                return "redirect:/center/account";
            }
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
                           RedirectAttributes redirectAttributes,
                           HttpServletResponse response,
                           HttpServletRequest request){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(githubRedirectUri);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(githubClientId);
        accessTokenDTO.setClient_secret(githubClientSecret);

        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);

        if(githubUser != null){
            //user.setToken(token);
            //此处的token有两种情况
            //当新建用户：       会将此token保存到用户表中
            //当更新用户信息：   会将此token作为新的token更新到用户表中，以便于保持登录态
            //user.setName(githubUser.getName());
            //user.setAccountId(String.valueOf(githubUser.getId()));
            //user.setAvatarUrl(githubUser.getAvatarUrl());
            //user.setGmtCreate(System.currentTimeMillis());
            //user.setGmtModified(user.getGmtCreate());
            Object isLogin =  request.getSession().getAttribute("users");
            if(isLogin == null || isLogin.equals("")){
                String token = UUID.randomUUID().toString();
                userService.createOrUpdateGithubUser(githubUser,token,accessToken);
                response.addCookie(new Cookie("token",token));
            }
            //已经登录了 进行绑定操作
            else {
                Users users = (Users)request.getSession().getAttribute("users");
                if(!userService.addGithubUser(githubUser,accessToken,users)){
                    redirectAttributes.addFlashAttribute("msg","此账号已被绑定");
                }else {
                    redirectAttributes.addFlashAttribute("msg","账号绑定成功，您可以使用此账号登录您的账户");
                }
                return "redirect:/center/account";
            }
            //userService.createOrUpdate(user);
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
    @PostMapping("/register/{username}/{password}/{mobile}")
    //@ResponseBody
    public String register(
                           Model model,
                           @PathVariable(value = "username")String userName,
                           @PathVariable(value = "password")String userPassword,
                           @PathVariable(value = "mobile")String mobile,
                           RedirectAttributes redirectAttributes
                           ){
//        if(){
//              如果验证码错误 直接返回错误信息
//        }


        UserLocalAuth userLocalAuth = new UserLocalAuth();
        userLocalAuth.setMobile(mobile);
        userLocalAuth.setUserName(userName);
        userLocalAuth.setUserPassword(userPassword);
        String result = userService.createOrUpdateLocalUser(userLocalAuth);
        model.addAttribute("login",true);

        redirectAttributes.addFlashAttribute("result",result);
        //redirectAttributes.addAttribute("registermsg","registerSuccess");
        return "redirect:/";
    }
    @GetMapping("/register")
    public String toRegister(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("msg","register");
        return "redirect:/";
    }
    @ResponseBody
    @PostMapping("/register/isUserNameExit")
    public String isUserNameExist(@RequestBody Map<String,String> datas){
        if("".equals(datas.get("userName"))){
            return "用户名不能为空";
        }
        //验证用户名是否重复
        if(!userService.isUserNameExist(datas.get("userName"))){
            return "用户名重复";
        }
        return "用户名可用";
    }
    @RequestMapping("/toLogin")
    public String toLogin(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("msg","login");
        return "redirect:/";
    }
    @ResponseBody
    @PostMapping("/login")
    public String login(@RequestBody Map<String,String> datas,
                        HttpServletResponse response){
        String token = UUID.randomUUID().toString();
        String loginUserName = datas.get("loginUserName");
        String loginPassword = datas.get("loginPassword");
        String result = userService.login(loginUserName,loginPassword,token);
        if("登录成功".equals(result)){
            response.addCookie(new Cookie("token",token));
            return result;
        }else {
            return result;
        }
    }
}
