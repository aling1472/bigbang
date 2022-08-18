package com.tencent.wxcloudrun.dvo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DingTalkAlarm {
    private String msgtype;
    private DingTalkMsg text;

    public static DingTalkAlarm of(DingTalkAlarmEnum dingTalkAlarmEnum){
        DingTalkMsg dingTalkMsg = new DingTalkMsg();
        dingTalkMsg.setContent("消息提醒: " + dingTalkAlarmEnum.toString());
        return new DingTalkAlarm("text", dingTalkMsg);
    }
}
