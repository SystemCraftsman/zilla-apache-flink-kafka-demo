/*
 * Copyright 2021-2022 Aklivity. All rights reserved.
 */
package com.systemcraftsman.demo.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@Data
public class Task
{
    String id;
    String content;
}
