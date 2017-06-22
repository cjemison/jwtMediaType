package com.cjemison.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by cjemison on 6/21/17.
 */
@RestController
@RequestMapping(value = "/v1")
public class ExampleController {
  private static final byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("secret");
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  @RequestMapping(value = "/jwt", method = RequestMethod.GET, produces = "application/jwt")
  public ResponseEntity<?> gen() {

    final Map<String, Object> map = new HashMap<>();
    map.put("payload", UUID.randomUUID().toString());

    return ResponseEntity.ok(map);
  }

  @RequestMapping(value = "/jwt", method = RequestMethod.POST,
        consumes = "application/jwt", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<?> post(@RequestBody final Map<String, Object> map) {
    return ResponseEntity.ok(map);
  }
}
