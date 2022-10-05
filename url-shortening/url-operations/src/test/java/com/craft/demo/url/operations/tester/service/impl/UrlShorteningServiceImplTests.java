package com.craft.demo.url.operations.tester.service.impl;

import com.craft.demo.commons.utils.BasicOperationsUtils;
import com.craft.demo.url.operations.entity.UrlInfoDetails;
import com.craft.demo.url.operations.repository.UrlInfoRepository;
import com.craft.demo.url.operations.service.impl.UrlShorteningServiceImpl;
import com.craft.demo.url.operations.utils.UrlOperationsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class UrlShorteningServiceImplTests {

    @InjectMocks
    private UrlShorteningServiceImpl urlShorteningServiceImpl;

    @Mock
    private BasicOperationsUtils basicOperationsUtils;

    @Mock
    private UrlOperationsUtils urlOperationsUtils;

    @Mock
    private UrlInfoRepository urlInfoRepository;

    @Test
    public void testContextLoad() {
        assertEquals(1, 1); //Test to test the tests
    }

    @Test
    public void fetchStatsTest() {

        String url_1 = "cra.ft/demo1";

        Mockito.when(basicOperationsUtils.isEmpty(url_1))
                .thenReturn(false);

        Mockito.when(urlInfoRepository.findByShortUrl(url_1))
                .thenReturn(Optional.empty());

        assertEquals(Optional.of(-1), Optional.of(urlShorteningServiceImpl.fetchStatsForUrl(url_1)));

        UrlInfoDetails urlInfoDetails = UrlInfoDetails.builder()
                .shortUrl("")
                .longUrl("")
                .visitCount(5)
                .build();

        Mockito.when(urlInfoRepository.findByShortUrl(url_1))
                .thenReturn(Optional.ofNullable(urlInfoDetails));

        assertEquals(Optional.of(5), Optional.of(urlShorteningServiceImpl.fetchStatsForUrl(url_1)));
    }

    @Test
    public void deletionTest() {

        UrlInfoDetails urlInfoDetails = UrlInfoDetails.builder()
                .shortUrl("")
                .longUrl("")
                .build();

        assertNotNull(urlInfoDetails);
        assertEquals(true, urlShorteningServiceImpl.deleteThisEntry(urlInfoDetails));
    }

    @Test
    public void fetchAllShortUrlsPresentTest() {

        //Testing with Empty Return
        Mockito.when(urlInfoRepository.findAll())
                .thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), urlShorteningServiceImpl.fetchAllShortUrls());

        Mockito.when(urlInfoRepository.findAll())
                .thenReturn(Collections.emptyList());


        //Mocking a return value from DB
        List<UrlInfoDetails> valuesPresent = new ArrayList<>();

        UrlInfoDetails urlInfoDetails = UrlInfoDetails.builder()
                .shortUrl("")
                .longUrl("")
                .visitCount(5)
                .build();

        valuesPresent.add(urlInfoDetails);

        Mockito.when(urlInfoRepository.findAll())
                .thenReturn(valuesPresent);


        //Mocking expired to be true
        Mockito.when(urlOperationsUtils.isExpired(urlInfoDetails))
                .thenReturn(true);

        assertEquals(Collections.emptyList(), urlShorteningServiceImpl.fetchAllShortUrls());


        //Mocking expired to be false
        Mockito.when(urlOperationsUtils.isExpired(urlInfoDetails))
                .thenReturn(false);

        assertNotEquals(Collections.emptyList(), urlShorteningServiceImpl.fetchAllShortUrls());
    }

}
