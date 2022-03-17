package life.majiang.community.community.advice;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.TokenExpiredException;
import life.majiang.community.community.dto.ResultDTO;
import life.majiang.community.community.exception.ErrorCodeEnum;
import life.majiang.community.community.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomizeExceptionHandler {
    /*@ExceptionHandler(TokenExpiredException.class)
    ModelAndView handle1(HttpServletRequest request, Throwable e, Model model, HttpServletResponse response){
        ResultDTO resultDTO;
        resultDTO = ResultDTO.errorOf((CustomizeException)e);
        try {
            response.setContentType("application/json");
            response.setStatus(200);
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(resultDTO));
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }*/

    @ExceptionHandler(Exception.class)
    public String handle(HttpServletRequest request, Throwable e, Model model, HttpServletResponse response) {
        e.printStackTrace();
        ResultDTO resultDTO;
        if(e instanceof CustomizeException){
            resultDTO = ResultDTO.errorOf((CustomizeException)e);
        }else {
            resultDTO = ResultDTO.errorOf(ErrorCodeEnum.SYS_ERROR);
        }
        model.addAttribute("message",resultDTO.getMessage());
        /*try {
            response.setContentType("application/json");
            response.setStatus(200);
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(resultDTO));
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }*/
        return "error";
       /* String contentType = request.getContentType();
        if("application/json".equals(contentType)){
            ResultDTO resultDTO;
            //返回JSON
            if(e instanceof CustomizeException){
                resultDTO = ResultDTO.errorOf((CustomizeException)e);
            }else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return null;
        }
        else {
            if(e instanceof CustomizeException){
                model.addAttribute("message",e.getMessage());
            }else {
                model.addAttribute("message",CustomizeErrorCode.SYS_ERROR.getMessage());
            }
            return new ModelAndView("error");
            //错误页面跳转
        }*/
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
