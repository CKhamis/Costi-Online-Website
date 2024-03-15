package com.costi.csw9.Intercept;

import com.costi.csw9.Model.DTO.RequestReport;
import com.costi.csw9.Model.DTO.WebSpyResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

public class WebSpyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Generate the request report
        RequestReport report = new RequestReport(
                "NewSuperMarioBros",
                // todo: check if this is all correct
                request.getRemoteAddr(),
                request.getRemoteHost(),
                String.valueOf(request.getRemotePort()),
                request.getRemoteUser(),
                request.getLocale().toString(),
                request.getSession().getId(),
                request.getHeader("Cookie"),
                request.getRequestURI(),
                request.getRequestURL().toString(),
                request.getMethod(),
                request.getHeader("Accept"),
                request.getProtocol(),
                request.getScheme()
        );

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://127.0.0.1:8080/report";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<RequestReport> webSpyReport = new HttpEntity<>(report, headers);

            ResponseEntity<WebSpyResponse> webSpyResponse = restTemplate.postForEntity(url, webSpyReport, WebSpyResponse.class);

            if(Objects.requireNonNull(webSpyResponse.getBody()).is_blocked()){
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, webSpyResponse.getBody().getMessage());
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
