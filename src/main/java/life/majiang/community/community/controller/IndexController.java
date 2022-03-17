package life.majiang.community.community.controller;


import life.majiang.community.community.dto.PaginationDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.Users;
import life.majiang.community.community.provider.Constant;
import life.majiang.community.community.provider.JwtUtil;
import life.majiang.community.community.provider.PassToken;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private QuestionService questionService;
        @PassToken
        @RequestMapping("/")
        public String index(Model model,
                            @RequestParam(name = "page",defaultValue = "1")Integer page,
                            @RequestParam(name = "size",defaultValue = "2")Integer size,
                            @RequestParam(name = "search",required = false)String search,
                            @ModelAttribute("msg")String msg,
                            @ModelAttribute("result")String result) {
                if(msg == "register"){
                model.addAttribute("msg","register");
            }if(result != null && !result.equals("")){
                model.addAttribute("result",result);
            }
            PaginationDTO<QuestionDTO> pagination = questionService.list(search,page,size);
            QuestionDTO redisQuestion = questionService.listFromRedis(Constant.TOPPING_QUESTION_ID);


            //System.out.println(pagination.toString());
            model.addAttribute("pagination",pagination);
            model.addAttribute("search",search);
            model.addAttribute("redisQuestion",redisQuestion);
            return "index";
        }
   /* public String hello(@RequestParam(name = "name") String name, Model model){
        model.addAttribute("name",name);
        return "index";
    }*/
    @PassToken
    @ResponseBody
    @RequestMapping("/token")
    public String getToken(){
       Users users = new Users();
       users.setUserName("SJJ");
       users.setUserId(2L);
       return JwtUtil.createToken(users);
    }
}
