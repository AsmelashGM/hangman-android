package server.controller;

import server.model.HangmanModel;
import server.view.View;

public class HangmanController {
	
	private HangmanModel model;
	private View view;
	public int score = 0;
	
	public HangmanController() {}
	
	public String instantiateTheGame() {
		model = new HangmanModel();
		view = new View();
		model.setDictionary(); //build a dictionary
		
		if(model.getErrorMessage()!=null) 
			view.println(model.getErrorMessage());
		int randomIndex = (int)(Math.random() * (model.getDictionary().length-1) + 0);
		
		model.setSlectedWord(model.getDictionary()[randomIndex].toLowerCase()); //Convert the selected word to lower case.
		String selectWord = model.getSelectedWord();
		//view.println(selectWord);
		
		model.setRemainingAttempts(selectWord.length());
		String userProgress = "";
		for(int i=1; i<=selectWord.length(); i++) {
			userProgress += "-";
		}
		model.setUserProgress(userProgress);
		model.setSuccess(false);
		
		//Set to current value
		model.setScore(score);
		
		//Message to return 
		String message = model.getUserProgress() + ":"
					   + model.getScore() + ":"
					   + model.getRemainingAttempts() + ":";
		
		return message;
	}
	public String onGame(String userGuess) {
		userGuess = userGuess.toLowerCase(); //Convert user input to lower case.
		
		if (userGuess.length() > 1){
			if(model.getSelectedWord().equals(userGuess)) {
				model.setScore(model.getScore()+1);
				model.setUserProgress(userGuess);
				model.setSuccess(true);
			}
			else {
				model.setRemainingAttempts(model.getRemainingAttempts()-1);
			}
		}
		else if(userGuess.length() == 1) {
			if(!model.getSelectedWord().contains(userGuess)) { 
				model.setRemainingAttempts(model.getRemainingAttempts()-1);
			}
			else{
				for (int i=0; i<model.getSelectedWord().length(); i++) {
					if(model.getSelectedWord().charAt(i) == userGuess.charAt(0)) {
						char[] userProgress = model.getUserProgress().toCharArray();
						userProgress[i] = userGuess.charAt(0);
						model.setUserProgress(String.valueOf(userProgress));
					}
				}
				if(model.getUserProgress().equals(model.getSelectedWord())) {//!model.getUserProgress().contains("-")
					model.setScore(model.getScore()+1);
					model.setSuccess(true);
				}
			}
		}
		
		//Message to return 
		String attempts = String.valueOf(model.getRemainingAttempts());
		if(model.getSuccess()) attempts = "-";
		if(model.getRemainingAttempts()==0) //Decrement score
			model.setScore(model.getScore()-1);
			
		String message = model.getUserProgress() + ":"
					   + model.getScore()+ ":"
					   + attempts + ":";
		
		if(!model.getSuccess()) {
			if(model.getRemainingAttempts() == 0) {
				score = model.getScore();
				message += "Game over!";
			}
			else message += "You're playing...";
		}
		else {
			score = model.getScore();
			message += "Success!";
		}
			
		return message;
	}
}
