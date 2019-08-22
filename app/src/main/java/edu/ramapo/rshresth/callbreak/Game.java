/**/
/*
 * Every round will start after distribution of the cards.
 * Every player will get 13 cards.
 * If any player do not get any trump card (spades) or at least one card greater rank than 10, then dealer will distribute the cards again.
 * After successful distribution of cards, players have to give their calls one by one in anti-clockwise direction. 
 * There should be 11 minimum calls (total of all player's calls) from all players.
 * Player who call first,he will throw the first card.
 * Next player should throw the card of greater value than previous card of led suit.
 * If player does not have greater value of card then he can throw any card of led suit.
 * If player does not have led suit then he can throw the trump card.
 * If player does not have led suit and trump card then he can throw card of any suit.
 * In one hand, player who will throw the highest priority card,he will win the hand and get one point.
 * If player not able to get points equals to his call , then he will get minus points equal to his call.
 * Player who will get 20 points first, win the game.
 */
/**/
package edu.ramapo.rshresth.callbreak;

import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class Game {
	//member variables which are instances of Deck and Players
	private Deck gameDeck;
	private Player player_me;
	private Player opponent_one;
	private Player opponent_two;
	private Player opponent_three;

	//required to manage the turn
	private Player[] playerList;

	//current player who is suppose to set contract or play
	private int currentTurnNum;

	//suit played by the player of first turn
	private char currentHandSuit;

	//variable to track how many player played their hand for the given round
	private int countHandPlayed;

	//multi array to store the current played card by each players
	private String currentPlayedCards[];

	//variable to track the best hand out of the 4 players
	private int bestHandIndex;

	//vector of vector of int to store the score of each player and from each round
	private double scoreBoard[][];
	
	//round of the game
    private int round;

    //constant value for the max number of rounds to be played
	static final int MAX_ROUND = 2;

	/**/
	/*
	Game()
	NAME
        Game
	SYNOPSIS
		public Game()
	DESCRIPTION
		Constructor
		The function will deal new cards while checking for the distribution
		protocol. ALso initializes all the member variables and sets the turn of the first
		player.
	RETURNS

	AUTHOR
		Rojan Shrestha
	DATE
        1:12pm 08/19/2019
	*/
	/**/
	public Game() {

		//loop to keep dealing the cards until distribution follows spade or high card rule
		do{ newDealCards(); } while (distributeAgain());

		//array to store the current played card by the player
		currentPlayedCards = new String[4];

		// Generate random integers in range 0 to 3
		Random rand = new Random();
		currentTurnNum = rand.nextInt(4);

		//set the hand played num to 0 at the start of the game
		countHandPlayed = 0;

		//set the best Hand to currentTurn
		bestHandIndex = currentTurnNum;

		//initialize the array of array with 4 columns
		scoreBoard = new double[MAX_ROUND][4];

        //set initial round to 0 for easier manipulation of containers
		round = 0;

    }

	/**/
	/*
	newDealCards()
	NAME
        newDealCards
	SYNOPSIS
		public void newDealCards()
	DESCRIPTION
		The function will get 4 cards from deck to player hands and table
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        1:12pm 08/19/2019
	*/
	/**/
	public void newDealCards(){

		//Create new instance of Deck
		gameDeck = new Deck(true);

		//players
		player_me =  new Player(false);
		opponent_one = new Player(true);
		opponent_two = new Player(true);
		opponent_three = new Player(true);

		//list all the players in the array
		playerList = new Player[]{player_me,opponent_one,opponent_two,opponent_three};

		//print in console to test the cards
		gameDeck.printDeckCards();

        //Distribute card from the deck to table and hands
        for(int i =0; i<13; i++) {
        	for(int j =0; j<4;j++){
				playerList[currentTurnNum].storeHand(gameDeck.getNewCard());
				turnChange();
			}
        }

        //sort for easier access of the cards
		for(int j =0; j<4;j++){ playerList[j].sortHand();}

		//print in console to test the distribution of the cards
		printPlayerHand();

    }

	//
	/**/
	/*
	distributeAgain()
	NAME
        distributeAgain
	SYNOPSIS
		public boolean distributeAgain()
	DESCRIPTION
		The function will check if each player gets hand per the distribution protocol.
		Each player should have at least one face card or a spade card
	RETURNS
		Returns true if distribution protocol is passed
	AUTHOR
		Rojan Shrestha
	DATE
        1:16pm 08/19/2019
	*/
	/**/
	public boolean distributeAgain(){
		//loop each player to check for the distribution rule
		for(int i =0; i< 4;i++){ if(playerList[i].lowLevelHand()){ return true;} }
		return false;
	}

	/**/
	/*
	setUserContractNum()
	NAME
        setUserContractNum
	SYNOPSIS
		public void setUserContractNum(int num)
		num -> contract number of the user
	DESCRIPTION
		The function sets the contract number of the user and the flag indicating contract has been made
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        1:17pm 08/19/2019
	*/
	/**/
    public void setUserContractNum(int num){
    	player_me.setCurrentContractNum(num);
		player_me.setHasContracted(true);
		//storing the contract num in the scoreBoard
		scoreBoard[round][currentTurnNum] = num;
	}

	/**/
	/*
	setBotContractNum()
	NAME
        setBotContractNum
	SYNOPSIS
		public void setBotContractNum()
	DESCRIPTION
		sets the contract number for a bot player of given turn and also sets required flags
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        1:19pm 08/19/2019
	*/
	/**/
	public void setBotContractNum(){

		//get the contract num
		//int contractNum = currentTurnNum;
		int contractNum = getContractNum();
		//set the Contract num for bot of given turn
		playerList[currentTurnNum].setCurrentContractNum(contractNum );
		//also set the flag indicating contract has been made
		playerList[currentTurnNum].setHasContracted(true);
		//storing the contract num in the scoreBoard
		scoreBoard[round][currentTurnNum] = contractNum;
		//change the turn after setting the contract
		turnChange();
	}

	/**/
	/*
	setCountHandPlayed()
	NAME
        setCountHandPlayed
	SYNOPSIS
		public void setCountHandPlayed(int value)
		value -->denotes how many players have already played the given hand
	DESCRIPTION
		set the value of the variable countHandPlayed which the number
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        1:28pm 08/19/2019
	*/
	/**/
	public void setCountHandPlayed(int value){ countHandPlayed = value;}

	/**/
	/*
	setCurrentTurnNum()
	NAME
        setCurrentTurnNum
	SYNOPSIS
		public void setCurrentTurnNum(int value)
		value --> represents turn value
	DESCRIPTION
		set the turn
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        1:30pm 08/19/2019
	*/
	/**/
	public void setCurrentTurnNum(int value){currentTurnNum = value;}

	/**/
	/*
	getContractNum()
	NAME
        getContractNum
	SYNOPSIS
		public int getContractNum(int playerNum)
		playerNum --> represents the player
	DESCRIPTION
		The function will return the contract number the bot of given playerNum
	RETURNS
		Returns the contract number
	AUTHOR
		Rojan Shrestha
	DATE
        1:32pm 08/19/2019
	*/
	/**/
	public int getContractNum(int playerNum){ return playerList[playerNum].getCurrentContractNum();}

	/**/
	/*
	getScore()
	NAME
        getScore
	SYNOPSIS
		public int getScore(int playerNum)
		playerNum --> represents the player
	DESCRIPTION
		The function will returns the score of the player current round
	RETURNS
		Returns the score
	AUTHOR
		Rojan Shrestha
	DATE
        1:34pm 08/19/2019
	*/
	/**/
	public int getScore(int playerNum){ return playerList[playerNum].get_score();}

	/**/
	/*
	getCurrentTurnNum()
	NAME
        getCurrentTurnNum
	SYNOPSIS
		public int getCurrentTurnNum()
	DESCRIPTION
		The function will return the player num representing the current turn
	RETURNS
		Returns the current turn
	AUTHOR
		Rojan Shrestha
	DATE
        1:35pm 08/19/2019
	*/
	/**/
	public int getCurrentTurnNum(){ return this.currentTurnNum; }

	/**/
	/*
	getCountHandPlayed()
	NAME
        getCountHandPlayed
	SYNOPSIS
		public int getCountHandPlayed()
	DESCRIPTION
		The function will return the value of countHandPlayed
	RETURNS
		Returns the number representing how many players have already played in
		the current hand
	AUTHOR
		Rojan Shrestha
	DATE
        1:35pm 08/19/2019
	*/
	/**/
	public int getCountHandPlayed(){ return countHandPlayed;}

	/**/
	/*
	getRound()
	NAME
        getRound
	SYNOPSIS
		public int getRound()
	DESCRIPTION
		The function will return the round number
	RETURNS
		Returns the round number
	AUTHOR
		Rojan Shrestha
	DATE
        1:37pm 08/19/2019
	*/
	/**/
	public int getRound(){return round;}

	/**/
	/*
	getCurrentPlayedCards()
	NAME
        getCurrentPlayedCards
	SYNOPSIS
		public String[] getCurrentPlayedCards()
	DESCRIPTION
		The function will return the current played card array
	RETURNS
		Returns the array of all the cards played by the players
	AUTHOR
		Rojan Shrestha
	DATE
        1:38pm 08/19/2019
	*/
	/**/
	public String[] getCurrentPlayedCards(){ return currentPlayedCards;}

	/**/
	/*
	getUserHand()
	NAME
        getUserHand
	SYNOPSIS
		public Vector<String> getUserHand()
	DESCRIPTION
		The function will return the vector storing all the cards of the user
	RETURNS
		Returns the vector containing the cards stored as the hand of the user
	AUTHOR
		Rojan Shrestha
	DATE
        7:07pm 08/19/2019
	*/
	/**/
	public Vector<String> getUserHand(){
		Vector<Vector<String>> tempHand = player_me.getHand();
		Vector<String> merge = new Vector<>();
		//loop through each suit compartments of the hand and merge them together
		for(int i = 0 ; i<4;i++){ merge.addAll(tempHand.get(i)); }
		return merge;
	}

	/**/
	/*
	getWinnerHandPlayer()
	NAME
        getWinnerHandPlayer
	SYNOPSIS
		public String getWinnerHandPlayer()
	DESCRIPTION
		The function will return the player with the best card for given hand and set the turn to the player
	RETURNS
		Returns the player with the best card
	AUTHOR
		Rojan Shrestha
	DATE
        7:10pm 08/19/2019
	*/
	/**/
	public String getWinnerHandPlayer(){

		//update the turn to the winner
		currentTurnNum = bestHandIndex;

		//return the string value of the index
		switch(bestHandIndex){
			case 0: return "User";
			case 1: return "BOT1";
			case 2: return "BOT2";
			default: return "BOT3";

		}
	}

	/**/
	/*
	getWinnerHandCard()
	NAME
        getWinnerHandCard
	SYNOPSIS
		public String getWinnerHandCard()
	DESCRIPTION
		The function will return the best card for the given hand
	RETURNS
		Returns the best card for the given hand from the array of current played cards
	AUTHOR
		Rojan Shrestha
	DATE
        7:13pm 08/19/2019
	*/
	/**/
	public String getWinnerHandCard() { return currentPlayedCards[bestHandIndex];}

	/**/
	/*
	getScoreBoard()
	NAME
        getScoreBoard
	SYNOPSIS
		public double[][]  getScoreBoard()
	DESCRIPTION
		The function will return the scoreBoard which is an array of array
	RETURNS
		Returns 2D double array storing all the scores for all players for each round
	AUTHOR
		Rojan Shrestha
	DATE
        7:13pm 08/19/2019
	*/
	/**/
	public double[][] getScoreBoard(){ return scoreBoard;}

	/**/
	/*
	currentPlayerContracted()
	NAME
        currentPlayerContracted
	SYNOPSIS
		public boolean currentPlayerContracted()
	DESCRIPTION
		The function will check if the current player has contracted
	RETURNS
		Returns true if the isHasContracted attribute of the player is true
	AUTHOR
		Rojan Shrestha
	DATE
        7:16pm 08/19/2019
	*/
	/**/
	public boolean currentPlayerContracted(){return playerList[currentTurnNum].isHasContracted();}

	/**/
	/*
	turnChange()
	NAME
        turnChange
	SYNOPSIS
		public void turnChange()
	DESCRIPTION
		The function will change the turn, increase the turn by 1 but sets to 0 if ==3
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:19pm 08/19/2019
	*/
	/**/
	public void turnChange(){
		if(currentTurnNum == 3){ this.currentTurnNum = 0;}
		else this.currentTurnNum++;
	}

	/**/
	/*
	validUserHand()
	NAME
        validUserHand
	SYNOPSIS
		public boolean validUserHand(String card)
		card --> card selected by the user to be validated
	DESCRIPTION
		The function will validate the user selection, if the card selected follows the game protocol and
		update the required flags and variables if the user is the first player to play the hand
	RETURNS
		Returns true if the card selected is a valid selection
	AUTHOR
		Rojan Shrestha
	DATE
        7:21pm 08/19/2019
	*/
	/**/
	public boolean validUserHand(String card){
		if(countHandPlayed == 0) {

			//adding the played card into the list
			currentPlayedCards[currentTurnNum] = card;
			//store the bestHandIndex
			bestHandIndex = currentTurnNum;
			//storing the current suit being played for the given round
			currentHandSuit = card.charAt(0);
			//remove the card from the user's hand
            player_me.removeHandCard(card);

			return true;
		}

		//get the best card
		String bestCard = currentPlayedCards[bestHandIndex];
		//suit num for the current suit
		int suitNum = Player.getSuitNum(currentHandSuit);
		//get the list of cards of the current suit for given player
		Vector<String> bestSuit = player_me.getHand().get(suitNum);

		//Check if the current suit is equal to the bestCard suit
		//required to check if spade has already been played by other players
		if(currentHandSuit == bestCard.charAt(0)){

			//check if the current Suit vector for current player is empty
			//not empty means the card has to be picked of current suit
			if(!bestSuit.isEmpty()){

				//Check if the user played any other card then the current suit
				//Since he has cards of current suit he has to play it
				if(card.charAt(0) != currentHandSuit){ return false;}

				//the last element since it is the index of the lowest value card for any suit
				//in case the user does not have card to beat the best card, throw the lowest card
				Vector<String> listHigherCards = new Vector<>();

				//add the cards that has value higher than the best card
				for(int i=bestSuit.size()-1;i>=0;i--){
					String tempCurrentCard = bestSuit.get(i);
					//Check if the the given indexed card has greater value than the best card value
					if(Player.cardStringToValue(tempCurrentCard.charAt(1)) >
							Player.cardStringToValue(bestCard.charAt(1))){ listHigherCards.add(tempCurrentCard); }

				}

				//As of now
				// 1. No spade card has been used prior
				// 2. we have the list of cards better than the current best card
				// 3. user has selected the card of current suit

				//Check if the list is empty
				//then the user made valid selection
				if(listHigherCards.isEmpty()){
					//adding the played card into the list
					currentPlayedCards[currentTurnNum] = card;
					//removing the selected card from the current suit vector of the player
					player_me.removeHandCard(card);
					return true;
				}
				else{
					//not empty means there are cards better than the current best card
					//Check if user selected a card is in the high list
					if(listHigherCards.contains(card)){
						//adding the played card into the list
						currentPlayedCards[currentTurnNum] = card;
						//store the bestHandIndex
						bestHandIndex = currentTurnNum;
						//remove the card from the user's hand
						player_me.removeHandCard(card);
						return true;
					}
					//not in the list means invalid move by the player
					return false;

				}
			}
			else {
				//empty means user cannot play the current suit
				// and if he has spade cards then he has to play spade

				//Check if the user has spade cards
				//suit num for the current suit
				suitNum = Player.getSuitNum('S');
				//get the list of cards of the current suit for given player
				bestSuit = player_me.getHand().get(suitNum);

				//not empty means he has to play spade card
				if(!bestSuit.isEmpty()){
					//therefore if the card selected is not of suit Spade invalid move
					if(card.charAt(0) != 'S'){ return false;}

					//Since no spade card has been played before
					//Any spade card played by the user is the best card

					//adding the played card into the list
					currentPlayedCards[currentTurnNum] = card;
					//store the bestHandIndex
					bestHandIndex = currentTurnNum;
					//remove the card from the user's hand
					player_me.removeHandCard(card);
					return true;
				}
				else {
					//Since user has no current suit and neither the spade card
					//he has the freedom to play any card from the rem 2 suits

					//adding the played card into the list
					currentPlayedCards[currentTurnNum] = card;
					//remove the card from the user's hand
					player_me.removeHandCard(card);
					return true;
				}
			}
		}
		else{
			//Spade has already been played

			//Check if the user has cards of current suit
			if(!bestSuit.isEmpty()){
				//Check if the user played any other card then the current suit
				//Since he has cards of current suit he has to play it
				if(card.charAt(0) != currentHandSuit){ return false;}

				//any card he played that is of current suit is a valid move

				//adding the played card into the list
				currentPlayedCards[currentTurnNum] = card;
				//remove the card from the user's hand
				player_me.removeHandCard(card);
				return true;
			}
			else{
				//does not has the current suit

				//Check if the user has spade cards
				//suit num for the current suit
				suitNum = Player.getSuitNum('S');
				//get the list of cards of the current suit for given player
				bestSuit = player_me.getHand().get(suitNum);

				if(!bestSuit.isEmpty()){
					//has spade card so he has to play the spade card
					if(card.charAt(0) != 'S'){ return false;}

					//Check if the user played his best Spade card
					//the last element since it is the index of the lowest value card for any suit
					//in case the user does not have card to beat the best card, throw the lowest card
					Vector<String> listHigherCards = new Vector<>();

					//add the cards that has value higher than the best card
					for(int i=bestSuit.size()-1;i>=0;i--){
						String tempCurrentCard = bestSuit.get(i);
						//Check if the the given indexed card has greater value than the best card value
						if(Player.cardStringToValue(tempCurrentCard.charAt(1)) >
								Player.cardStringToValue(bestCard.charAt(1))){ listHigherCards.add(tempCurrentCard); }

					}

					//As of now
					// 1. Spade card has been used prior
					// 2. We have the list of spade cards better than the current best card(a spade card)
					// 3. user has selected the card of Spade

					//Check if the list is empty
					//then the user made valid selection
					if(listHigherCards.isEmpty()){
						//adding the played card into the list
						currentPlayedCards[currentTurnNum] = card;
						//removing the selected card from the current suit vector of the player
						player_me.removeHandCard(card);
						return true;
					}
					else{
						//not empty means there are cards better than the current best card
						//Check if user selected a card is in the high list
						if(listHigherCards.contains(card)){
							//adding the played card into the list
							currentPlayedCards[currentTurnNum] = card;
							//store the bestHandIndex
							bestHandIndex = currentTurnNum;
							//remove the card from the user's hand
							player_me.removeHandCard(card);
							return true;
						}
						//not in the list means invalid move by the player
						return false;
					}
				}
				else {
					//no current suit and no spade card so the user has the freedom to choose any card

					//adding the played card into the list
					currentPlayedCards[currentTurnNum] = card;
					//removing the selected card from the current suit vector of the player
					player_me.removeHandCard(card);
					return true;
				}
			}
		}

	}

	/**/
	/*
	playBotCard()
	NAME
        playBotCard
	SYNOPSIS
		public String playBotCard()
	DESCRIPTION
		The function will get the card to be played by the current bot player. The card is selected after
		validation
	RETURNS
		Returns the card selected for the current bot
	AUTHOR
		Rojan Shrestha
	DATE
        7:23pm 08/19/2019
	*/
	/**/
	public String playBotCard() {

		if(countHandPlayed == 0){
			//the card played by the player
			String handCard;
			// Generate random integers in range 0 to 3
			Random rand = new Random();
			int suitIndex = rand.nextInt(4);

			while (true) {
				//exception handling for temp trail period
				try {
					//randomly get the value card
					rand = new Random();
					int cardIndex = rand.nextInt(playerList[currentTurnNum].getHand().get(suitIndex).size());

					//check if the player has Ace
					Vector<String> selectedSuitList = playerList[currentTurnNum].getHand().get(suitIndex);
					for(int i=0; i<selectedSuitList.size();i++){
						if(selectedSuitList.get(i).charAt(1) == 'A'){
							cardIndex = i;
							break;
						}
					}
					//get the hand card
					handCard = playerList[currentTurnNum].get_handCard(suitIndex , cardIndex);
					break;
				}
				catch (Exception e) {
					rand = new Random();
					suitIndex  = rand.nextInt(4);
				}
			}
			//adding the played card into the list in index =  currentTurn
			currentPlayedCards[currentTurnNum] = handCard;
			//storing the current suit being played for the given round
			currentHandSuit = handCard.charAt(0);
			//store the bestHandIndex
			bestHandIndex = currentTurnNum;
			return currentPlayedCards[currentTurnNum];
		}
		else{
			//get the best card
			String bestCard = currentPlayedCards[bestHandIndex];
			//suit num for the current suit
			int suitNum = Player.getSuitNum(currentHandSuit);
			//get the list of cards of the current suit for given player
			Vector<String> bestSuit = playerList[currentTurnNum].getHand().get(suitNum);

			//Check if the current suit is equal to the bestCard suit
			if(currentHandSuit == bestCard.charAt(0)){

				//check if the current Suit vector for current player is empty
				//not empty means the card has to be picked of current suit
				if(!bestSuit.isEmpty()){
					//get the least highest best card
					for(int i=bestSuit.size()-1;i>=0;i--){
						String tempCurrentCard = bestSuit.get(i);

						//Check if the the given indexed card has greater value than the best card value
						if(Player.cardStringToValue(tempCurrentCard.charAt(1)) >
								Player.cardStringToValue(bestCard.charAt(1))){
							//return the new bestCard after adding the best card and updating bestCard index
							//also removes the card from the vector
							currentPlayedCards[currentTurnNum] = playerList[currentTurnNum].get_handCard(suitNum,i);
							bestHandIndex = currentTurnNum;
							return currentPlayedCards[currentTurnNum];
						}
					}

					//if greater value not found then return the smallest value card of current suit
					currentPlayedCards[currentTurnNum] = playerList[currentTurnNum].get_handCard(suitNum,bestSuit.size()-1);
					return currentPlayedCards[currentTurnNum] ;
				}
				else {
					//when empty, no card of current suit then pick the spade
					suitNum = Player.getSuitNum('S');
					bestSuit = playerList[currentTurnNum].getHand().get(suitNum);

					//check if the Spade suit vector for current player is empty
					//not empty means the card has to be picked of suit Spade
					if(!bestSuit.isEmpty()){
						//return the new bestCard after adding the best card and updating bestCard index
						//also removes the card from the vector
						currentPlayedCards[currentTurnNum] = playerList[currentTurnNum].get_handCard(suitNum,bestSuit.size()-1);
						bestHandIndex = currentTurnNum;
						return currentPlayedCards[currentTurnNum];
					}
				}
			}
			else{
				//not equal meaning player before has already used spade
				//check if the current Suit vector for current player is empty
				//not empty means the card has to be picked has of current suit
				if(!bestSuit.isEmpty()){
					//get the least highest card
					currentPlayedCards[currentTurnNum] = playerList[currentTurnNum].get_handCard(suitNum,bestSuit.size()-1);
					return currentPlayedCards[currentTurnNum];
				}
				else {
					//empty so use spade of value higher than the current best hand
					suitNum = Player.getSuitNum('S');
					bestSuit = playerList[currentTurnNum].getHand().get(suitNum);

					//check if the Spade suit vector for current player is empty
					//not empty means the card has to be picked of suit Spade
					if(!bestSuit.isEmpty()){
						//there are spade cards so get the value greater the current best hand
						//get the least highest best card
						for(int i = bestSuit.size()-1;i>=0;i--){
							String tempCurrentCard = bestSuit.get(i);

							//Check if the the given indexed card has greater value than the best card value
							if(Player.cardStringToValue(tempCurrentCard.charAt(1)) >
									Player.cardStringToValue(bestCard.charAt(1))){
								//return the new bestCard after adding the best card and updating bestCard index
								//also removes the card from the vector
								currentPlayedCards[currentTurnNum] = playerList[currentTurnNum].get_handCard(suitNum,i);
								bestHandIndex = currentTurnNum;
								return currentPlayedCards[currentTurnNum];
							}
						}

						//if greater value not found then return the smallest value card of current suit
						currentPlayedCards[currentTurnNum] = playerList[currentTurnNum].get_handCard(suitNum,bestSuit.size()-1);
						return currentPlayedCards[currentTurnNum] ;
					}
				}
			}

			//if still not card found that means the player has no current suit or spade
			//if no spade card then select any of the remaining 2 suits
			//find the suit vector of the highest size
			Vector<Character> suitList = new Vector<>();
			//Adding all three non-spade suits in vector
			suitList.add('H');
			suitList.add('C');
			suitList.add('D');

			//loop to find the suit with the highest number of cards
			int highestSize = 0;
			for(int i=0;i<3;i++){
				int tempSuitNum = Player.getSuitNum(suitList.get(i));
				int currentSuitSize = playerList[currentTurnNum].getHand().get(tempSuitNum).size();
				if(currentSuitSize > highestSize){
					//update the highestSize and suitNum
					highestSize = currentSuitSize;
					suitNum = tempSuitNum;
				}
			}

			//return the card from the suit with highest size
			currentPlayedCards[currentTurnNum] = playerList[currentTurnNum].get_handCard(suitNum,highestSize-1);
			return currentPlayedCards[currentTurnNum];
		}
	}

	/**/
	/*
	printPlayerHand()
	NAME
        printPlayerHand
	SYNOPSIS
		public void printPlayerHand()
	DESCRIPTION
		The function will print all the player's hand cards
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:25pm 08/19/2019
	*/
	/**/
	public void printPlayerHand(){ for(int j =0; j<4;j++){ playerList[j].printHand();} }

	/**/
	/*
	updateScore()
	NAME
        updateScore
	SYNOPSIS
		public void updateScore()
	DESCRIPTION
		The function will update the score of the player that won the last hand, increase by 1
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:26pm 08/19/2019
	*/
	/**/
	public void updateScore(){ playerList[bestHandIndex].set_score(playerList[bestHandIndex].get_score()+1); }

	/**/
	/*
	endOfRound()
	NAME
        endOfRound
	SYNOPSIS
		public boolean endOfRound()
	DESCRIPTION
		The function will update the variables and plays new round and check for the end of the game.
	RETURNS
		Returns false if it is the end of the game
	AUTHOR
		Rojan Shrestha
	DATE
        7:27pm 08/19/2019
	*/
	/**/
	public boolean endOfRound(){

        //calculates the score
        calculateEndScore();

		//Check if it is the end of game
        if(round + 1 == MAX_ROUND){return false;}

        //set the current turn to the winner of the last round
        currentTurnNum = bestHandIndex;

		//loop to keep dealing the cards until distribution follows spade or high card rule
		do{ newDealCards(); } while (distributeAgain());

		//set the hand played num to 0 at the start of the game
		countHandPlayed = 0;

		//set initial round to 0 for easier manipulation of containers
		round ++;

		return true;
    }

	/**/
	/*
	calculateEndScore()
	NAME
        calculateEndScore
	SYNOPSIS
		public void calculateEndScore()
	DESCRIPTION
		The function will calculate and update the score at the end of the round,
		also get the contract num and the score of the player at the end of round and
		check if the score is less than contract num, endScore = - [contract num] else, endScore = contract num + (contract num - score) / 10
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:27pm 08/19/2019
	*/
	/**/
	public void calculateEndScore(){

	    //update the score of each player
	    for(int i = 0; i<4;i++){
	        double score  = playerList[i].get_score();
	        double contractNum = playerList[i].getCurrentContractNum();
	        if(score < contractNum){ scoreBoard[round][i] = 0 - contractNum; }
	        else{ scoreBoard[round][i] = contractNum + (score - contractNum)/10; }
        }
	}

	/**/
	/*
	saveGame()
	NAME
        saveGame
	SYNOPSIS
		public boolean saveGame(String fileName)
		fileName --> the name of the file to be saved
	DESCRIPTION
		The function will save the game, essentially the flags and variables required by the game
	RETURNS
		Returns true after successful game saving
	AUTHOR
		Rojan Shrestha
	DATE
        7:29pm 08/19/2019
	*/
	/**/
	public boolean saveGame(String fileName){

		//if the user input empty filename
		if(fileName.isEmpty()) return false;

		File file = new File(Environment.getExternalStorageDirectory()+ "/callBreak" +"/"+fileName+".txt");
		FileOutputStream fos;

		try {
			System.out.println("-->"+file);
			//attach the file to fileOutputStream
			fos = new FileOutputStream(file);

			//check whether the file exists, not then create
			if (!file.exists()) { file.createNewFile(); }

			//variable to store a line of info
			String contentLine = "";

			//saving the required generic variable/flags in the file
			//currentHandSuit,countHandPlayed,round and bestHandIndex
			contentLine += "round: "+ round + "\n\n" +
					"countHandPlayed: "+countHandPlayed+"\n\n" +
					"currentHandSuit: "+currentHandSuit+"\n\n" +
					"bestHandIndex: "+bestHandIndex+"\n\n"+
					"scoreBoard: ";

			//adding the scores form all the completed rounds
			for(int i=0;i<round;i++){
				for(int j=0;j<4;j++){
					contentLine+=scoreBoard[i][j]+" ";
				}
			}
			contentLine+="\n\n";

			for(int i =0;i<4;i++){
				contentLine+= "Player_"+i+":\n"+
						" score: "+playerList[i].get_score()+"\n" +
						" contractNum: "+playerList[i].getCurrentContractNum()+"\n"+
						" currentPlayedCards: "+currentPlayedCards[i]+"\n" +
						" hand: ";

				//get each card from each suit vector
				//and add to the contentLine
				for (Vector<String> vectorSuit : playerList[i].getHand()) {
					Vector<String> temp = new Vector<String>(vectorSuit);
					for (String card : temp) { contentLine += card+" ";}
				}

				contentLine+="\n\n";
			}

			fos.write(contentLine.getBytes());
			fos.flush();
			fos.close();

			return true;

		} catch (IOException e) {

			// handle exception
			System.out.println("ERROR");
			return false;

		}
	}

	/**/
	/*
	saveGame()
	NAME
        saveGame
	SYNOPSIS
		public boolean saveGame(String fileName)
		fileName --> the name of the file to be load
	DESCRIPTION
		The function will load the game, essentially the flags and variables required by the game
	RETURNS
		Returns true after successful game loading
	AUTHORs
		Rojan Shrestha
	DATE
        7:32pm 08/19/2019
	*/
	/**/
	public boolean loadGame(String fileName) {

		//Careful with the currentHand Suit
		//Check if the countHandPlayed is not 0

		//Empty string
		String label = "";
		String data = "";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = reader.readLine()) != null) {
				//Scanner object to use delimiter
				Scanner scan = new Scanner(line);
				// initialize the string delimiter
				scan.useDelimiter(": ");

				if (scan.hasNext()) label = scan.next();
				if (scan.hasNext()) data = scan.next();

				// closing the scanner stream
				scan.close();

				//Check the label
				if (label.equals("round")) {
					//Round

					//set the round
					round = Integer.parseInt(data);

				}
				else if (label.equals("countHandPlayed")) {
					//countHandPlayed

					//set the countHandPlayed
					countHandPlayed = Integer.parseInt(data);

				}
				else if (label.equals("currentHandSuit")) {
					//currentHandSuit

					//set the currentHandSuit
					currentHandSuit = data.charAt(0);
				}
				else if (label.equals("bestHandIndex")) {
					//bestHandIndex

					//set the countHandPlayed
					bestHandIndex = Integer.parseInt(data);
				}
				else if (label.equals("scoreBoard")) {
					//scoreBoard
					scan = new Scanner(data);
					// initialize the string delimiter
					scan.useDelimiter(" ");
					for (int j = 0; j < round; j++) {
						for (int i = 0; i < 4; i++) {
							scoreBoard[j][i] = Double.parseDouble(scan.next());
						}
					}
					// closing the scanner stream
					scan.close();

				}
				else if (label.contains("Player_")) {
					//player info
					int playerNum = Character.getNumericValue(label.charAt(7));
					//loop for 4 times to access hand, score, contractNum, currentPlayerCards
					for(int i =0;i<4;i++){
						//read another line
						line = reader.readLine();
						//Scanner object to use delimiter
						scan = new Scanner(line);
						// initialize the string delimiter
						scan.useDelimiter(": ");
						if (scan.hasNext()) label = scan.next();
						if (scan.hasNext()) data = scan.next();
						// closing the scanner stream
						scan.close();

						if(label.equals(" score")){
							//score of current player
							playerList[playerNum].set_score( Integer.parseInt(data));

						}
						else if(label.equals(" contractNum")){
							//currentPlayedCards of the current player
							playerList[playerNum].setCurrentContractNum(Integer.parseInt(data));

						}
						else if(label.equals(" currentPlayedCards")){
							//currentPlayedCards of the current player
							currentPlayedCards[playerNum] = data;

						}
						else{
							//hand
							scan = new Scanner(data);
							// initialize the string delimiter
							scan.useDelimiter(" ");

							//reset the hand and empty the vectors
							playerList[playerNum].resetHand();

							//store the card to the hand of the player
							while(scan.hasNext()){
								String card = scan.next();
								playerList[playerNum].storeHand(card);
							}
							// closing the scanner stream
							scan.close();

						}

						//Empty string
						label = "";
						data = "";
					}
				}

				//resetting the values to avoid repeating data
				data = "";
				label = "";
			}

			//set the currentTurnNum = 0 since the user can only save
			//when it is his turn
			currentTurnNum = 0;
			reader.close();
			return true;

		} catch (Exception ex) {

			//Unable to open file
			//Error reading file
			//Displaying the turn of the player
			System.out.println("Error");
			return false;

		}



	}

	/**/
	/*
	getContractNum()
	NAME
        getContractNum
	SYNOPSIS
		public int getContractNum()
	DESCRIPTION
		The function will calculate the contract num for the bot
	RETURNS
		Returns true after successful game loading
	AUTHORs
		Rojan Shrestha
	DATE
        7:36pm 08/19/2019
	*/
	/**/
	public int getContractNum(){
		Vector<Vector<String>> hand =  playerList[currentTurnNum].getHand();
		int contractNum = 0;
		int spadeNum = hand.get(0).size();
		for(int i=0;i<hand.size();i++){
			Vector<String> suit = hand.get(i);
			for(int j=0;j<suit.size();j++){
				switch (suit.get(j).charAt(1)){
					case 'A':
					case 'K':
						contractNum++;
						break;
				}
			}
		}
		if(spadeNum >5){
			contractNum ++;
		}
		//Increase the num per the Random agression
		Random rand = new Random();
		int randAgression = rand.nextInt(3);
		if(randAgression == 0){
			contractNum++;
		}
		else if(randAgression == 1){
			contractNum--;
		}
		if(contractNum < 1){contractNum = 1;}
		return contractNum;
	}
}
