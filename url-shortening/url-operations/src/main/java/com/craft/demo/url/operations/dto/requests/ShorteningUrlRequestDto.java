package com.craft.demo.url.operations.dto.requests;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ShorteningUrlRequestDto {

    @NotNull
    String longUrl;

    Integer expirationTime;  //Assuming time in minutes now

    String customizeUrl;
}