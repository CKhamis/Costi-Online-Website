package com.costi.csw9.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@Getter
@Setter
public class RequestReport {
    // Identification
    String domain_id;

    // Client info
    String ip;
    String client_port;
    String client_user;
    String client_locale;

    // Session info
    String session;
    String cookies;

    // Request info
    String request_uri;
    String request_url;
    String request_method;
    String request_header;
    String request_protocol;
    String request_scheme;

    // Browser info
    String user_agent;
}
