package com.cjemison.config.messageConverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by cjemison on 6/21/17.
 */
public class JwtMessageConverter extends AbstractHttpMessageConverter<Map<String, Object>> {
  private static final String jwtKey = "secret";
  private static final byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtKey);
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
  private final ObjectMapper objectMapper;

  public JwtMessageConverter(final ObjectMapper objectMapper) {

    super(new MediaType("application", "jwt"));
    this.objectMapper = objectMapper;
  }

  @Override
  protected boolean supports(final Class<?> clazz) {
    return true;
  }

  @Override
  protected Map<String, Object> readInternal(final Class<? extends Map<String, Object>> clazz,
                                             final HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {
    final String jwt = IOUtils.toString(inputMessage.getBody());
    final Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName
          ());
    final Jws<Claims> claimsJws = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt);
    final String json = (String) claimsJws.getBody().get("payload");
    return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
    });
  }

  @Override
  protected void writeInternal(final Map<String, Object> map, final HttpOutputMessage
        outputMessage) throws
        IOException, HttpMessageNotWritableException {

    final Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName
          ());

    final String json = objectMapper.writeValueAsString(map);
    final Map<String, Object> payloadMap = new HashMap<>();
    payloadMap.put("payload", json);

    final String compactJWT = Jwts.builder().setClaims(payloadMap)
          .setSubject("data")
          .setExpiration(new DateTime().plusHours(8).toDate())
          .signWith(signatureAlgorithm, signingKey).compact();

    outputMessage.getHeaders().add(HttpHeaders.CONTENT_TYPE,
          new MediaType("application", "jwt").toString());
    outputMessage.getBody().write(compactJWT.getBytes());
  }
}
