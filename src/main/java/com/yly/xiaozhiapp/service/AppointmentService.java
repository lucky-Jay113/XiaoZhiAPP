package com.yly.xiaozhiapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yly.xiaozhiapp.entity.Appointment;

/**
 * @ClassName: AppointmentService
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/28
 * @Version: 1.0
 */
public interface AppointmentService extends IService<Appointment> {

    Appointment getOne(Appointment appointment);
}
