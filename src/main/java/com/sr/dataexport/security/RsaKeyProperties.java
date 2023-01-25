package com.sr.dataexport.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPublicKey;

/**
 * @ClassName RsaKeyProperties
 * @Description This record is used to store the RSA keys so they are immutable.
 */
@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(RSAPublicKey publicKey) {
}


