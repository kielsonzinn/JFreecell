package com.sri.jfreecell;

/**
 * @author Sateesh Gampala
 *
 */

public class CardPileBulk extends CardPile {

    private static final long serialVersionUID = -2102907183340211155L;
    private CardPile pile;
    private int count;

    public CardPileBulk(CardPile pile, int count) {
        this.pile = pile;
        this.count = count;
    }

    public CardPile getCardPile() {
        return pile;
    }

    public void setPile(CardPile pile) {
        this.pile = pile;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
