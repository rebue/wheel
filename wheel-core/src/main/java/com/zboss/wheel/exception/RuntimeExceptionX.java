package com.zboss.wheel.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeExceptionX extends RuntimeException {

	private static final long	serialVersionUID	= 1L;

	private static Logger		_logger				= LoggerFactory.getLogger(RuntimeExceptionX.class);

	public RuntimeExceptionX(String msg) {
		super(msg);
		_logger.error(msg, this);
	}

}
