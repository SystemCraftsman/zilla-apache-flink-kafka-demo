/*
 * Copyright 2021-2022 Aklivity. All rights reserved.
 */
package com.systemcraftsman.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskCommand
{
    String taskId;
    @JsonProperty("content")
    String taskContent;
    CommandType commandType;
}
