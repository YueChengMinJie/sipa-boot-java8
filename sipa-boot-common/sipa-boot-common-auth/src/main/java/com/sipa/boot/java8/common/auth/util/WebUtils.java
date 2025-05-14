package com.sipa.boot.java8.common.auth.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.SavedRequest;

import com.sipa.boot.java8.common.auth.model.JwtAuthenticationToken;
import com.sipa.boot.java8.common.auth.model.User;
import com.sipa.boot.java8.common.auth.model.UserContext;

/**
 * @author zhouxiajie
 * @date 2018/2/6
 */
public class WebUtils {
    private static Logger logger = LoggerFactory.getLogger(WebUtils.class);

    private static final String XML_HTTP_REQUEST = "XMLHttpRequest";

    private static final String X_REQUESTED_WITH = "X-Requested-With";

    private static final String CONTENT_TYPE = "Content-type";

    private static final String CONTENT_TYPE_JSON = "application/json";

    public static boolean isAjax(HttpServletRequest request) {
        return XML_HTTP_REQUEST.equals(request.getHeader(X_REQUESTED_WITH));
    }

    public static boolean isAjax(SavedRequest request) {
        return request.getHeaderValues(X_REQUESTED_WITH).contains(XML_HTTP_REQUEST);
    }

    public static boolean isContentTypeJson(SavedRequest request) {
        return request.getHeaderValues(CONTENT_TYPE).contains(CONTENT_TYPE_JSON);
    }

    public static User getCurrentUser() {
        try {
            JwtAuthenticationToken jat = (JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
            UserContext uc = (UserContext)jat.getPrincipal();
            return uc.getUser();
        } catch (Exception ex) {
            logger.warn("no user context!");
            return null;
        }
    }
}
