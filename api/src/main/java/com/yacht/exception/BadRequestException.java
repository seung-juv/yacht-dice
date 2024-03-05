package com.yacht.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

	private String message;

	public BadRequestException() {
	}

	public BadRequestException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
