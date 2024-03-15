package com.costi.csw9.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RequestReport {
    // Client info
    String ip;
    String clientHost;
    String clientPort;
    String clientUser;
    String clientLocale;

    // Session info
    String session;
    String cookies;

    // Request info
    String requestURI;
    String requestURL;
    String requestMethod;
    String requestHeader;
    String requestProtocol;
    String requestScheme;
}
