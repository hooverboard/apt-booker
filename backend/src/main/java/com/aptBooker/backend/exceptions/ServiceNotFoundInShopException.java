package com.aptBooker.backend.exceptions;

public class ServiceNotFoundInShopException extends RuntimeException {
    public ServiceNotFoundInShopException(String message) {
        super(message);
    }
}
