package com.carpool.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CarpoolApplicationRequest {
    @NotNull(message = "拼车信息ID不能为空")
    private Long postId;
    
    @NotBlank(message = "申请人姓名不能为空")
    private String applicantName;
    
    @NotBlank(message = "申请人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String applicantPhone;
    
    @NotNull(message = "乘车人数不能为空")
    @Min(value = 1, message = "乘车人数至少为1")
    private Integer passengers = 1;
}
