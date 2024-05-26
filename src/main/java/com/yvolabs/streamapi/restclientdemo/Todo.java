package com.yvolabs.streamapi.restclientdemo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yvonne N
 */
@Data
@Builder
public class Todo{
    private Long id;
    private String title;
    private Boolean completed;

}
