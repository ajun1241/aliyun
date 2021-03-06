package com.modcreater.tmstore.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.exception.MyException;
import com.modcreater.tmutils.DtoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-13
 * Time: 14:47
 */
@ControllerAdvice
public class ExceptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Dto errorHandler(Exception exception) {
        LOGGER.error("捕获到全局异常", exception);
        return DtoUtil.getFalseDto("网络异常，请重试", 100);
    }

    @ResponseBody
    @ExceptionHandler(value = MyException.class)
    public Dto myErrorHandler(MyException exception) {
        LOGGER.error("捕获到自定义异常", exception);
        return DtoUtil.getFalseDto("网络异常，请重试", 101);
    }


}
