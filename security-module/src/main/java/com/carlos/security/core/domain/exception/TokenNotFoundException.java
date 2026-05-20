package com.carlos.security.core.domain.exception;

public class TokenNotFoundException extends RuntimeException {
  public TokenNotFoundException(String message) {
    super(message);
  }
}
