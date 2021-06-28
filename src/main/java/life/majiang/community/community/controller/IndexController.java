package life.majiang.community.community.controller;


import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    @Autowired
    private UserMapper userMapper;
        @GetMapping("/")
        public String index(HttpServletRequest request) {
            //登陆状态持久化操作
            Cookie[] cookies = request.getCookies();
            if(cookies != null){
                for (Cookie cookie : cookies) {
                    if("token".equalsIgnoreCase(cookie.getName())){
                        String token = cookie.getValue();           //拿到token
                        User user = userMapper.findByToken(token);  //检查数据库中是否存在此token
                        if(user != null){
                            request.getSession().setAttribute("user",user);   //存在此token 把user信息写到Session里去 实现登录持久化
                        }
                    }
                }
            }
            else {
                System.out.println("cookies = null 失败");
            }
            return "index";
        }
   /* public String hello(@RequestParam(name = "name") String name, Model model){
        model.addAttribute("name",name);
        return "index";
    }*/
}
