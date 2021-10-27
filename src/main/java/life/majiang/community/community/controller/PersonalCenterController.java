package life.majiang.community.community.controller;

import life.majiang.community.community.model.Users;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PersonalCenterController {
    @RequestMapping("/center/{action}")
    public String center(HttpServletRequest request,
                         Model model,
                         @PathVariable(name = "action")String action){
        Users users = (Users)request.getSession().getAttribute("users");
        model.addAttribute("users",users);
        if("personal".equals(action)){
            model.addAttribute("section","personal");
        }
        else if("account".equals(action)){
            model.addAttribute("section","account");
        }
        return "center";
    }
}
