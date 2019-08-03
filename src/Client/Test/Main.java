/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Test;

/**
 *
 * @author SeniorDeveloper
 */
import java.sql.SQLException;
import Client.Test.OrderManagement;
public class Main {

public static void main(String[] args) throws InterruptedException, ClassNotFoundException, SQLException {
    OrderManagement order = new OrderManagement();
    order.sendMarketOrder("MSFT","BUY", 70);
    order.sendMarketOrder("GOOG","BUY", 90);
    order.sendMarketOrder("APPL","BUY", 100);
//    System.exit(0);
}
}
