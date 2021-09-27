package life.majiang.community.community.interceptor;

import life.majiang.community.community.enums.NotificationStatusEnum;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import life.majiang.community.community.model.UserExample;
import life.majiang.community.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.management.Notification;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class Sessioninterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length != 0){
            for (Cookie cookie : cookies) {
                if("token".equalsIgnoreCase(cookie.getName())){
//                  服务端从客户端拿到token
                    String token = cookie.getValue();
                    UserExample example = new UserExample();
                    example.createCriteria().andTokenEqualTo(token);
//                  服务端拿到token以后在数据库中验证token是否正确，并取出对应的用户信息返回给客户端
                    List<User> users = userMapper.selectByExample(example);
//                  User user = userMapper.findByToken(token);  //检查数据库中是否存在此token
                    if(users.size() != 0){
                        Long unreadCount = notificationService.unreadCount(users.get(0).getId());
                        request.getSession().setAttribute("user",users.get(0));   //存在此token 把user信息写到Session里去 实现登录持久化
                        request.getSession().setAttribute("unreadMessage",unreadCount);
                    }
                }
            }
        }
        return true;
        //return true ：继续执行
        //return false ：会停止
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
