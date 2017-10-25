package com.zboss.wheel.exception;


public class NoSuchIntfException extends ExceptionX {

	private static final long	serialVersionUID	= 1L;

	public NoSuchIntfException() {
		super("此对象没有含有指定注解的接口");
	}

}
