package com.techelevator.view;

import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.services.AccountService;



@RunWith(MockitoJUnitRunner.class)
public class MenuServiceTest {
	
	private String BASE_URL = "asdf";
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private AccountService testService = new AccountService(BASE_URL);
	
	

	
	@Test
	public void should_view_current_balance() {
		String authToken = "asdf";
		long user_id = 2;
		Account accountToReturn = new Account();
		accountToReturn.setBalance(new BigDecimal(1));
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		HttpEntity entity = new HttpEntity(headers);
		Mockito.when(
				this.restTemplate.exchange(this.BASE_URL + "/account/" + user_id, HttpMethod.GET, entity, Account.class)		
		).thenReturn(new ResponseEntity<>(accountToReturn, HttpStatus.OK));
		
		BigDecimal ans = testService.viewCurrentBalance(authToken, user_id);
		
		Assert.assertEquals(accountToReturn.getBalance(), ans);
	}
	
	@Test
	public void should_return_transfer_history() {
		String authToken = "asdf";
		long user_id = 1;
		Transfer[] arrayToReturn = new Transfer[0];
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		HttpEntity entity = new HttpEntity(headers);
		Mockito.when(
				this.restTemplate.exchange(this.BASE_URL + "/account/" + user_id + "/transfers", HttpMethod.GET, entity, Transfer[].class)
		).thenReturn(new ResponseEntity<>(arrayToReturn, HttpStatus.OK));		
		
		Transfer[] ans = testService.getTransferHistory(authToken, user_id);
		
		Assert.assertEquals(arrayToReturn.length, ans.length);
	}
	
	@Test
	public void should_return_userName_by_accountId() {
		String authToken = "asdf";
		long user_id = 1;
		String userNameToReturn = "Bob";
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		HttpEntity entity = new HttpEntity(headers);
		Mockito.when(
				this.restTemplate.exchange(this.BASE_URL + "/accounts/" + user_id, HttpMethod.GET, entity, String.class)
		).thenReturn(new ResponseEntity<>(userNameToReturn, HttpStatus.OK));
		
		String ans = testService.getUsernameByAccount(authToken, user_id);
		
		Assert.assertEquals(userNameToReturn, ans);
		
	}
	
	@Test
	public void should_return_status_desc() {
		String authToken = "asdf";
		int userId = 1;
		String statusToReturn = "Pass";
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		HttpEntity entity = new HttpEntity(headers);
		Mockito.when(
				this.restTemplate.exchange(this.BASE_URL + "/transfers/status/" + userId, HttpMethod.GET, entity, String.class)
		).thenReturn(new ResponseEntity<>(statusToReturn, HttpStatus.OK));
		
		String ans = testService.getTransferStatus(authToken, userId);
		
		Assert.assertEquals(statusToReturn, ans);
	}
	
	@Test
	public void should_return_type_desc() {
		String authToken = "asdf";
		int id = 1;
		String typeToReturn = "Pass";
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		HttpEntity entity = new HttpEntity(headers);
		Mockito.when(
				this.restTemplate.exchange(this.BASE_URL + "/transfers/type/" + id, HttpMethod.GET, entity, String.class)
		).thenReturn(new ResponseEntity<>(typeToReturn, HttpStatus.OK));
		
		String ans = testService.getTransferType(authToken, id);
		
		Assert.assertEquals(typeToReturn, ans);
	}
	
	@Test
	public void should_return_all_users() {
		String authToken = "asdf";
		int userId = 1;
		User[] arrayToReturn = new User[0];
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		HttpEntity entity = new HttpEntity(headers);
		Mockito.when(
				this.restTemplate.exchange(this.BASE_URL + "/accounts", HttpMethod.GET, entity, User[].class)
		).thenReturn(new ResponseEntity<>(arrayToReturn, HttpStatus.OK));
		
		User[] ans = testService.getAllUsers(authToken);
		
		Assert.assertEquals(arrayToReturn.length, ans.length);
	}
	
	@Test
	public void should_return_accountId_by_userId() {
		String authToken = "asdf";
		long userId = 1;
		long longToReturn = 2;
		Account account = new Account();
		account.setAccountId(longToReturn);
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		HttpEntity entity = new HttpEntity(headers);
		Mockito.when(
				this.restTemplate.exchange(this.BASE_URL + "/account/" + userId, HttpMethod.GET, entity, Account.class)
		).thenReturn(new ResponseEntity<>(account, HttpStatus.OK));
		
		long ans = testService.getAccountFromUserId(authToken, userId);
		
		Assert.assertEquals(longToReturn, ans);
	}
	
	@Test
	public void should_return_all_pending_transfers() {
		String authToken = "asdf";
		long user_id = 1;
		Transfer[] arrayToReturn = new Transfer[0];
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		HttpEntity entity = new HttpEntity(headers);
		Mockito.when(
				this.restTemplate.exchange(this.BASE_URL + "/account/" + user_id + "/transfers/pending", HttpMethod.GET, entity, Transfer[].class)
		).thenReturn(new ResponseEntity<>(arrayToReturn, HttpStatus.OK));		
		
		Transfer[] ans = testService.getPendingTransfers(authToken, user_id);
		
		Assert.assertEquals(arrayToReturn.length, ans.length);
	}

}
