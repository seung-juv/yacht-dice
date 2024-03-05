package com.yacht.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

	private final String message;

	public NotFoundException() {
		this.message = ("Not found Records.");
	}

	public NotFoundException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
