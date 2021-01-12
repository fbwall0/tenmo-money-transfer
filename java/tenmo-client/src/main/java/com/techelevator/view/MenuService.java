package com.techelevator.view;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.services.AccountService;

public class MenuService {

	private PrintWriter out;
	private Scanner in;
	private AuthenticatedUser currentUser;
	private AccountService accountService;

	public MenuService(InputStream input, OutputStream output, AuthenticatedUser currentUser, AccountService accountService) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
		this.currentUser = currentUser;
		this.accountService = accountService;
	}

	public Transfer getChoiceFromOptions(AuthenticatedUser currentUser, Transfer[] options) {
		this.currentUser = currentUser;
		Transfer choice = null;
		long selectedOption = -1;
		while (selectedOption == -1) {
			displayMenuOptions(options);
			selectedOption = getChoiceFromUserInput(options);
		}
		for(Transfer results : options) {
			if (results.getTransferId() == selectedOption) {
				choice = results;
			}
		}
		out.println();
		return choice;
	}

	private Long getChoiceFromUserInput(Transfer[] options) {
		long choice = -1;
		String userInput = in.nextLine();
		try {
			long selectedOption = Long.valueOf(userInput);
			if (selectedOption == 0) {
				choice = selectedOption;
			}
			for(Transfer results : options) {
				if (results.getTransferId() == selectedOption) {
					choice = selectedOption;
				}
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == -1) {
			out.println("\n*** " + userInput + " is not a valid option ***\n");
		}
		return choice;
	}

	private void displayMenuOptions(Transfer[] options) {
		out.println("--------------------------------------------------------");
		out.println("Transfers");
		out.printf("%-10s%-30s%-10s\n", "ID", "From/To", "Amount");
		out.println("--------------------------------------------------------");
		for (Transfer transfer : options) {
			out.printf("%-10d", (int) transfer.getTransferId());
			if (currentUser.getUser().getUsername().equals(accountService.getUsernameByAccount(currentUser.getToken(), transfer.getAccountTo()))) { //if the money goes from them to you
				String otherAccount = accountService.getUsernameByAccount(currentUser.getToken(), transfer.getAccountFrom());
				out.printf("%-30s", "From: " + otherAccount);
			} else { //if the money goes from you to them
				String otherAccount = accountService.getUsernameByAccount(currentUser.getToken(), transfer.getAccountTo());
				out.printf("%-30s", "To: " + otherAccount);
			}
			out.printf("$%-10.2f\n", transfer.getAmount());
		}
		out.println("--------------------------------------------------------");
		out.print("Please enter transfer ID to view details (0 to cancel): ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println("\n*** " + userInput + " is not valid ***\n");
			}
		} while(result == null);
		return result;
	}
	
	private void displayMenuOptions(User[] options) {
		out.println("--------------------------------------------------------");
		out.println("Users");
		out.printf("%-10s%s\n", "ID", "Name");
		out.println("--------------------------------------------------------");
		for (User user : options) {
			if (user.getId() != currentUser.getUser().getId()) {
				out.printf("%-10d%s\n", (int) user.getId(), user.getUsername());
			}
		}
		out.println("--------------------------------------------------------");
		out.print("Enter ID of user you are sending to (0 to cancel): ");
		out.flush();
	}
	
	private Long getChoiceFromUserInput(User[] options) {
		long choice = -1;
		String userInput = in.nextLine();
		try {
			long selectedOption = Long.valueOf(userInput);
			if (selectedOption == 0) {
				choice = selectedOption;
			} else if(selectedOption == currentUser.getUser().getId()) {
				choice = -1;
			}
			for(User results : options) {
				if (results.getId() == selectedOption) {
					choice = selectedOption;
				}
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == -1) {
			out.println("\n*** " + userInput + " is not a valid option ***\n");
		}
		return choice;
	}
	
	public User getChoiceFromOptions(AuthenticatedUser currentUser, User[] options) {
		this.currentUser = currentUser;
		User choice = null;
		long selectedOption = -1;
		while (selectedOption == -1) {
			displayMenuOptions(options);
			selectedOption = getChoiceFromUserInput(options);
		}
		for(User results : options) {
			if (results.getId() == selectedOption) {
				choice = results;
			}
		}
		out.println();
		return choice;
	}
	
	public Transfer getChoiceFromPendingOptions(AuthenticatedUser currentUser, Transfer[] options) {
		this.currentUser = currentUser;
		Transfer choice = null;
		long selectedOption = -1;
		while (selectedOption == -1) {
			displayPendingMenuOptions(options);
			selectedOption = getChoiceFromUserInput(options);
		}
		for(Transfer results : options) {
			if (results.getTransferId() == selectedOption) {
				choice = results;
			}
		}
		out.println();
		return choice;
	}

	private void displayPendingMenuOptions(Transfer[] options) {
		out.println("--------------------------------------------------------");
		out.println("Pending Transfers");
		out.printf("%-10s%-30s%-10s\n","ID","To","Amount");
		out.println("--------------------------------------------------------");
		for (Transfer transfer : options) {
			out.printf("%-10d%-30s$%-10.2f\n", (int) transfer.getTransferId(), accountService.getUsernameByAccount(currentUser.getToken(), transfer.getAccountTo()), transfer.getAmount());
		}
		out.println("--------------------------------------------------------");
		out.print("Please enter transfer ID to approve/reject (0 to cancel): ");
		out.flush();
	}
	
	
}
