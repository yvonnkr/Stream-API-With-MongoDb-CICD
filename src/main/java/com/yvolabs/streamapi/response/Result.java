package com.yvolabs.streamapi.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yvonne N
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result<T> {
    private boolean flag; // Two values: true means success, false means not success

    private Integer code; // Status code.

    private String message; // Response message

    private T data; // The response payload
}
