package com.craft.demo.url.operations.dto.response;

import com.craft.demo.commons.enums.ResponseStatus;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Builder
@Data
public class UrlApiResponseDto {

    String responseMsg;

    String urlReceived;

    @Enumerated(EnumType.STRING)
    ResponseStatus responseStatus;

}
