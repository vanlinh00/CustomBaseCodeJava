package com.example.basecommon.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class PageResponse {


    @JsonProperty("content")
    public List content;


    @JsonProperty("totalPages")
    private Integer totalPages;


    @JsonProperty("hasNext")
    private Boolean hasNext;


    @JsonProperty("hasPrevious")
    private Boolean hasPrevious;


    @JsonProperty("page")
    private Integer page;


    @JsonProperty("size")
    private Integer size;


    @JsonProperty("totalElements")
    private Long totalElements;
}

