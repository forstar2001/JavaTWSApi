/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.JBClient;

/**
 *
 * @author SeniorDeveloper
 */
import com.ib.client.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

//doesn't extend thread and if you implement EWrapper you have to implement all methods
//in API 9.72 you can extend DefaultWrapper and just override the methods you need
public class OrderManagement implements EWrapper{

    private EClientSocket client = null; //IB API client Socket Object
    private EJavaSignal m_signal = new EJavaSignal();
    private int orderId = -1;//use as flag to send orders
    //private double limitprice;
    //private String Ticker;

    //keep track of all working orders
    private Map<Integer, Order> workingOrders = new HashMap<>();

    //method to create connection class. It's the constructor
    public OrderManagement(){
        // Create a new EClientSocket object
        client = new EClientSocket(this, m_signal); //Creation of a socket to connect
        //connect to the TWS Demo
        client.eConnect(null, 7496, 123);//starts reader thread
        try {
            Thread.sleep(10000); //waits 3 seconds for user to accept
            while (!(client.isConnected()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("-----Connected-----");
    }
    
    public void closeBasket(String closePercent){
        for(int i=0; i<workingOrders.size(); i++){
//            workingOrders.get(i).cl
        }
    }

    public void sendMarketOrder(String cusip, String buyorSell, int shares, int target, int stop) {
        //make new stock and order for each stock
        Stock stock = new Stock();
        Order parent = new Order();
        //New Order ID, but get from API as you have to increment on every run for life
        orderId = orderId+3;
        parent.m_action = buyorSell;
        parent.m_orderId = orderId;
        parent.m_orderType = "MKT";
        parent.m_totalQuantity = shares;
        //I don't think you're supposed to use these fields
        //order.m_account = "DU33xxxxx"; //write own account
        //order.m_clientId = 1;
        Order takeProfit = new Order();
	takeProfit.orderId(parent.orderId() + 1);
	takeProfit.action(buyorSell);
	takeProfit.orderType("LMT");
	takeProfit.totalQuantity(shares);
	takeProfit.lmtPrice(target*5);
	takeProfit.parentId(orderId);
	takeProfit.transmit(false);
		
	Order stopLoss = new Order();
	stopLoss.orderId(parent.orderId() + 2);
	stopLoss.action(buyorSell);
	stopLoss.orderType("STP");
	//Stop trigger price
	stopLoss.auxPrice(stop*5);
	stopLoss.totalQuantity(shares);
	stopLoss.parentId(orderId);		
	stopLoss.transmit(false);

        //Create a new contract
        stock.createContract(cusip);
        
        List<Order> bracketOrder = new ArrayList<Order>();
	bracketOrder.add(parent);
	bracketOrder.add(takeProfit);
	bracketOrder.add(stopLoss);

        //remember which orders are working
        workingOrders.put(orderId, parent);
        client.placeOrder(orderId, stock.contract, parent);
//        client.placeOrder(takeProfit.orderId(), stock.contract, takeProfit);
//        client.placeOrder(stopLoss.orderId(), stock.contract, stopLoss);

        //Show order in console
        SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm:ss");
        String current_time_str = time_formatter.format(System.currentTimeMillis());
        System.out.println("////////////////////////////////////////////////\n"
                + "#Limit Price: " + parent.m_lmtPrice + "///////////////////////////\n"
                + "#Client number: " + parent.m_clientId + "///////////////////////////\n"
                + "#OrderType: " + parent.m_orderType + "///////////////////////////\n"
                + "#Order Quantity: " + parent.m_totalQuantity + "///////////////////////////\n"
                + "#Account number: " + parent.m_account + "///////////////////////////\n"
                + "#Symbol: " + stock.contract.m_secId + "///////////////////////////\n"
                + "///////////////////////////////////////"
        );
    }

    //always impl the error callback so you know what's happening
    @Override
    public void error(int id, int errorCode, String errorMsg) {
        System.out.println(id + " " + errorCode + " " + errorMsg);
    }

    @Override
    public void nextValidId(int orderId) {
        System.out.println("next order id "+orderId);
        this.orderId = orderId;
    }
    
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        //so you know it's been filled
        System.out.println(EWrapperMsgGenerator.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld));
        //completely filled when remaining == 0, or possible to cancel order from TWS
        if (remaining == 0 || status.equals("Cancelled")){
            //remove from map, should always be there
            if (workingOrders.remove(orderId) == null) System.out.println("not my order!");
        }

        //if map is empty then exit program as all  orders have been filled
        if (workingOrders.isEmpty()){
            System.out.println("all done");
            client.eDisconnect();//will stop reader thread
            //now is when you stop the program, but since all 
            //non-daemon threads have finished, the jvm will close.
            //System.exit(0);
        }
    }
    
    @Override
    public void orderStatus( int orderId, String status, double filled, double remaining,
            double avgFillPrice, int permId, int parentId, double lastFillPrice,
            int clientId, String whyHeld){}
    
    @Override
    public void tickPrice( int tickerId, int field, double price, int canAutoExecute){}
    @Override
    public void tickSize( int tickerId, int field, int size){}
    @Override
    public void tickOptionComputation( int tickerId, int field, double impliedVol,
    		double delta, double optPrice, double pvDividend,
    		double gamma, double vega, double theta, double undPrice){}
	@Override
    public void tickGeneric(int tickerId, int tickType, double value){}
	@Override
    public void tickString(int tickerId, int tickType, String value){}
	@Override
    public void tickEFP(int tickerId, int tickType, double basisPoints,
			String formattedBasisPoints, double impliedFuture, int holdDays,
			String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate){}
   
    @Override
    public void openOrder( int orderId, Contract contract, Order order, OrderState orderState){}
    @Override
    public void openOrderEnd(){}
    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName){}
    @Override
    public void updatePortfolio(Contract contract, double position, double marketPrice, double marketValue,
            double averageCost, double unrealizedPNL, double realizedPNL, String accountName){}
    @Override
    public void updateAccountTime(String timeStamp){}
    @Override
    public void accountDownloadEnd(String accountName){}    
    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails){}
    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails){}
    @Override
    public void contractDetailsEnd(int reqId){}
    @Override
    public void execDetails( int reqId, Contract contract, Execution execution){}
    @Override
    public void execDetailsEnd( int reqId){}
    @Override
    public void updateMktDepth( int tickerId, int position, int operation, int side, double price, int size){}
    @Override
    public void updateMktDepthL2( int tickerId, int position, String marketMaker, int operation,
    		int side, double price, int size){}
    @Override
    public void updateNewsBulletin( int msgId, int msgType, String message, String origExchange){}
    @Override
    public void managedAccounts( String accountsList){}
    @Override
    public void receiveFA(int faDataType, String xml){}
    @Override
    public void historicalData(int reqId, String date, double open, double high, double low,
                      double close, int volume, int count, double WAP, boolean hasGaps){}
    @Override
    public void scannerParameters(String xml){}
    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance,
    		String benchmark, String projection, String legsStr){}
    @Override
    public void scannerDataEnd(int reqId){}
    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count){}
    @Override
    public void currentTime(long time){}
    @Override
    public void fundamentalData(int reqId, String data){}
    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract underComp){}
    @Override
    public void tickSnapshotEnd(int reqId){}
    @Override
    public void marketDataType(int reqId, int marketDataType){}
    @Override
    public void commissionReport(CommissionReport commissionReport){}
    @Override
    public void position(String account, Contract contract, double pos, double avgCost){}
    @Override
    public void positionEnd(){}
    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency){}
    @Override
    public void accountSummaryEnd(int reqId){}
    @Override
    public void verifyMessageAPI( String apiData){}
    @Override
    public void verifyCompleted( boolean isSuccessful, String errorText){}
    @Override
    public void verifyAndAuthMessageAPI( String apiData, String xyzChallange){}
    @Override
    public void verifyAndAuthCompleted( boolean isSuccessful, String errorText){}
    @Override
    public void displayGroupList( int reqId, String groups){}
    @Override
    public void displayGroupUpdated( int reqId, String contractInfo){}
    @Override
    public void error( Exception e){}
    @Override
    public void error( String str){}    
    @Override
    public void connectionClosed(){}
    @Override
    public void connectAck(){}
    @Override
    public void positionMulti( int reqId, String account, String modelCode, Contract contract, double pos, double avgCost){}
    @Override
    public void positionMultiEnd( int reqId){}
    @Override
    public void accountUpdateMulti( int reqId, String account, String modelCode, String key, String value, String currency){}
    @Override
    public void accountUpdateMultiEnd( int reqId){}
    
    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {}
    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes){}
    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId){}
}
