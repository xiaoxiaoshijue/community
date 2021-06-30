package life.majiang.community.community.controller;


import life.majiang.community.community.dto.PaginationDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

        @GetMapping("/")
        public String index(HttpServletRequest request,
                            Model model,
                            @RequestParam(name = "page",defaultValue = "1")Integer page,
                            @RequestParam(name = "size",defaultValue = "2")Integer size ) {
            //登陆状态持久化操作
            Cookie[] cookies = request.getCookies();
            if(cookies != null && cookies.length != 0){
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
            //列表问题功能
            PaginationDTO pagination = questionService.list(page,size);
            System.out.println(pagination.toString());
            model.addAttribute("pagination",pagination);
            return "index";
        }
   /* public String hello(@RequestParam(name = "name") String name, Model model){
        model.addAttribute("name",name);
        return "index";
    }*/
}
