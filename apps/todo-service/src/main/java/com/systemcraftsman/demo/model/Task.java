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
