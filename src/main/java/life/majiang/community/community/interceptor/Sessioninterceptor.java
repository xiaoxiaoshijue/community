package life.majiang.community.community.interceptor;

import life.majiang.community.community.exception.ErrorCodeEnum;
import life.majiang.community.community.exception.CustomizeException;
import life.majiang.community.community.mapper.UsersMapper;
import life.majiang.community.community.model.Users;
import life.majiang.community.community.model.UsersExample;
import life.majiang.community.community.provider.JwtUtil;
import life.majiang.community.community.provider.PassToken;
import life.majiang.community.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

@Service
public class Sessioninterceptor implements HandlerInterceptor {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private NotificationService notificationService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if("token".equals(cookie.getName())){
                    token = cookie.getValue();
                }
            }
            for(Cookie cookie : cookies){
                if("token".equals(cookie.getName())){
                    String userName = JwtUtil.getAudience(token);
                    try{
                        Users users = JwtUtil.getUsersByJWT(token,userName);
                        //设置通知已读未读   ---优化 --> 在通知页面时才执行
                        Long unreadCount = notificationService.unreadCount(users.getUserId());
                        //放入attribute以便后面调用
                        request.getSession().setAttribute("users",users);
                        request.getSession().setAttribute("unreadMessage",unreadCount);
                    }catch (Exception e){
                        return true;
                    }
                }
            }
        }
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
//默认全部检查
        else {
            System.out.println("被jwt拦截需要验证");
            // 执行认证
            if (token == null) {
                //这里其实是登录失效,没token了   这个错误也是我自定义的，读者需要自己修改
                //throw new CustomizeException(CustomizeErrorCode.TOKEN_NOT_EXIST);
                return true;
            }
            // 获取 token 中的 user Name
            String userName = JwtUtil.getAudience(token);
            //找找看是否有这个user   因为我们需要检查用户是否存在，读者可以自行修改逻辑
            UsersExample usersExample = new UsersExample();
            usersExample.createCriteria().andUserNameEqualTo(userName);
            List<Users> dbUser = usersMapper.selectByExample(usersExample);
            if(dbUser == null){
                throw new CustomizeException(ErrorCodeEnum.USER_NOT_EXIST);
            }
            // 验证 token
            JwtUtil.verifyToken(token,userName);
            //获取载荷内容
            Users users = JwtUtil.getUsersByJWT(token,userName);
            //设置通知已读未读   ---优化 --> 在通知页面时才执行
            Long unreadCount = notificationService.unreadCount(users.getUserId());
            //放入attribute以便后面调用
            request.getSession().setAttribute("users",users);
            request.getSession().setAttribute("unreadMessage",unreadCount);
            return true;
        }
        return true;

        /*if(cookies != null && cookies.length != 0){
            System.out.println("-----------mapping");
            for (Cookie cookie : cookies) {
                if("token".equalsIgnoreCase(cookie.getName())){
//                  服务端从客户端拿到token
                    String token = cookie.getValue();
                    *//*UserExample example = new UserExample();
                    example.createCriteria().andTokenEqualTo(token);2021109*//*
                    UsersExample usersExample = new UsersExample();
                    usersExample.createCriteria().andTokenEqualTo(token);//2021109
//                  服务端拿到token以后在数据库中验证token是否正确，并取出对应的用户信息返回给客户端
                    List<Users> users = usersMapper.selectByExample(usersExample);
//                  User user = userMapper.findByToken(token);  //检查数据库中是否存在此token
                    if(users.size() != 0){
                        Long unreadCount = notificationService.unreadCount(users.get(0).getUserId());
                        request.getSession().setAttribute("users",users.get(0));   //存在此token 把user信息写到Session里去 实现登录持久化
                        request.getSession().setAttribute("unreadMessage",unreadCount);
                    }
                }
            }
        }*/
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
