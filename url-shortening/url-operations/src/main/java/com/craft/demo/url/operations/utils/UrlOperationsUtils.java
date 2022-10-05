package com.craft.demo.url.operations.utils;

import com.craft.demo.commons.utils.BasicOperationsUtils;
import com.craft.demo.commons.utils.NextIdGenerator;
import com.craft.demo.url.operations.entity.UrlInfoDetails;
import com.craft.demo.url.operations.service.impl.UrlShorteningServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.craft.demo.commons.constants.ApplicationConstants.HTTP_SCHEME;
import static com.craft.demo.commons.constants.ApplicationConstants.URL_REGEX;

@Component
@Slf4j
public class UrlOperationsUtils {

    @Autowired
    private BasicOperationsUtils basicOperationsUtils;
    @Autowired
    private UrlShorteningServiceImpl urlOperationsServiceImpl;
    @Autowired
    private NextIdGenerator nextIdGenerator;

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public ResponseEntity<RedirectView> redirectionToUrl(String receivedRedirectUrl) {

        if (basicOperationsUtils.isEmpty(receivedRedirectUrl)) {
            log.info("Some issue while redirection. Looks like no such short url record found in our storage");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        try {
            log.info("Trying to redirect to: {}", receivedRedirectUrl);
            RedirectView redirectionView = new RedirectView(receivedRedirectUrl);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(redirectionView);

        } catch (Exception e) {
            log.error("Issue in redirecting to this longUrl: {}", receivedRedirectUrl);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
    }

    public String generateUniqueCode() {
        //generating using Base 64 Operation
        return Base64Ops.encode(nextIdGenerator.nextId());
    }

    public Boolean isExpired(@NotNull UrlInfoDetails urlInfoDetails) {

        if (urlInfoDetails.getUrlCreationTime().plusMinutes(urlInfoDetails.getExpirationTime()).isBefore(LocalDateTime.now())) {

            if (Boolean.TRUE.equals(urlOperationsServiceImpl.deleteThisEntry(urlInfoDetails))) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        //Expiration time not up yet
        return Boolean.FALSE;
    }

    public Boolean isValidUrl(String urlToCheck) {
        try {

            log.info("Url to be checked: {}", urlToCheck);
            if (Boolean.TRUE.equals(basicOperationsUtils.isEmpty(urlToCheck))) {
                return Boolean.FALSE;
            }

            //check if scheme is present or not
            String scheme = new URI(urlToCheck).getScheme();
            if (scheme == null) {
                log.error("Looks like no scheme provided for url: {}. Adding {} by default", urlToCheck, HTTP_SCHEME);
                urlToCheck = HTTP_SCHEME.concat(urlToCheck);
            }

            log.info("Url received: {}", urlToCheck);

            new URL(urlToCheck).toURI(); //If URl object is created without error, its valid

            //Second check with URL regex -> To check top level domains and other checks
            Matcher matcher = URL_PATTERN.matcher(urlToCheck);
            return matcher.matches();

        } catch (MalformedURLException | URISyntaxException ex) { // Any Exception while creating URL object
            log.error("Issue while creating/checking url Object: {} with error: ", urlToCheck, ex);
            return Boolean.FALSE;
        } catch (NullPointerException nullPointerException) {
            log.error("Some issue causing null throw in url: {} with error: ", urlToCheck, nullPointerException);
            return Boolean.FALSE;
        }
    }

    public Boolean isValidCustomisation(String customUrlCode) {

        //Either code is empty or it should match regex and length should be between 5 and 10
        return (customUrlCode.matches("[A-Za-z0-9_-]*")
                && ((basicOperationsUtils.isEmpty(customUrlCode)) || ((customUrlCode.length() >= 5) && (customUrlCode.length() <= 10)
        )));
    }

}
