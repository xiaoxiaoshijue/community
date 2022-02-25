package life.majiang.community.community.controller;

import life.majiang.community.community.dto.PaginationDTO;
import life.majiang.community.community.model.Users;
import life.majiang.community.community.service.NotificationService;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    /**
     *  根据userid获取我的提问/根据userid获取最新回复(只是按照时间排序)
     * @param action 请求：我的提问/最新回复
     * @param model 我的所有问题DTO对象/我的所有通知DTO对象
     * @return
     */
    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action")String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page",defaultValue = "1")Integer page,
                          @RequestParam(name = "size",defaultValue = "6")Integer size ){
        Users users = (Users)request.getSession().getAttribute("users");
        System.out.println("当前登录user的信息 ：" + users.toString());
        if(users == null){
            return "redirect:/";
        }

        if("questions".equals(action)){
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的提问");
            PaginationDTO paginationDTO = questionService.listByUserId(users.getUserId(), page, size);
            model.addAttribute("pagination",paginationDTO);
            System.out.println(paginationDTO.toString());
        }else if("replies".equals(action)){
            PaginationDTO paginationDTO = notificationService.list(users.getUserId(),page,size);
            model.addAttribute("section","replies");
            model.addAttribute("pagination",paginationDTO);
            model.addAttribute("sectionName","最新回复");
        }
        return "profile";
    }
}
