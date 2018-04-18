package com.dengzhanglin.favoriteapi.exception;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class AppExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object errorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {

        e.printStackTrace();

//        for (StackTraceElement trace : e.getStackTrace()) {
//            logger.info(trace.getClassName() + ":" + trace.getLineNumber());
//        }

        String msg;
        if (e instanceof MethodArgumentNotValidException) {
            logger.error("MethodArgumentNotValidException");
            StringBuilder sb = new StringBuilder();
            for (ObjectError error : ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors()) {
                sb.append(((FieldError) error).getField()).append(":").append(error.getDefaultMessage()).append(" ");
            }
            msg = sb.toString().trim();
        } else {
            msg = e.getMessage();
        }

        logger.error("url: " + request.getRequestURL().toString());
        logger.error("message: " + e.getMessage());
        logger.error("status: " + response.getStatus());

        String finalMsg = msg;
        return new JSONObject() {{
            put("code", -1);
            put("message", finalMsg);
        }};
    }
}
