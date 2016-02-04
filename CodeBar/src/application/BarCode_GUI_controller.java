package application;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.layout.VBox;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.SAXException;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;

public class BarCode_GUI_controller implements Initializable {
	
	// FXML DECLARATIONS
	@FXML
	private TextField textField;
	@FXML
	private Button button;

	
	// OTHER DECLARATIONS
	private DefaultConfigurationBuilder builder;
	private File cfgFile;
	private Configuration cfg;
	private String home ;
	private String myMessage;
	private BarcodeGenerator frag2;
	
	public void generateBarCode(String s){
		
		myMessage = textField.getText();
		
		System.out.println(myMessage);
		
	    try {
			frag2 = BarcodeUtil.getInstance()
					.createBarcodeGenerator(cfg);
		} catch (ConfigurationException | BarcodeException e) {
			e.printStackTrace();
		}
	    
	    OutputStream out = null;
		try {
			out = new java.io.FileOutputStream(new File(Paths.get(home).resolve("images_svg").resolve(String.format("%s.png", myMessage)).toString()));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	    BitmapCanvasProvider provider = new BitmapCanvasProvider(
	        out, "image/x-png", 400, BufferedImage.TYPE_BYTE_GRAY, true, 0);
	    frag2.generateBarcode(provider, String.format("-$ %s $-", myMessage));
	    try {
			provider.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    ImageView view = new ImageView();
	    try {
			view.setImage(new Image(Paths.get(home).resolve("images_svg").resolve(String.format("%s.png", myMessage)).toUri().toURL().toString()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	    
	    if ( ((VBox) textField.getParent()).getChildren().size() > 2)
	    {
	    	((VBox) textField.getParent()).getChildren().remove(2);
	    }
	    ((VBox) textField.getParent()).getChildren().add(view);
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		SvgImageLoaderFactory.install();
		
		home =  System.getProperty("user.home");
		String cfgPath = Paths.get(home).resolve("barCode.cfg").toString();
		System.out.println(cfgPath);
		
		builder = new DefaultConfigurationBuilder();
	    cfgFile = new File(cfgPath);
	    try {
			cfg = builder.buildFromFile(cfgFile);
			System.out.println("cfg build : OK");
		} catch (ConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		 
	    button.setText("Générer le code barre (code39)");
        button.setOnAction(a -> generateBarCode(textField.getText()));	
        textField.setOnKeyPressed(k -> {
        	if (k.getCode().equals(KeyCode.ENTER)){
        		generateBarCode(textField.getText());
        	}
        } );
    
//    	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//    	String line = "";
//
//	    while (line.equalsIgnoreCase("quit") == false) {
//	       try {
//				line = in.readLine();
//				
//				if (line.startsWith("-$") && line.endsWith("$-")){
//					
//					System.out.println("Une ligne de scan : " + line);
//				}
//					
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//	   
//        }
//    	try {
//			in.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}


}
