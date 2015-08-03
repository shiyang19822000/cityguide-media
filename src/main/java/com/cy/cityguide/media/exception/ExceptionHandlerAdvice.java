package com.cy.cityguide.media.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

	public class Response {

		private String message;

		public Response(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	@ExceptionHandler(value = { MissingServletRequestParameterException.class,
			ServletRequestBindingException.class, TypeMismatchException.class,
			HttpMessageNotReadableException.class, BindException.class,
			MethodArgumentNotValidException.class,
			MissingServletRequestPartException.class,
			BadRequestBusinessException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody Response processBadRequest(Exception exception) {
		return new Response(exception.getMessage());
	}

	@ExceptionHandler(value = { NoHandlerFoundException.class,
			NoSuchRequestHandlingMethodException.class,
			NotFoundBusinessException.class })
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody Response processNotFound(Exception exception) {
		return new Response(exception.getMessage());
	}

	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	public @ResponseBody Response processMethodNotAllowed(Exception exception) {
		return new Response(exception.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Response processInternalServerError(Exception exception) {
		return new Response(exception.getMessage());
	}

}
