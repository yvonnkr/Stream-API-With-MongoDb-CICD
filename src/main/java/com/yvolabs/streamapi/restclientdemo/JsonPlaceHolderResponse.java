package com.yvolabs.streamapi.restclientdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yvonne N
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonPlaceHolderResponse {
    private Long id;
    private String title;
    private Boolean completed;
}
