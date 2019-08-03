/* Copyright (C) 2013 Interactive Brokers LLC. All rights reserved.  This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package Client.JBClient;

import Client.*;
import com.ib.client.EWrapper;
import com.ib.client.EWrapperMsgGenerator;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import com.ib.client.TagValue;
import javax.swing.JFrame;
import javax.swing.JSlider;


import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import com.ib.client.EWrapper;
import com.ib.client.EWrapperMsgGenerator;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.SoftDollarTier;

import Client.JBClient.OrderManagement;
import Client.JBClient.Stock;


public class StartPanel extends JFrame{
    	
    private JTextField 		m_quantity = new JTextField( "");
    private JTextField 		m_percent = new JTextField( "");
    private JRadioButton        m_buy = new JRadioButton("buy");    
    private JRadioButton        m_sell = new JRadioButton("sell");
    private ButtonGroup         m_group = new ButtonGroup();
    private JTextField 		m_tag = new JTextField( "");
    private JTextField 		m_value = new JTextField( "");
    private JButton 		m_addParam = new JButton( "Add");
    private JButton	 	    m_removeParam = new JButton( "Remove");
    private JButton 		m_sendOrder = new JButton( "Open Basket Order");
    private JButton	 	    m_closeBasket = new JButton( "Close Basket");
    private AlgoParamModel 	m_paramModel = new AlgoParamModel();
    private JTable 		    m_paramTable = new JTable(m_paramModel);
    private JScrollPane 	m_paramPane = new JScrollPane(m_paramTable);
    public AlgoParamModel       paramModel() { return m_paramModel; }
    private JSlider targetSlider = new JSlider(0,100,30);
    private JSlider limitSlider = new JSlider(0,100,20);
    private JSlider stopSlider = new JSlider(0,100,100);
    
    private EClientSocket       m_econnect;
    OrderManagement             order = null;

    public StartPanel() {

        setTitle( "Interactive Broker API");

        JPanel pAlgoPanel = new JPanel( new GridLayout( 0, 2, 10, 10) );
        pAlgoPanel.setBorder( BorderFactory.createTitledBorder( "Values") );
        pAlgoPanel.add( new JLabel( "Quantity:") );
        m_quantity.setText("");
        pAlgoPanel.add(m_quantity);
        m_buy.setSelected(true);
        m_group.add(m_buy);
        m_group.add(m_sell);
        pAlgoPanel.add(m_buy);
        pAlgoPanel.add(m_sell);
        pAlgoPanel.add( new JLabel( "Close(%):") );
        m_percent.setText("");
        pAlgoPanel.add(m_percent);
        
        JPanel orderPanel = new JPanel( new GridLayout( 0, 2, 10, 10));
        orderPanel.setBorder( BorderFactory.createTitledBorder( "Options"));
        orderPanel.add(new JLabel("Profit Target", 0));
//        orderPanel.add(new JLabel("Stop Limit", 0));
        orderPanel.add(new JLabel("Stop Loss", 0));
        targetSlider.setMinorTickSpacing(10);        
        targetSlider.setPaintTicks(true);
        limitSlider.setMinorTickSpacing(10);        
        limitSlider.setPaintTicks(true);        
        stopSlider.setMinorTickSpacing(10);        
        stopSlider.setPaintTicks(true);        
        orderPanel.add(targetSlider);
//        orderPanel.add(limitSlider);
        orderPanel.add(stopSlider);
        orderPanel.add(new JLabel(targetSlider.getValue()+" ticks", 0));
//        orderPanel.add(new JLabel(limitSlider.getValue()+" ticks", 0));
        orderPanel.add(new JLabel(stopSlider.getValue()+" ticks", 0));

        // create algo params panel
        JPanel pParamList = new JPanel( new GridLayout( 0, 1, 10, 10) );
        pParamList.setBorder( BorderFactory.createTitledBorder( "Basket") );

//        ArrayList<TagValue> algoParams = m_order.algoParams();
//        if (algoParams != null) {
//        	m_paramModel.algoParams().addAll(algoParams);
//        }
        pParamList.add( m_paramPane);

        // create combo details panel
        JPanel pParamListControl = new JPanel( new GridLayout( 0, 2, 10, 10) );
        pParamListControl.setBorder( BorderFactory.createTitledBorder( "Add / Remove") );
        pParamListControl.add( new JLabel( "Insert new ticker:") );
        pParamListControl.add( m_tag);
//        pParamListControl.add( new JLabel( "Value:") );
//        pParamListControl.add( m_value);
        pParamListControl.add( m_addParam);
        pParamListControl.add( m_removeParam);

        // create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( m_sendOrder);
        buttonPanel.add( m_closeBasket);

        JPanel topPanel = new JPanel();
        topPanel.setLayout( new BoxLayout( topPanel, BoxLayout.Y_AXIS) );        
        topPanel.add( pParamList);
        topPanel.add( pParamListControl);
        topPanel.add( pAlgoPanel);

        getContentPane().add( topPanel, BorderLayout.NORTH);
        getContentPane().add( orderPanel, BorderLayout.CENTER);
        getContentPane().add( buttonPanel, BorderLayout.SOUTH);        

        // create action listeners
        m_addParam.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                onAddParam();
            }
        });
        m_removeParam.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                onRemoveParam();
            }
        });
        m_sendOrder.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                onSendOrder();
            }
        });
        m_closeBasket.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                onCloseBasket();
            }
        });

        setSize(400, 850);
        centerOnOwner( this);
    }

     public void onAddParam() {
        try {
            String tag = m_tag.getText();
            String value = m_value.getText();

            m_paramModel.addParam( new TagValue(tag, value));
        }
        catch( Exception e) {
            reportError( "Error - ", e);
            return;
        }
    }

    public void onRemoveParam() {
        try {
            if ( m_paramTable.getSelectedRowCount() != 0 ) {
                int[] rows = m_paramTable.getSelectedRows();
                for ( int i=rows.length -1; i>=0 ; i-- ) {
                        m_paramModel.removeParam( rows[i]);
                }
            }
        }
        catch( Exception e) {
            reportError( "Error - ", e);
            return;
        }
    }

    void onSendOrder() {  
        String action = "buy";
        if(!m_buy.isSelected())
            action = "sell";
        String quantity = m_quantity.getText().toString();
        if(quantity.equals("")){
            JOptionPane.showMessageDialog(null, "Insert quantity of orders");
            return;
        }
    	ArrayList<TagValue> orderTickers = m_paramModel.algoParams();
        if(orderTickers.isEmpty()){
            JOptionPane.showMessageDialog(null, "Add more than one tickers");
            return;
        }
        order = new OrderManagement();        
        for(int i=0; i<orderTickers.size(); i++){
            order.sendMarketOrder("922908363", action, Integer.parseInt(quantity), targetSlider.getValue(), stopSlider.getValue());
        }
                
    }

    void onCloseBasket() {
        String closePercent = m_percent.getText().toString();
        if(order != null){
            order.closeBasket(closePercent);
        }
    }

    void reportError( String msg, Exception e) {
        Main.inform( this, msg + " --" + e);
    }

    private static void centerOnOwner( Window window) {
        Window owner = window.getOwner();
        if( owner == null) {
            return;
        }
        int x = owner.getX() + ((owner.getWidth()  - window.getWidth())  / 2);
        int y = owner.getY() + ((owner.getHeight() - window.getHeight()) / 2);
        if( x < 0) x = 0;
        if( y < 0) y = 0;
        window.setLocation( x, y);
    }
}

class AlgoParamModel extends AbstractTableModel {
    private ArrayList<TagValue> m_allData = new ArrayList<TagValue>();

    synchronized public void addParam( TagValue tagValue) {
        m_allData.add( tagValue);
        fireTableDataChanged();
    }

    synchronized public void removeParam( int index) {
        m_allData.remove( index);
        fireTableDataChanged();
    }

    synchronized public void reset() {
        m_allData.clear();
		fireTableDataChanged();
    }

    synchronized public int getRowCount() {
        return m_allData.size();
    }

    synchronized public int getColumnCount() {
        return 1;
    }

    synchronized public Object getValueAt(int r, int c) {
        TagValue tagValue = m_allData.get(r);

        switch (c) {
            case 0:
                return tagValue.m_tag;
//            case 1:
//                return tagValue.m_value;
            default:
                return "";
        }
    }

    public boolean isCellEditable(int r, int c) {
        return false;
    }

    public String getColumnName(int c) {
        switch (c) {
            case 0:
                return "tickers";
//            case 1:
//                return "Value";
            default:
                return null;
        }
    }

    public ArrayList<TagValue> algoParams() {
        return m_allData;
    }   
    
}
