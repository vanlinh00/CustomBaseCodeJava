package com.example.basecommon.factory;

import com.example.basecommon.config.StringConst;
import io.swagger.v3.oas.annotations.media.Schema;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;




@Data
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class GenericResponse<T> implements Serializable {


    @Schema(name = "success", example = "true", defaultValue = "true")
    private boolean success;


    @Schema(name = "code", example = "i.xx.fw.200")
    private String code;


    @Schema(name = "message", example = StringConst.SUCCESS)
    private String message;


    @Schema(name = "db/data")
    private T data;


    @Schema(name = "details", example = "[]", defaultValue = "[]")
    private List<String> details;


    public GenericResponse() {
        this.details = new ArrayList<>();
    }


}

