package sample.Model;

public class Term {

    private String value;
    private int amount;

    public Term(String value) {
        this.value = value;
        amount = 1;
    }

    public void increaseAmount() {
        amount++;
    }

    public String getValue() {
        return value;
    }

    public int getAmount () {return amount;}

    public void setAmount (int newAmount) {amount = newAmount;}
}