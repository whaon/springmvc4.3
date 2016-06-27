package com.my.entity;

public class ViewChain {
	private String viewName;
	private ViewChain next;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public ViewChain getNext() {
		return next;
	}
	public void setNext(ViewChain next) {
		this.next = next;
	}
}
