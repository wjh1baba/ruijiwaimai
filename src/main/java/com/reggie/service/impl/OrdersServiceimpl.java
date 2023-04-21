package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.Orders;
import com.reggie.mapper.OrdersMapper;
import com.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceimpl extends ServiceImpl<OrdersMapper,Orders> implements OrdersService {
}
