package com.rzd.order.client;

import com.rzd.common.dto.UserDTO;
import com.rzd.order.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/api/users/{email}")
    UserDTO getUserByEmail(@PathVariable("email") String email);
}