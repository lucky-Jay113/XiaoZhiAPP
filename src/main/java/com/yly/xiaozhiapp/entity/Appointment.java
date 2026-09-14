package com.yly.xiaozhiapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: Appointment
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/28
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    //主键自增
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String idCard;
    private String department;
    private String date;
    private String time;
    private String doctorName;
}
