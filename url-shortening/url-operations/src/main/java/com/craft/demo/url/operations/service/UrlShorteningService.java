package com.craft.demo.url.operations.service;

import com.craft.demo.url.operations.dto.requests.ShorteningUrlRequestDto;
import com.craft.demo.url.operations.dto.response.UrlApiResponseDto;
import org.springframework.data.util.Pair;

import java.util.List;

public interface UrlShorteningService {

    UrlApiResponseDto shortenUrl(ShorteningUrlRequestDto longUrl); //return shortened Url

    UrlApiResponseDto fetchLongUrl(String shortenUrl);  //returns corresponding Long Url

    List<Pair<String, String>> fetchAllShortUrls();

    UrlApiResponseDto deleteUrl(String shortUrl); //deletes a short Url, Need valid user-id and password matching to delete urls

    String redirectUrl(String shortUrl);

    Integer fetchStatsForUrl(String shortUrl);
}
