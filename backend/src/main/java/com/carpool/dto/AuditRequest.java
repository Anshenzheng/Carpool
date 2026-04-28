package com.carpool.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuditRequest {
    @NotNull(message = "拼车信息ID不能为空")
    private Long postId;
    
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;
    
    private String auditRemark;
}
