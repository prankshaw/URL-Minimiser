package com.craft.demo.url.operations.service.impl;

import com.craft.demo.commons.enums.ResponseStatus;
import com.craft.demo.commons.utils.BasicOperationsUtils;
import com.craft.demo.commons.utils.NextIdGenerator;
import com.craft.demo.url.operations.dto.requests.ShorteningUrlRequestDto;
import com.craft.demo.url.operations.dto.response.UrlApiResponseDto;
import com.craft.demo.url.operations.entity.UrlInfoDetails;
import com.craft.demo.url.operations.repository.UrlInfoRepository;
import com.craft.demo.url.operations.service.UrlShorteningService;
import com.craft.demo.url.operations.utils.RequestResponseUtils;
import com.craft.demo.url.operations.utils.UrlOperationsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.craft.demo.commons.constants.ApplicationConstants.*;

@Service
@Slf4j
public class UrlShorteningServiceImpl implements UrlShorteningService {

    private final RequestResponseUtils requestResponseUtils = new RequestResponseUtils();
    @Autowired
    private BasicOperationsUtils basicOperationsUtils;
    @Autowired
    private UrlInfoRepository urlInfoRepository;
    @Autowired
    private NextIdGenerator nextIdGenerator;
    @Autowired
    private UrlOperationsUtils urlOperationsUtils;

    /**
     * @param shortenUrlRequest - LongUrl, ExpirationTime, Customisation
     * @return UrlApiResponseDto - shortenedUrl and response against and as specified by input
     */
    @Override
    public UrlApiResponseDto shortenUrl(ShorteningUrlRequestDto shortenUrlRequest) {

        String responseMsg;
        String shortenedUrl;
        String longUrl = shortenUrlRequest.getLongUrl();
        Boolean isCustomUrl = Boolean.FALSE;

        if (Boolean.TRUE.equals(basicOperationsUtils.isEmpty(longUrl)) || Boolean.FALSE.equals(urlOperationsUtils.isValidUrl(longUrl))) {
            log.info("Cannot shorten Url, as longUrl provided is empty or invalid");

            responseMsg = "Cannot perform shortening of Url, as provided input is empty or invalid";
            return requestResponseUtils.buildUrlApiResponse(responseMsg, null, ResponseStatus.FAILURE);
        }

        //if customUrl is provided in request
        if (Boolean.FALSE.equals(basicOperationsUtils.isEmpty(shortenUrlRequest.getCustomizeUrl()))) {
            shortenedUrl = DOMAIN_PREFIX.concat(shortenUrlRequest.getCustomizeUrl());
            isCustomUrl = Boolean.TRUE;
        } else {
            shortenedUrl = DOMAIN_PREFIX.concat(urlOperationsUtils.generateUniqueCode());
        }

        Optional<UrlInfoDetails> optionalUrlInfoDetails = urlInfoRepository.findByShortUrl(shortenedUrl);

        if (optionalUrlInfoDetails.isPresent()) {

            UrlInfoDetails urlInfoDetails = optionalUrlInfoDetails.get();

            if (Boolean.TRUE.equals(urlOperationsUtils.isExpired(urlInfoDetails))) {
                //Time has expired for this Url, and we can use this for further mapping
                log.info(TIME_EXPIRED_FOR_URL, shortenedUrl);
            } else {

                if (Boolean.TRUE.equals(isCustomUrl)) {

                    //As this custom URL is already present in our system, user need to change this
                    log.info("Custom Url: {} is already present. Kindly change.", shortenUrlRequest.getCustomizeUrl());

                    responseMsg = "Provided Custom Url: " + shortenUrlRequest.getCustomizeUrl() + " is already present in our storage. Kindly change to something unique like: " + urlOperationsUtils.generateUniqueCode();
                    return requestResponseUtils.buildUrlApiResponse(responseMsg, null, ResponseStatus.FAILURE);
                } else {

                    //Retry to generate new one
                    int counter = RETRY_COUNT;
                    boolean canNotGenerate = Boolean.TRUE;

                    while (counter != 0) { //Try RETRY_COUNT times

                        shortenedUrl = DOMAIN_PREFIX.concat(urlOperationsUtils.generateUniqueCode());

                        if (urlInfoRepository.findByShortUrl(shortenedUrl).isPresent()) { //This generated url is present in our storage
                            counter--;
                        } else {
                            //found a unique shortUrl
                            canNotGenerate = Boolean.FALSE;
                        }
                    }

                    if (Boolean.TRUE.equals(canNotGenerate)) {
                        responseMsg = "Unable to generate unique Url for: " + shortenUrlRequest.getLongUrl() + ", right now. Kindly wait.";
                        return requestResponseUtils.buildUrlApiResponse(responseMsg, null, ResponseStatus.FAILURE);
                    }
                }
            }
        }


        UrlInfoDetails urlInfoDetails = UrlInfoDetails.builder()
                .shortUrl(shortenedUrl)
                .longUrl(longUrl)
                .expirationTime(shortenUrlRequest.getExpirationTime())
                .build();

        //save in storage
        urlInfoRepository.save(urlInfoDetails);

        log.info("The Url shortening is done for: {} -> {}", longUrl, shortenedUrl);

        responseMsg = "The Url shortening is done for: " + longUrl + " -> " + shortenedUrl;
        return requestResponseUtils.buildUrlApiResponse(responseMsg, shortenedUrl, ResponseStatus.SUCCESS);
    }

    /**
     * @param shortUrl - Short URL generated by system earlier
     * @return UrlApiResponseDto - LongURL mapping and response against input
     */
    @Override
    public UrlApiResponseDto fetchLongUrl(String shortUrl) {

        String responseMsg;

        if (basicOperationsUtils.isEmpty(shortUrl)) {
            log.info(EMPTY_INPUT);

            responseMsg = EMPTY_INPUT;
            return requestResponseUtils.buildUrlApiResponse(responseMsg, null, ResponseStatus.FAILURE);
        }

        Optional<UrlInfoDetails> optionalUrlInfoDetails = urlInfoRepository.findByShortUrl(shortUrl);

        if (optionalUrlInfoDetails.isEmpty()) {
            log.info(NO_LONG_URL_FOR_INPUT, shortUrl);

            responseMsg = "Cannot find any Url mapping corresponding to provided input: " + shortUrl;
            return requestResponseUtils.buildUrlApiResponse(responseMsg, null, ResponseStatus.FAILURE);
        }

        UrlInfoDetails urlInfoDetails = optionalUrlInfoDetails.get();

        if (Boolean.TRUE.equals(urlOperationsUtils.isExpired(urlInfoDetails))) {
            //Time has expired for this Url
            log.info(TIME_EXPIRED_FOR_URL, shortUrl);

            responseMsg = "Cannot find any Url mapping corresponding to provided input: " + shortUrl;
            return requestResponseUtils.buildUrlApiResponse(responseMsg, null, ResponseStatus.FAILURE);
        }

        log.info("Found LongUrl: {} corresponding to provided Url: {}, with hits So Far: {}", urlInfoDetails.getLongUrl(), shortUrl, urlInfoDetails.getVisitCount());

        responseMsg = "Found LongUrl: " + urlInfoDetails.getLongUrl() + " corresponding to provided Url: " + shortUrl + ", with hits So Far: " + urlInfoDetails.getVisitCount().toString();
        return requestResponseUtils.buildUrlApiResponse(responseMsg, urlInfoDetails.getLongUrl(), ResponseStatus.SUCCESS);
    }

    /**
     * @return List<Pair < String, String>> - List of mappings of short and long Urls as present in system
     */
    @Override
    public List<Pair<String, String>> fetchAllShortUrls() {
        List<UrlInfoDetails> allValuesPresent = urlInfoRepository.findAll();

        List<Pair<String, String>> allUrlCombinations = new ArrayList<>();

        for (UrlInfoDetails urlInfoDetails : allValuesPresent) {

            //checking if any link is expired
            if (Boolean.TRUE.equals(urlOperationsUtils.isExpired(urlInfoDetails))) {
                //Time has expired for this Url
                log.info(TIME_EXPIRED_FOR_URL, urlInfoDetails.getShortUrl());
                continue;
            }

            allUrlCombinations.add(Pair.of(urlInfoDetails.getShortUrl(), urlInfoDetails.getLongUrl()));
        }

        return allUrlCombinations;
    }

    /**
     * @param shortUrl - Short URL generated by system earlier
     * @return String - Redirect URL against input
     */
    @Override
    public String redirectUrl(String shortUrl) {

        if (basicOperationsUtils.isEmpty(shortUrl)) {
            log.info(EMPTY_INPUT);
            return null;
        }

        Optional<UrlInfoDetails> optionalUrlInfoDetails = urlInfoRepository.findByShortUrl(shortUrl);

        if (optionalUrlInfoDetails.isEmpty()) {
            log.info(NO_LONG_URL_FOR_INPUT, shortUrl);
            return null;
        }

        UrlInfoDetails urlInfoDetails = optionalUrlInfoDetails.get();

        if (Boolean.TRUE.equals(urlOperationsUtils.isExpired(urlInfoDetails))) {
            //Time has expired for this Url
            log.info(TIME_EXPIRED_FOR_URL, shortUrl);
            return null;
        }

        log.info("Found LongUrl: {} corresponding to provided Url: {}, with hits So Far: {}", urlInfoDetails.getLongUrl(), shortUrl, urlInfoDetails.getVisitCount());

        //incrementing visit count statistic on redirection request
        //Visit count increment can be done asynchronously or can be written to cache and later persisted
        urlInfoDetails.setVisitCount(urlInfoDetails.getVisitCount() + 1);
        urlInfoRepository.save(urlInfoDetails);

        return urlInfoDetails.getLongUrl();

    }

    /**
     * @param shortUrl - Short URL generated by system earlier
     * @return Integer - Number of redirections, so far against input
     */
    @Override
    public Integer fetchStatsForUrl(String shortUrl) {

        if (basicOperationsUtils.isEmpty(shortUrl)) {
            log.info("Provided input is empty. Pls provide valid Url");
            return -1;
        }

        Optional<UrlInfoDetails> optionalUrlInfoDetails = urlInfoRepository.findByShortUrl(shortUrl);

        if (optionalUrlInfoDetails.isEmpty()) {
            log.info(NO_LONG_URL_FOR_INPUT, shortUrl);
            return -1;
        }

        UrlInfoDetails urlInfoDetails = optionalUrlInfoDetails.get();
        log.info("Found LongUrl: {}, corresponding to provided Url: {}, with hits So Far: {}", urlInfoDetails.getLongUrl(), shortUrl, urlInfoDetails.getVisitCount());

        return urlInfoDetails.getVisitCount();
    }

    /**
     * @param shortUrl - Short URL generated by system earlier
     * @return UrlApiResponseDto - Status and response against deletion request
     */
    @Override
    public UrlApiResponseDto deleteUrl(String shortUrl) {

        String responseMsg;

        if (basicOperationsUtils.isEmpty(shortUrl)) {
            log.info(EMPTY_INPUT);

            responseMsg = EMPTY_INPUT;
            return requestResponseUtils.buildUrlApiResponse(responseMsg, null, ResponseStatus.FAILURE);
        }

        Optional<UrlInfoDetails> optionalUrlInfoDetails = urlInfoRepository.findByShortUrl(shortUrl);
        if (optionalUrlInfoDetails.isEmpty()) {
            log.info(NO_LONG_URL_FOR_INPUT, shortUrl);

            responseMsg = "Cannot find this entry in our storage: " + shortUrl;
            return requestResponseUtils.buildUrlApiResponse(responseMsg, null, ResponseStatus.SUCCESS);
        }

        UrlInfoDetails urlInfoDetails = optionalUrlInfoDetails.get();

        //call delete function
        if (Boolean.TRUE.equals(deleteThisEntry(urlInfoDetails))) {
            log.info("Entry deleted for shortUrl: {}", shortUrl);

            responseMsg = "Entry deleted for shortUrl: " + shortUrl;
            return requestResponseUtils.buildUrlApiResponse(responseMsg, null, ResponseStatus.SUCCESS);
        }

        log.info("Issue in deleting entry: {} from DB", urlInfoDetails);
        responseMsg = "Issue in deleting entry: " + urlInfoDetails + "from DB.";
        return requestResponseUtils.buildUrlApiResponse(responseMsg, null, ResponseStatus.FAILURE);
    }


    /**
     * @param urlInfoDetails - Details about url mapping as fetched from storage
     * @return Boolean - Whether deletion was successful or not
     * Helper Function
     */
    public Boolean deleteThisEntry(UrlInfoDetails urlInfoDetails) {
        log.info("Request received for deleting entries : {}", urlInfoDetails);

        try {
            urlInfoRepository.delete(urlInfoDetails);
            log.info("Entry deleted: {}", urlInfoDetails);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Issue in deleting entry: {} from DB", urlInfoDetails, e);
            return Boolean.FALSE;
        }
    }

}
