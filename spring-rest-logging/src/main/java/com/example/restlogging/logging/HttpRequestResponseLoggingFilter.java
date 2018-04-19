package com.example.restlogging.logging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

/**
 * This is basically the same as
 * {@link org.springframework.web.filter.CommonsRequestLoggingFilter} with
 * additional features: 1) print in info level, 2) print request method, 3)
 * print response body.
 * 
 * @author Yu-Hua Chang
 * @see {@link org.springframework.web.filter.CommonsRequestLoggingFilter}
 * @see <a href=
 *      "https://stackoverflow.com/questions/8933054/how-to-read-and-copy-the-http-servlet-response-output-stream-content-for-logging">This
 *      stackoverflow link</a>
 */
public class HttpRequestResponseLoggingFilter extends AbstractRequestLoggingFilter {

    public HttpRequestResponseLoggingFilter() {
        setIncludeClientInfo(true);
        setIncludeQueryString(true);
        setIncludeHeaders(true);
        setIncludePayload(true);
        setMaxPayloadLength(50000);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // check if this is the first request
        boolean isFirstRequest = !isAsyncDispatch(request);

        // prepare the request to use
        HttpServletRequest requestToUse = request;

        // when required, if request is not cached, create a request cache
        // wrapper to retrieve request content
        if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
            requestToUse = new HttpServletRequestCopier(request);
        }

        // when required, print request info before processing
        boolean shouldLog = shouldLog(requestToUse);
        if (shouldLog && isFirstRequest) {
            logger.info(createMessage(requestToUse, "API Request [", "]"));
        }

        // prepare response wrapper
        HttpServletResponseCopier responseToUse = new HttpServletResponseCopier(response);

        try {
            // do processing
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            // when required, print response after processing
            if (shouldLog && !isAsyncStarted(requestToUse)) {
                logger.info(createResponseMessage(responseToUse, "API Response [", "]"));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return logger.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);

        msg.append("method=").append(request.getMethod());
        msg.append(";uri=").append(request.getRequestURI());

        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                msg.append('?').append(queryString);
            }
        }

        if (isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                msg.append(";client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append(";session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append(";user=").append(user);
            }
        }

        if (isIncludeHeaders()) {
            msg.append(";headers=").append(new ServletServerHttpRequest(request).getHeaders());
        }

        if (isIncludePayload()) {
            HttpServletRequestCopier wrapper = WebUtils.getNativeRequest(request, HttpServletRequestCopier.class);
            if (wrapper != null) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    int length = Math.min(buf.length, getMaxPayloadLength());
                    String payload;
                    try {
                        payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
                    } catch (UnsupportedEncodingException ex) {
                        payload = "[unknown]";
                    }
                    msg.append(";payload=").append(payload);
                }
            }
        }

        msg.append(suffix);
        return msg.toString();
    }

    protected String createResponseMessage(HttpServletResponse response, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);

        msg.append("statusCode=").append(response.getStatus());
        
        String contentType = response.getContentType();
        if (contentType != null) {
            msg.append(";contentType=").append(contentType);
        }

        boolean isIncludePayload = isIncludePayload();
        
        if ("application/octet-stream".equals(response.getContentType())) {
            isIncludePayload = false;
        } else if ("image/gif".equals(response.getContentType())) {
            isIncludePayload = false;
        }
        
        if (isIncludePayload) {
            HttpServletResponseCopier wrapper = WebUtils.getNativeResponse(response, HttpServletResponseCopier.class);
            if (wrapper != null) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    int length = Math.min(buf.length, getMaxPayloadLength());
                    String payload;
                    try {
                        payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
                    } catch (UnsupportedEncodingException ex) {
                        payload = "[unknown]";
                    }
                    msg.append(";payload=").append(payload);
                }
            }
        }

        msg.append(suffix);
        return msg.toString();
    }
}
