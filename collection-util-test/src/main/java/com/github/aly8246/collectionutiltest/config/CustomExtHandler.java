package com.github.aly8246.collectionutiltest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestControllerAdvice
@Slf4j
public class CustomExtHandler {
    @ExceptionHandler(value = Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        log.error("url:{},msg:{}", request.getRequestURI(), e.getMessage());
        System.err.println("HttpServletRequest::" + request.toString());
        System.err.println("Exception::" + e.toString());
        System.err.print("StackTrace::");
        e.printStackTrace();

        Map<String, Object> map = new HashMap<>();
        map.put("code", 100);
        map.put("msg", e.getMessage());
        map.put("url", request.getRequestURI());
        return map;
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.error("url{},msg{}", request.getRequestURI(), e.getMessage());
        Map<String, Object> map = new HashMap<>();
        map.put("code", 100);
        map.put("msg", "文件过大");
        map.put("url", request.getRequestURI());
        return map;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        BindingResult bindingResult = ex.getBindingResult();

        Map<String, Object> map = new HashMap<>();
        map.put("code", 999);
        map.put("msg", IntStream
                .range(0, bindingResult.getFieldErrorCount())
                .mapToObj(i -> bindingResult.getFieldErrors()
                        .get(i))
                .collect(Collectors.toMap(FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (a, b) -> b,
                        LinkedHashMap::new)));
        map.put("url", request.getRequestURI());
        return map;
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Object handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 999);
        map.put("msg", ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(e -> e.getPropertyPath().toString().split("\\.")[1],
                        ConstraintViolation::getMessage,
                        (a, b) -> b,
                        LinkedHashMap::new)));
        map.put("url", request.getRequestURI());
        return map;
    }


    @ExceptionHandler({BindException.class})
    @ResponseBody
    public Object handleBeanPropertyBindingResult(BindException ex, HttpServletRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, Object> map = new HashMap<>();
        map.put("code", 999);
        map.put("msg", IntStream
                .range(0, bindingResult.getFieldErrorCount())
                .mapToObj(i -> bindingResult.getFieldErrors()
                        .get(i))
                .collect(Collectors.toMap(FieldError::getField,
                        e -> Objects.requireNonNull(e.getDefaultMessage()).split(":")[1],
                        (a, b) -> b,
                        LinkedHashMap::new)));
        map.put("url", request.getRequestURI());
        return map;
    }
}
