package com.craft.demo.commons.constants;


public class ApplicationConstants {

    //URL Operations
    public static final String DOMAIN_PREFIX = "cra.ft/";
    public static final String HTTP_SCHEME = "http://";
    //OWASP URL Validation Regex
    public static final String URL_REGEX = "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" +
            "(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)" +
            "([).!';/?:,][[:blank:]])?$";
    //Counters
    public static final Integer RETRY_COUNT = 5;
    //log Messages
    public static final String EMPTY_INPUT = "Provided input is empty. Pls provide valid Url";
    public static final String NO_LONG_URL_FOR_INPUT = "Cannot find any Url corresponding to provided ShortUrl: {}";
    public static final String TIME_EXPIRED_FOR_URL = "Time has expired for shortUrl: {}";

    private ApplicationConstants() {
        throw new IllegalStateException("Constants class");
    }
}
