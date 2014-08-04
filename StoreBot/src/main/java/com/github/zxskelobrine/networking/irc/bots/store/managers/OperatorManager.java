package com.github.zxskelobrine.networking.irc.bots.store.managers;

import java.util.ArrayList;
import java.util.List;

public class OperatorManager {

	private static List<String> operators = new ArrayList<String>();

	public static synchronized void addOperator(String user) {
		synchronized (operators) {
			operators.add(user);
		}
	}

	public static synchronized void removeOperator(String user) {
		synchronized (operators) {
			operators.remove(operators.indexOf(user));
		}
	}

	public static synchronized boolean isOperator(String user) {
		if (user.equals("Vitineth")) {
			return true;
		}
		for (String operator : operators) {
			if (operator.equals(user)) {
				return true;
			}
		}
		return false;
	}
}
