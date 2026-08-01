package com.example.basecommon.factory;


import com.example.basecommon.enums.MessageCode;
import com.example.basecommon.util.BaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




@Component
public class ResponseFactory {


    private static ResponseFactory factory;


    @Autowired
    private HttpServletRequest httpServletRequest;


    @PostConstruct
    public void init() {
        factory = this;
        factory.httpServletRequest = this.httpServletRequest;
    }


    public static ResponseEntity success() {
        GenericResponse<Object> responseObject = getSuccessResponse();
        return ResponseEntity.ok(responseObject);
    }


    public static ResponseEntity success(Object data) {
        GenericResponse<Object> responseObject = getSuccessResponse();
        responseObject.setData(data);
        return ResponseEntity.ok(responseObject);
    }


    public static ResponseEntity success(Page<Object> data) {
        GenericResponse<Object> responseObject = getSuccessResponse();
        responseObject.setData(buildPageResponse(data));
        return ResponseEntity.ok(responseObject);
    }


    public static ResponseEntity noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    public static ResponseEntity error(String code, String message) {
        return error(HttpStatus.NOT_FOUND, code, message, null);
    }


    public static ResponseEntity error(String code, String message, String... details) {
        return error(HttpStatus.NOT_FOUND, code, message, details != null ? Arrays.asList(details) : null);
    }


    public static ResponseEntity error(String code, String message, List<String> details) {
        return error(HttpStatus.NOT_FOUND, code, message, details);
    }


    public static ResponseEntity error(HttpStatus httpStatus, String code, String message, List<String> details) {
        GenericResponse<Object> responseObject = new GenericResponse<>();
        responseObject
                .setSuccess(false)
                .setCode(code)
                .setMessage(message)
                .setDetails(details == null ? new ArrayList<>() : details);
        return new ResponseEntity<>(responseObject, httpStatus);
    }


    private static GenericResponse<Object> getSuccessResponse() {
        GenericResponse<Object> responseObject = new GenericResponse<>();
        responseObject.setSuccess(true);
        responseObject.setCode(MessageCode.SUCCESS.getCode());
        responseObject.setMessage(BaseUtils.getErrorMessageLanguage(factory.httpServletRequest, MessageCode.SUCCESS.getCode()));
        return responseObject;
    }


    private static PageResponse buildPageResponse(Page<Object> page) {
        if (page == null) {
            return null;
        }
        int currentPage = page.getNumber() + 1;
        if (currentPage <= page.getTotalPages()) {
            return new PageResponse(page.getContent(), page.getTotalPages(), page.hasNext(), page.hasPrevious(), currentPage, page.getSize(), page.getTotalElements());
        } else {
            return new PageResponse(page.getContent(), page.getTotalPages(), null, null, null, null, page.getTotalElements());
        }
    }
}

