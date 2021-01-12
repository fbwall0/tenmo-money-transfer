package com.techelevator.tenmo.services;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

public class AccountService {
	
	private String BASE_URL;
	private RestTemplate restTemplate;
	
	public AccountService(String url) {
		BASE_URL = url;
		restTemplate = new RestTemplate();
	}
	
	public BigDecimal viewCurrentBalance(String authToken, long user_id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity <Account> response = restTemplate.exchange(BASE_URL + "/account/" + user_id, HttpMethod.GET, entity, Account.class);
		
		return response.getBody().getBalance();
	}

	public Transfer[] getTransferHistory(String token, long id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity <Transfer[]> response = restTemplate.exchange(BASE_URL + "/account/" + id + "/transfers", HttpMethod.GET, entity, Transfer[].class);
		
		return response.getBody();
	}
	
	public String getUsernameByAccount(String token, long id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity <String> response = restTemplate.exchange(BASE_URL + "/accounts/" + id, HttpMethod.GET, entity, String.class);
		
		return response.getBody();
	}
	
	public String getTransferStatus(String token, int id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity <String> response = restTemplate.exchange(BASE_URL + "/transfers/status/" + id, HttpMethod.GET, entity, String.class);
		return response.getBody();
	}
	
	public String getTransferType(String token, int id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity <String> response = restTemplate.exchange(BASE_URL + "/transfers/type/" + id, HttpMethod.GET, entity, String.class);
		return response.getBody();
	}
	
	public User[] getAllUsers(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity <User[]> response = restTemplate.exchange(BASE_URL + "/accounts", HttpMethod.GET, entity, User[].class);
		return response.getBody();
	}
	
	public void initiateTransfer(String token, Transfer transfer) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);
		HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
		restTemplate.exchange(BASE_URL + "/transfers", HttpMethod.POST, entity, Transfer.class);
	}
	
	public Long getAccountFromUserId(String token, long id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity <Account> response = restTemplate.exchange(BASE_URL + "/account/" + id, HttpMethod.GET, entity, Account.class);
		return response.getBody().getAccountId();
	}
	
	public Transfer[] getPendingTransfers(String token, long id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity <Transfer[]> response = restTemplate.exchange(BASE_URL + "/account/" + id + "/transfers/pending", HttpMethod.GET, entity, Transfer[].class);
		
		return response.getBody();
	}
	
	public void updateTransferStatus(String token, Transfer transfer) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);
		HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
		restTemplate.exchange(BASE_URL + "/transfers", HttpMethod.PUT, entity, Transfer.class);
	}

}
