package com.example.assetsys.entity.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDto {
    private Long id;
    private String name;
    private String taskdesc;
    private String relatedorg;
    private List<String> joinedtesters;
    private String taskstatus;
    private String taskvul;
    private String starttime;
    private String endtime;





}
