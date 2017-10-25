package com.zboss.wheel.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionX extends Exception {

	private static final long	serialVersionUID	= 1L;

	private static Logger		_logger				= LoggerFactory.getLogger(ExceptionX.class);

	public ExceptionX(String msg) {
		super(msg);
		_logger.error(msg, this);
	}

}
