package com.craft.demo.url.operations.tester.util;

import com.craft.demo.commons.utils.BasicOperationsUtils;
import com.craft.demo.url.operations.entity.UrlInfoDetails;
import com.craft.demo.url.operations.service.impl.UrlShorteningServiceImpl;
import com.craft.demo.url.operations.utils.UrlOperationsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UrlOperationsTest {

    @InjectMocks
    private UrlOperationsUtils urlOperationsUtils;

    @Mock
    private BasicOperationsUtils basicOperationsUtils;

    @Mock
    private UrlShorteningServiceImpl urlShorteningService;

    @Test
    public void isExpiredDefault() {
        UrlInfoDetails urlInfoDetails = UrlInfoDetails.builder()
                .shortUrl("")
                .longUrl("")
                .build();

        assertEquals(false, urlOperationsUtils.isExpired(urlInfoDetails));
    }

    @Test
    public void isExpiredCustom() {
        UrlInfoDetails urlInfoDetails = UrlInfoDetails.builder()
                .shortUrl("")
                .longUrl("")
                .urlCreationTime(LocalDateTime.now().minusMinutes(10))
                .build();

        Mockito.when(urlShorteningService.deleteThisEntry(urlInfoDetails))
                .thenReturn(true);

        assertEquals(true, urlOperationsUtils.isExpired(urlInfoDetails));
    }

    @Test
    public void isValidUrl() {
        String urlToCheck_1 = "http://www.twitter.in";
        String urlToCheck_2 = "";

        Mockito.when(basicOperationsUtils.isEmpty(urlToCheck_1))
                .thenReturn(false);

        Mockito.when(basicOperationsUtils.isEmpty(urlToCheck_2))
                .thenReturn(true);

        assertEquals(true, urlOperationsUtils.isValidUrl(urlToCheck_1));
        assertEquals(false, urlOperationsUtils.isValidUrl(urlToCheck_2));
    }

    @Test
    public void isValidCustomisation() {
        assertEquals(false, urlOperationsUtils.isValidCustomisation(""));
        assertEquals(true, urlOperationsUtils.isValidCustomisation("q12-y"));
        assertEquals(false, urlOperationsUtils.isValidCustomisation("q12-yopiuyt"));
        assertEquals(false, urlOperationsUtils.isValidCustomisation("me+craft"));
    }

}