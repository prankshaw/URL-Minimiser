package com.craft.demo.url.operations.repository;

import com.craft.demo.url.operations.entity.UrlInfoDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlInfoRepository extends JpaRepository<UrlInfoDetails, String> {

    Optional<UrlInfoDetails> findByShortUrl(String shortUrl);
}
