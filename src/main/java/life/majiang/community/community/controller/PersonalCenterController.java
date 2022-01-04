package life.majiang.community.community.controller;

import life.majiang.community.community.mapper.UsersMapper;
import life.majiang.community.community.model.UserThirdAuth;
import life.majiang.community.community.model.Users;
import life.majiang.community.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PersonalCenterController {
    @Autowired
    private UserService userService;
    @RequestMapping("/center/{action}")
    public String center(HttpServletRequest request,
                         Model model,
                         @ModelAttribute("msg")String msg,
                         @PathVariable(name = "action")String action){
        Users users = (Users)request.getSession().getAttribute("users");
        model.addAttribute("users",users);

        /*个人信息*/
        if("personal".equals(action)){
            model.addAttribute("section","personal");
        }
        /*账号信息*/
        else if("account".equals(action)){
            //获取当前用户所有相关信息
            List<UserThirdAuth> UserThirdAuthList = userService.getAllUsers((Users)request.getSession().getAttribute("users"));
            model.addAttribute("section","account");
            model.addAttribute("userList",UserThirdAuthList);
            model.addAttribute("msg",msg);
        }
        return "center";
    }
    @RequestMapping("/center/delUser/{authId}/{authType}")
    @ResponseBody
    public String delUser(@PathVariable("authId")Long authId,
                          @PathVariable("authType")String authType){
        System.out.println(authId + authType);
        return userService.delUserByAuthIdAndAuthType(authId,authType);
    }
}
