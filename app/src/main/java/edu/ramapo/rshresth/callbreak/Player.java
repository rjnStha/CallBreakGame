package edu.ramapo.rshresth.callbreak;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class Player {

	//vector to store the cards in the player's hand
	//vector of vector to compartmentalize the suits while storing the hand cards
	private Vector<Vector<String>> hand;

	//stores the number of hand won for given round
	private int score;
	//stores the number of tricks the player expects to win for given round
	private int currentContractNum;
	//flag to check if contract num has been assigned
	private boolean hasContracted;

	//to differentiate between bot and human
	private boolean valueIsBot;
	//name of the player
	private String name;

	/**/
	/*
	Player()
	NAME
        Player
	SYNOPSIS
		public Player(boolean valueIsBot)
		valueIsBot -> false if the player is the user
	DESCRIPTION
		Constructor
		The function will reset Hand and initialize the member variables
	RETURNS

	AUTHOR
		Rojan Shrestha
	DATE
        7:37pm 07/29/2019
	*/
	/**/
	public Player(boolean valueIsBot){

		//new instance of hand and 4 suit compartments
		resetHand();

		this.valueIsBot= valueIsBot;
		//set the default contract num
		currentContractNum = 1;
		hasContracted = false;
		score = 0;
	}
		
	//Getters

	/**/
	/*
	get_score()
	NAME
        get_score
	SYNOPSIS
		public int get_score()
	DESCRIPTION
		The function will return the score of the player
	RETURNS
		Returns player's score of given round
	AUTHOR
		Rojan Shrestha
	DATE
        7:40pm 07/29/2019
	*/
	/**/
	public int get_score() { return this.score;}

	/**/
	/*
	isBot()
	NAME
        isBot
	SYNOPSIS
		public boolean isBot()
	DESCRIPTION
		The function will return if the player is bot or not
	RETURNS
		Returns true if the player is a bot
	AUTHOR
		Rojan Shrestha
	DATE
        7:41pm 07/29/2019
	*/
	/**/
	public boolean isBot(){return this.valueIsBot;}

	/**/
	/*
	isHasContracted()
	NAME
        isHasContracted
	SYNOPSIS
		public boolean isHasContracted()
	DESCRIPTION
		The function will return if the player has contracted
	RETURNS
		Returns true if the player has contracted for the given round
	AUTHOR
		Rojan Shrestha
	DATE
        7:42pm 07/29/2019
	*/
	/**/
	public boolean isHasContracted(){return this.hasContracted;}

	/**/
	/*
	getHand()
	NAME
        getHand
	SYNOPSIS
		public Vector<Vector<String>> getHand()
	DESCRIPTION
		The function will return the hand of the player
	RETURNS
		Returns with the vector of vector string containing the cards as the hand of the player
	AUTHOR
		Rojan Shrestha
	DATE
        7:43pm 07/29/2019
	*/
	/**/
	public Vector<Vector<String>> getHand(){return this.hand;}

	/**/
	/*
	getCurrentContractNum()
	NAME
        getCurrentContractNum
	SYNOPSIS
		public int getCurrentContractNum()
	DESCRIPTION
		The function will return the current contract num of the player
	RETURNS
		Returns the contract num of the player
	AUTHOR
		Rojan Shrestha
	DATE
        7:44pm 07/29/2019
	*/
	/**/
	public int getCurrentContractNum(){return this.currentContractNum;}
	
	//Setters

	/**/
	/*
	setHasContracted()
	NAME
        setHasContracted
	SYNOPSIS
		public void setHasContracted(boolean hasContracted)
		hasContracted --> true or false indicating if the player has contracted
	DESCRIPTION
		The function will sets whether the player has contracted for the given round
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:46pm 07/29/2019
	*/
	/**/
	public void setHasContracted(boolean hasContracted){this.hasContracted = hasContracted;}

	/**/
	/*
	set_score()
	NAME
        set_score
	SYNOPSIS
		public void set_score(int score)
		score --> value of the player's score to be set
	DESCRIPTION
		The function will set the score of the player
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:47pm 07/29/2019
	*/
	/**/
	public void set_score(int score) {this.score = score;}

	/**/
	/*
	storeHand()
	NAME
        storeHand
	SYNOPSIS
		public void storeHand(String card)
		card --> card to be stored
	DESCRIPTION
		The function will store the given card in player hand based on the suit
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:48pm 07/29/2019
	*/
	/**/
	public void storeHand(String card) {
		switch(card.charAt(0)){
			case 'S': hand.get(0).add(card);
				break;
			case 'H':  hand.get(1).add(card);
				break;
			case 'C':  hand.get(2).add(card);
				break;
			case 'D':  hand.get(3).add(card);
				break;
		}
	}

	/**/
	/*
	setCurrentContractNum()
	NAME
        setCurrentContractNum
	SYNOPSIS
		public void setCurrentContractNum(int num)
		num --> value of the contract to be set
	DESCRIPTION
		The function will set the current Contract num
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:50pm 07/29/2019
	*/
	/**/
	public void setCurrentContractNum(int num){this.currentContractNum = num;}

	/**/
	/*
	get_handCard()
	NAME
        get_handCard
	SYNOPSIS
		public String get_handCard(int suitIndex, int cardIndex)
		suitIndex --> index value representing the suit
		cardIndex --> index value representing the card to be removed
	DESCRIPTION
		The function will remove and return the hand card of given index
	RETURNS
		Returns the hand card of given index
	AUTHOR
		Rojan Shrestha
	DATE
        7:52pm 07/29/2019
	*/
	/**/
    public String get_handCard(int suitIndex, int cardIndex) { return hand.get(suitIndex).remove(cardIndex);	}

	/**/
	/*
	removeHandCard()
	NAME
        removeHandCard
	SYNOPSIS
		public void removeHandCard(String card)
		card --> the card to be removed from the hand
	DESCRIPTION
		The function will remove the given card from the hand
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:53pm 07/29/2019
	*/
	/**/
    public void removeHandCard(String card){ hand.get(getSuitNum(card.charAt(0))).remove(card); }

	/**/
	/*
	printHand()
	NAME
        printHand
	SYNOPSIS
		public void printHand()
	DESCRIPTION
		The function will print all the cards in the player's hand
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:53pm 07/29/2019
	*/
	/**/
	public void printHand(){
		for (int i =0; i<4;i++)
		{
			Vector<String> suitHand = hand.get(i);
			for (int j = 0; j<suitHand.size();j++){
				System.out.print(suitHand.get(j) + " ");
			}
		}

		System.out.println();
	}

	/**/
	/*
	sortHand()
	NAME
        sortHand
	SYNOPSIS
		public void sortHand()
	DESCRIPTION
		The function will sort the hand card based on value(ascending) and suit
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:54pm 07/29/2019
	*/
	/**/
	public void sortHand(){
		//loop through each compartment of the hand and sort each of them
		for(int i = 0; i<4;i++){
			//sorts the hand based on the value in descending order
			Collections.sort(hand.get(i), new Comparator<String>()
			{
				@Override
				public int compare(String o1, String o2) { return cardStringToValue(o2.charAt(1)) - cardStringToValue(o1.charAt(1)); }
			});
		}
	}

	/**/
	/*
	cardStringToValue()
	NAME
        cardStringToValue
	SYNOPSIS
		public static int cardStringToValue(char key)
		key --> X, A, J, Q or numeric(2-10)
	DESCRIPTION
		The function will convert card value in character to numeric value
		made static for it's use in multiple classes
	RETURNS
		Returns the numeric value of the given card's key
	AUTHOR
		Rojan Shrestha
	DATE
        7:56pm 07/29/2019
	*/
	/**/
    public static int cardStringToValue(char key){
        switch(key)
        {
            case 'A': return 14;
            case 'K': return 13;
            case 'Q': return 12;
            case 'J': return 11;
            case 'X': return 10;
            default: return key-'0';
        }
    }

	/**/
	/*
	lowLevelHand()
	NAME
        lowLevelHand
	SYNOPSIS
		public boolean lowLevelHand()
	DESCRIPTION
		The function will checks if the player hand has at least one spade card or
		a face card
	RETURNS
		Returns false if the hand fails the protocol
	AUTHOR
		Rojan Shrestha
	DATE
        7:58pm 07/29/2019
	*/
	/**/
	public boolean lowLevelHand() {

    	//get the size of the spade compartment of the hand
    	if(hand.get(0).size() == 0) {return true;}

		//loop to count the number of cards greater than 10
		for (Vector<String> x:hand)
		{
			Vector<String> suitHand = x;
			for (String card: suitHand){
				//check for high cards and if found return false
				switch (card.charAt(1)){
					case 'A':
					case 'K':
					case 'Q':
					case 'J':
						return false;
				}
			}
		}

    	return true;
	}

	/**/
	/*
	resetHand()
	NAME
        resetHand
	SYNOPSIS
		public void resetHand()
	DESCRIPTION
		The function will create new vector of hand
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        7:58pm 07/29/2019
	*/
	/**/
	public void resetHand() {
    	hand = new Vector<>();
		//initialize each 4 vectors and store in the hand
		for(int i =0;i<4;i++){
			Vector<String> temp = new Vector<>();
			hand.add(temp);
		} }

	/**/
	/*
	resetHand()
	NAME
        resetHand
	SYNOPSIS
		public static int getSuitNum(char suit)
	DESCRIPTION
		The function will return the number assigned for a suit, S-0 H-1 C-2 D-3
	RETURNS
		returns the suit number
	AUTHOR
		Rojan Shrestha
	DATE
        7:59pm 07/29/2019
	*/
	/**/
	public static int getSuitNum(char suit){
		switch(suit)
		{
			case 'S': return 0;
			case 'H': return 1;
			case 'C': return 2;
			default: return 3;
		}
	}
}
