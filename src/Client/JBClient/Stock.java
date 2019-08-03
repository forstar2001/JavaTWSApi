/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.JBClient;

import com.ib.client.Contract;

/**
 *
 * @author SeniorDeveloper
 */
public class Stock{
private int StockId; //we can identify the stock
private String Symbol; //Ticker

    public Stock() { //default constructor
    }

    public Stock(int StockId, String Symbol) { //constructor
        this.StockId = StockId;
        this.Symbol = Symbol;
    }
    //getter and setters
    public int getStockId() {
        return StockId;
    }

    public String getSymbol() {
        return Symbol;
    }

Contract contract = new Contract ();
public void createContract(String cusip){
    contract.m_secId = cusip;
    contract.m_secIdType = "CUSIP";
    contract.m_exchange = "SMART";
    contract.m_secType = "STK";
    contract.m_currency = "USD";

}
}
