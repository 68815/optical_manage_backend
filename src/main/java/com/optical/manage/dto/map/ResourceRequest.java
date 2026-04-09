package com.optical.manage.dto.map;

import lombok.Data;

@Data
public class ResourceRequest {
    private String name;
    private String type;
    private String address;
    private Integer status;
    private double lat;
    private double lng;
    private String props;
}
