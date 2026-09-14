package com.yly.xiaozhiapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yly.xiaozhiapp.entity.Appointment;
import com.yly.xiaozhiapp.mapper.AppointmentMapper;
import com.yly.xiaozhiapp.service.AppointmentService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: AppointmentServiceImpl
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/28
 * @Version: 1.0
 */
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment>
        implements AppointmentService {
    /**
     * 查询订单是否存在
     * @param appointment
     * @return
     */

    public Appointment getOne(Appointment appointment) {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getUsername, appointment.getUsername());
        queryWrapper.eq(Appointment::getIdCard, appointment.getIdCard());
        queryWrapper.eq(Appointment::getDepartment, appointment.getDepartment());
        queryWrapper.eq(Appointment::getDate, appointment.getDate());
        queryWrapper.eq(Appointment::getTime, appointment.getTime());
        Appointment appointmentDB = baseMapper.selectOne(queryWrapper);
        return appointmentDB;
    }
}
