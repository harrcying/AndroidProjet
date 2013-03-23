package fr.umlv.andex.data;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	private String password;
	private String user;
	private long userId;
	private String token;
	
	private List<String> examList;
	
	public User(){
		examList=new ArrayList<String>();
	}
	public void addExam(String exam){
		examList.add(exam);
	}
	
	
	// SETTER & GETTER
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public List<String> getExamList() {
		return examList;
	}
	public void setExamList(List<String> examList) {
		this.examList = examList;
	}
	
}
