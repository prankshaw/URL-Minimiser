package com.craft.demo.url.operations.utils;

import com.craft.demo.commons.enums.ResponseStatus;
import com.craft.demo.url.operations.dto.response.UrlApiResponseDto;
import org.springframework.stereotype.Component;

@Component
public class RequestResponseUtils {

    public UrlApiResponseDto buildUrlApiResponse(String responseMsg, String associatedUrl, ResponseStatus responseStatus) {
        return UrlApiResponseDto.builder()
                .responseMsg(responseMsg)
                .urlReceived(associatedUrl)
                .responseStatus(responseStatus)
                .build();
    }
}
