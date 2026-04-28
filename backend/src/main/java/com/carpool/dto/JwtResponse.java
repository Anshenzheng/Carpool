package com.carpool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Set;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Set<String> roles;

    public JwtResponse(String token, Long id, String username, String realName, 
                       String phone, String email, Set<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.realName = realName;
        this.phone = phone;
        this.email = email;
        this.roles = roles;
    }
}
