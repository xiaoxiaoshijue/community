package life.majiang.community.community.controller;

import life.majiang.community.community.cache.TagCache;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.Users;
import life.majiang.community.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    /**
     *  修改自己发布的问题
     * @param id：该问题的主键id
     * @param model ：将原页面数据封装到发布页面
     * @return 返回 发布页面
     */
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id")Long id,
                       Model model){
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    /**
     *  进入发布问题页面
     * @param model 携带所有标签信息到页面上
     * @return 从首页进入发布页面
     */
    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    /**
     *  发布问题 / 修改问题
     * @param title 当前页面标题
     * @param description 当前页面描述
     * @param tag 当前页面标签
     * @param id 问题的id。如果为null则是发布问题，如果不为null则是修改问题
     * @param request .
     * @param model 携带标题表述标签
     * @return 重定向回首页
     */
    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title",required = false)String title,
            @RequestParam(value = "description",required = false)String description,
            @RequestParam(value = "tag",required = false)String tag,
            @RequestParam(value = "id",required = false)Long id,
            HttpServletRequest request,
            Model model){

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tags", TagCache.get());


        if(title==null || title.equals("")){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if(title.length() >= 50){
            model.addAttribute("error","标题过长");
            return "publish";
        }
        if(description==null || description.equals("")){
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }
        if(tag==null || tag.equals("")){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }
        String invalid = TagCache.filterInvalid(tag);
        if(StringUtils.isNotBlank(invalid)){
            model.addAttribute("error","输入非法标签" + invalid);
            return "publish";
        }


        Users users = (Users)request.getSession().getAttribute("users");
        //user==null 未登录,在model中存储错误信息
        if(users == null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(users.getUserId());
        question.setId(id);
        questionService.createOrUpdate(question);

        return "redirect:/";
    }
}
