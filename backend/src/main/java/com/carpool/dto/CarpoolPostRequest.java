package com.carpool.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CarpoolPostRequest {
    @NotBlank(message = "出发地不能为空")
    private String departure;
    
    @NotBlank(message = "目的地不能为空")
    private String destination;
    
    @NotNull(message = "出发时间不能为空")
    private LocalDateTime departureTime;
    
    @NotNull(message = "座位数不能为空")
    @Min(value = 1, message = "座位数至少为1")
    private Integer seats;
    
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;
    
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;
    
    private String description;
}
