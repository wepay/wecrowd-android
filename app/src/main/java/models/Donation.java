package models;

/**
 * Created by zachv on 7/27/15.
 * Wecrowd Android
 */
public class Donation {
    private Integer campaignID;
    private String creditCardID;
    private Integer amount;
    private String checkoutID;

    public Donation() {}

    public void setCampaignID(Integer campaignID) { this.campaignID = campaignID; }
    public void setCreditCardID(String creditCardID) { this.creditCardID = creditCardID; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public void setCheckoutID(String checkoutID) { this.checkoutID = checkoutID; }

    public Integer getCampaignID() { return campaignID; }
    public String getCreditCardID() { return creditCardID; }
    public Integer getAmount() { return amount; }
    public String getCheckoutID() { return checkoutID; }
}
