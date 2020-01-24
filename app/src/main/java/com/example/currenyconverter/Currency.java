package com.example.currenyconverter;

/**
 * Currency view class
 */
public class Currency {
    private int countryFlag;
    private String CurrencyName;
    private String currencyValue;
    private boolean currencyEditable;

    public Currency(int countryFlag, String CurrencyName, String currencyValue, boolean currencyEditable){
        this.countryFlag = countryFlag;
        this.CurrencyName = CurrencyName;
        this.currencyValue = currencyValue;
        this.currencyEditable = currencyEditable;
    }

    public int getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(int countryFlag) {
        this.countryFlag = countryFlag;
    }

    public String getCurrencyName() {
        return CurrencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.CurrencyName = currencyName;
    }

    public String getCurrencyValue() {
        return currencyValue;
    }

    public void setCurrencyValue(String currencyValue) {
        this.currencyValue = currencyValue;
    }

    public boolean isCurrencyEditable() {
        return currencyEditable;
    }

    public void setCurrencyEditable(boolean currencyEditable) {
        this.currencyEditable = currencyEditable;
    }
}