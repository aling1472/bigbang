package com.tencent.wxcloudrun.dvo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeviceStatus {
    private List<LineStatus> mtList;
    private LocalDateTime bindtime ;
    private String connecttype;
    private String devicename;
    private String enableflag;
    private LocalDateTime endtime;
    private String groupid;
    private String groupname;
    private String id;
    private String itemgroupname;
    private String muxcnt;
    private String onlineflag;
    private String sn;
    private LocalDateTime starttime;
    private String type;
}
