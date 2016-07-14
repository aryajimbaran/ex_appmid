package com.numazu.export.model;


public class Model {
	
	Integer id;
	String userId;
	String content;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String toString() {
		return "Model(id="+id+", usertId="+userId+", content="+content+")";
	}

}
