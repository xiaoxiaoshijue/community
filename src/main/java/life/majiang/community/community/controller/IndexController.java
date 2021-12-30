package life.majiang.community.community.controller;


import life.majiang.community.community.dto.PaginationDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    @Autowired
    private QuestionService questionService;

        @RequestMapping("/")
        public String index(Model model,
                            @RequestParam(name = "page",defaultValue = "1")Integer page,
                            @RequestParam(name = "size",defaultValue = "5")Integer size,
                            @RequestParam(name = "search",required = false)String search,
                            @ModelAttribute("msg")String msg,
                            @ModelAttribute("result")String result) {
                if(msg == "register"){
                model.addAttribute("msg","register");
            }if(result != null && !result.equals("")){
                model.addAttribute("result",result);
            }
            PaginationDTO<QuestionDTO> pagination = questionService.list(search,page,size);



            System.out.println(pagination.toString());
            model.addAttribute("pagination",pagination);
            model.addAttribute("search",search);
            return "index";
        }
   /* public String hello(@RequestParam(name = "name") String name, Model model){
        model.addAttribute("name",name);
        return "index";
    }*/
}
