/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdriver;

import webdriver.invoicesat.JSWaiter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import webdriver.gui.Invoices;
import webdriver.invoicesat.InvoiceSAT;

/**
 *
 * @author Carlos Israel
 */
public class Webdriver {
    private static String date = "2017-12", //2017-12-07
                          cert = "CERT.cer",
                          privateKey = "Claveprivada_FIEL.key",
                          pass = "";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        FileHandler fhd = null;
        try{
            Date d = new Date();
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            fhd = new FileHandler(dt.format(d)+".log");
            SimpleFormatter formatter = new SimpleFormatter();  
            fhd.setFormatter(formatter); 
            Logger.getLogger(Webdriver.class.getName()).addHandler(fhd);
            Logger.getLogger(Invoices.class.getName()).addHandler(fhd);
            Logger.getLogger(InvoiceSAT.class.getName()).addHandler(fhd);
            Logger.getLogger(Chromium.class.getName()).addHandler(fhd);
            Logger.getLogger(JSWaiter.class.getName()).addHandler(fhd);
            // And now use this to visit Google
            boolean gui = true;
            if (args.length > 0){
                for (String arg : args){
                    if (arg.equalsIgnoreCase("silent"))
                        gui = false;
                    else if (arg.equalsIgnoreCase("date="))
                        date = arg.substring(arg.indexOf("=")+1);
                    else if (arg.equalsIgnoreCase("cert="))
                        cert = arg.substring(arg.indexOf("=")+1);
                    else if (arg.equalsIgnoreCase("pk="))
                        privateKey = arg.substring(arg.indexOf("=")+1);
                    else if (arg.equalsIgnoreCase("pass="))
                        pass = arg.substring(arg.indexOf("=")+1);
                }
            }
            if (!gui) {
                if (date.length() > 4)
                    Chromium.downloadInvoicesDate(cert, privateKey, pass, date);
                else
                    Chromium.downloadInvoicesYear(cert, privateKey, pass, date);
                fhd.close();
            }else{
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Invoices gui = new Invoices();
                        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        gui.setCert(cert);
                        gui.setDateQuery(date);
                        gui.setPassword(pass);
                        gui.setPrivateKey(privateKey);
                        gui.pack();
                        gui.setVisible(true);
                    }
                });
            }
        }catch(IOException | SecurityException ex){
            Logger.getLogger(Webdriver.class.getName()).log(Level.SEVERE, ex.toString());
            if (fhd != null)
                fhd.close();
        }
    }
        
}
