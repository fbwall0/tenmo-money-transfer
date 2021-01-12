package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.util.InputMismatchException;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;
import com.techelevator.view.MenuService;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private MenuService menu;


    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
		this.menu = new MenuService(System.in, System.out, currentUser, accountService);
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		System.out.println("Your current account balance is: $" + accountService.viewCurrentBalance(currentUser.getToken(), (long) currentUser.getUser().getId()));
	}

	private void viewTransferHistory() {
		Transfer[] transfers = accountService.getTransferHistory(currentUser.getToken(), (long) currentUser.getUser().getId());
		if (transfers == null) {
			transfers = new Transfer[0];
		}
		if (transfers.length != 0) {
			Transfer transfer = (Transfer) menu.getChoiceFromOptions(currentUser, transfers);
			if (transfer != null) {
				viewTransferDetails(transfer);
			}
		} else {
			System.out.println("----------------------------");
			System.out.println("No previous transfers found");
			System.out.println("----------------------------");
		}
	}

	private void viewTransferDetails(Transfer transfer) {
		System.out.println("------------------------------");
		System.out.println("Transfer Details");
		System.out.println("------------------------------");
		System.out.println(" Id: " + transfer.getTransferId());
		System.out.println(" From: " + accountService.getUsernameByAccount(currentUser.getToken(), transfer.getAccountFrom()));
		System.out.println(" To: " + accountService.getUsernameByAccount(currentUser.getToken(), transfer.getAccountTo()));
		System.out.println(" Type: " + accountService.getTransferType(currentUser.getToken(), transfer.getTransferType()));
		System.out.println(" Status: " + accountService.getTransferStatus(currentUser.getToken(), transfer.getTransferStatus()));
		System.out.println(" Amount: $" + transfer.getAmount());
	}

	private void viewPendingRequests() {
		Transfer[] pendingTransfers = accountService.getPendingTransfers(currentUser.getToken(), currentUser.getUser().getId());
		Transfer choice = menu.getChoiceFromPendingOptions(currentUser, pendingTransfers);
		if (choice != null) {
			boolean approvable = false;
			String prompt = "";
			if (accountService.viewCurrentBalance(currentUser.getToken(), currentUser.getUser().getId()).compareTo(choice.getAmount()) >= 0) {
				approvable = true;
				prompt += "1: Approve\n2: Reject\n0: Don't approve or ";
			}
			else {
				prompt += "1: Reject\n0: Don't ";
			}
			prompt += "reject\n------------------------------\nPlease choose an option";
			String input = menu.getUserInput(prompt);
			int whatdo;
			try {
				whatdo = Integer.valueOf(input);
				if ((whatdo > 1 && !approvable) || whatdo > 2 || whatdo < 0) {
					whatdo = 0;
				}
			} catch (NumberFormatException ex) {
				whatdo = 0;
			}
			if (whatdo == 0) {
				System.out.println("No action taken");
			} else if (whatdo == 1 && approvable) {
				System.out.println("Transfer approved");
				choice.setTransferStatus(2);
			} else {
				System.out.println("Transfer Rejected");
				choice.setTransferStatus(3);
			}
			accountService.updateTransferStatus(currentUser.getToken(), choice);
		}
	}

	private void sendBucks() {
		User[] users = accountService.getAllUsers(currentUser.getToken());
		User choice = menu.getChoiceFromOptions(currentUser, users);
		if (choice != null) {
			String input = menu.getUserInput("Enter Amount");
			try {
				if (Double.valueOf(input) < 0) {
					throw new NumberFormatException();
				} else if((new BigDecimal(input)).compareTo(new BigDecimal(0)) == 0) {
					throw new StringIndexOutOfBoundsException();
				}
				Transfer transfer = new Transfer();
				transfer.setTransferType(2);
				transfer.setAccountFrom(accountService.getAccountFromUserId(currentUser.getToken(), currentUser.getUser().getId()));
				transfer.setAccountTo(accountService.getAccountFromUserId(currentUser.getToken(), choice.getId()));
				transfer.setAmount(new BigDecimal(input));
				accountService.initiateTransfer(currentUser.getToken(), transfer);
			} catch(NumberFormatException ex) {
				System.out.println("Not a valid amount.");
			} catch(StringIndexOutOfBoundsException ex) {
				System.out.println("Cannot transfer $0");
			}
		}
	}

	private void requestBucks() {
		User[] users = accountService.getAllUsers(currentUser.getToken());
		User choice = menu.getChoiceFromOptions(currentUser, users);
		if (choice != null) {
			String input = menu.getUserInput("Enter Amount");
			try {
				if (Double.valueOf(input) < 0) {
					throw new NumberFormatException();
				} else if((new BigDecimal(input)).compareTo(new BigDecimal(0)) == 0) {
					throw new StringIndexOutOfBoundsException();
				}
				Transfer transfer = new Transfer();
				transfer.setTransferType(1);
				transfer.setAccountTo(accountService.getAccountFromUserId(currentUser.getToken(), currentUser.getUser().getId()));
				transfer.setAccountFrom(accountService.getAccountFromUserId(currentUser.getToken(), choice.getId()));
				transfer.setAmount(new BigDecimal(input));
				accountService.initiateTransfer(currentUser.getToken(), transfer);
			} catch(NumberFormatException ex) {
				System.out.println("Not a valid amount.");
			} catch(StringIndexOutOfBoundsException ex) {
				System.out.println("Cannot transfer $0");
			}
		}
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
