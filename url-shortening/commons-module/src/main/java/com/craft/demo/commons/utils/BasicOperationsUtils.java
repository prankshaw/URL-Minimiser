package com.craft.demo.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BasicOperationsUtils {

    @Value("${admin.credentials.username}")
    private String credUser;

    @Value("${admin.credentials.password}")
    private String credPassword;

    public boolean isEmpty(@Nullable String str) {
        return ((str == null) || (str.length() == 0));
    }

    public Boolean verifyUserNameAndPassword(String userName, String passWord) {
        if (userName.equals(credUser) && passWord.equals(credPassword))
            return Boolean.TRUE;

        log.info("UserName: {}, Password: {} combination not matching.", userName, passWord);
        return Boolean.FALSE;
    }
}
