package me.egg82.fetcharr;

import kong.unirest.core.Proxy;
import kong.unirest.core.Unirest;
import me.egg82.fetcharr.env.ConfigVars;
import me.egg82.fetcharr.env.LogMode;
import me.egg82.fetcharr.web.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        setupUnirest();
    }

    private static void setupUnirest() {
        LogMode logMode = LogMode.getMode(ConfigVars.getVar(ConfigVars.LOG_MODE), LogMode.INFO);
        Unirest.config().interceptor(new LoggingInterceptor(logMode));

        String proxyHost = ConfigVars.getVar(ConfigVars.PROXY_HOST);
        int proxyPort = ConfigVars.getVar(ConfigVars.PROXY_PORT, 0);
        if (proxyHost != null && proxyPort > 0) {
            Unirest.config().proxy(new Proxy(proxyHost, proxyPort));
        }

        int connectTimeout = ConfigVars.getVar(ConfigVars.CONNECT_TIMEOUT, 0);
        if (connectTimeout > 0) {
            Unirest.config().connectTimeout(connectTimeout);
        }

        int requestTimeout = ConfigVars.getVar(ConfigVars.REQUEST_TIMEOUT, 0);
        if (requestTimeout > 0) {
            Unirest.config().requestTimeout(requestTimeout);
        }

        int connectTTL = ConfigVars.getVar(ConfigVars.CONNECT_TTL, -1);
        if (connectTTL > -1) {
            Unirest.config().connectionTTL(connectTTL, TimeUnit.MILLISECONDS);
        }

        boolean verifyCerts = ConfigVars.getVar(ConfigVars.VERIFY_CERTS, true);
        Unirest.config().disableHostNameVerification(!verifyCerts);
        Unirest.config().verifySsl(verifyCerts);

        Unirest.config().retryAfter(true);
    }
}
