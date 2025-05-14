package com.sipa.boot.java8.common.common.exception;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import springfox.documentation.annotations.ApiIgnore;

/**
 * @author caszhou
 * @date 2021/10/29
 */
@ApiIgnore
public class CustomizedErrorController extends BasicErrorController {
    private static final String STATUS_SYMBOL = "status";

    private static final String ERROR_SYMBOL = "error";

    private final ErrorAttributes errorAttributes;

    public CustomizedErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties,
        List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorProperties, errorViewResolvers);

        this.errorAttributes = errorAttributes;
    }

    @Override
    @RequestMapping(produces = "text/html")
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = getStatus(request);
        ServletWebRequest requestAttributes = new ServletWebRequest(request, response);
        Map<String, Object> model = Collections.unmodifiableMap(this.errorAttributes
            .getErrorAttributes(requestAttributes, isIncludeStackTrace(request, MediaType.TEXT_HTML)));

        if (model.containsKey(STATUS_SYMBOL)) {
            response.setStatus((int)model.get(STATUS_SYMBOL));
        } else {
            response.setStatus(status.value());
        }

        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        return modelAndView == null ? new ModelAndView(ERROR_SYMBOL, model) : modelAndView;
    }

    @Override
    @ResponseBody
    @RequestMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        // try to resolve status code from self-defined first
        HttpStatus status =
            Optional.ofNullable(body.get(STATUS_SYMBOL)).map(this::resolvePreDefinedStatus).orElse(getStatus(request));
        // only return 'ErrorEntity' in error attributes map
        return new ResponseEntity<>(body.get(ERROR_SYMBOL), status);
    }

    private HttpStatus resolvePreDefinedStatus(Object statusCode) {
        try {
            return HttpStatus.valueOf((Integer)statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
