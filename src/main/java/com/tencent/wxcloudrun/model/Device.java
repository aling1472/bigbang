package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Device implements Serializable {
    private Integer id;

   private String name;

    private String gpsX;

    private String gpsY;

    private String city;

    private Integer department;

    private String sn;

    private String status;

    private LocalDateTime workStart;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
