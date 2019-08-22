package edu.ramapo.rshresth.callbreak;

import java.util.Vector;
import java.util.Random;

public class Deck 
{
	//Vector of strings to store all the cards in Deck
	private Vector<String> cardDeck;

	/**/
	/*
	Deck()
	NAME
        Deck
	SYNOPSIS
		public Deck(boolean isNewRound)
		isNewRound -> if false then returns with empty Deck required when loading game
	DESCRIPTION
		Constructor
		The function will generates 52 cards and shuffles
	RETURNS

	AUTHOR
		Rojan Shrestha
	DATE
        12:50pm 07/29/2019
	*/
	/**/
	public Deck(boolean isNewRound) 
	{
		//new instance of deck in constructor
    	cardDeck = new Vector<String>();

    	//check if it is new round else return without creating new cards
		//required when loading a game
    	if(!isNewRound) return;

    	//assign values and suit to each of the 52 cards
    	for(int i = 0;i < 52;i++)
    	{
    		String temp;
    		
    		//first character as suit
    		if(i<13) temp = "S";
    		else if(i<26) temp = "H";
    		else if(i<39) temp = "C";
    		else temp = "D";
    		
    		//second character as value
    		int tempValue = (i % 13)+1;
    		if(tempValue == 1) temp += "A";
    		else if(tempValue < 10) temp += String.valueOf(tempValue);
    		else if(tempValue == 10) temp += 'X';
    		else if(tempValue == 11) temp += 'J';
    		else if(tempValue == 12) temp += 'Q';
    		else temp += 'K';

    		//push the temp into the deck
    		cardDeck.add(temp);
    	}
    	 
    	shuffle();                        
    }

	/**/
	/*
	printDeckCards()
	NAME
        printDeckCards
	SYNOPSIS
		public void printDeckCards()
	DESCRIPTION
		The function will print all the cards in the deck on the console
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        1:03pm 07/29/2019
	*/
	/**/
	public void printDeckCards()
	{
		for (String x:cardDeck)
		{
			System.out.print(x);
			System.out.print(" ");
		}
		System.out.println();
	}

	/**/
	/*
	shuffle()
	NAME
		shuffle
	SYNOPSIS
		public void shuffle()
	DESCRIPTION
		The function will randomly shuffle the cards in the deck
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        1:03pm 07/29/2019
	*/
	/**/
	public void shuffle() 
	{
        //creates instance of Random class 
		Random rand = new Random();
        
		//Start from the last element and swap one by one
	    //We don't need to run for the first element that's why i > 0 
		for (int i = cardDeck.size()-1; i > 0; i--) 
	    { 
	        // Generate a random integers in range 0 to i 
	        int j = rand.nextInt(i+1); 
	        
	        // Swap i with the element at random index j 
	        String temp = cardDeck.get(i);
	        cardDeck.set(i,cardDeck.get(j));
	        cardDeck.set(j,temp);
	    }
	}

	/**/
	/*
	getNewCard()
	NAME
		shuffle
	SYNOPSIS
		public string getNewCard()
	DESCRIPTION
		The function will get new card of index 0 from the deck and also
		removes the card from the deck
	RETURNS
		Returns the card as string object
	AUTHOR
		Rojan Shrestha
	DATE
        1:04pm 07/29/2019
	*/
	/**/
	public String getNewCard() 
	{
		//store the first card of deck in temp
		String temp = cardDeck.get(0);
		//Erased the first card and return
		cardDeck.remove(0);
		return temp;
	}

	/**/
	/*
	storeHand()
	NAME
		storeHand
	SYNOPSIS
		public void storeHand(String card)
		card --> the string object withe card to be stored info
	DESCRIPTION
		The function will store the given card in the deck
		used by the load game function
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        1:05pm 07/29/2019
	*/
	/**/
    public void storeHand(String card) { cardDeck.add(card);}

	/**/
	/*
	getDeck()
	NAME
		getDeck
	SYNOPSIS
		public Vector<String> getDeck()
	DESCRIPTION
		The function will return cardDeck, a Vector<string> which is the deck
		containing the cards stored as string object
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        1:11pm 07/29/2019
	*/
	/**/
	public Vector<String> getDeck() { return cardDeck; }
		
}
