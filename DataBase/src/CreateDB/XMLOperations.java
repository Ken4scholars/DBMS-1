package CreateDB;
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;  
import javax.xml.transform.TransformerException;  
import javax.xml.transform.TransformerFactory;  
import javax.xml.transform.dom.DOMSource;  
import javax.xml.transform.stream.StreamResult;  

import org.w3c.dom.Attr;  
import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
  
public class XMLOperations {  
	public void createTable(Table t, String DBName, String TName) {
	 
		String path = new String();
		File theDir = new File(DBName);
		 
		TName = TName.concat(".xml");
		 
		if (theDir.exists()) {
		
			try{
				path = theDir.getAbsolutePath();
				
			} catch(SecurityException e){
			
			}        
		
		}
		else {
			return;
		}
		try {  
	  
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory  
					.newInstance();  
			DocumentBuilder documentBuilder = documentFactory  
					.newDocumentBuilder();  
	  
			Document document = documentBuilder.newDocument();  
			Element rootElement = document.createElement("table");  
			document.appendChild(rootElement);
			Attr attribute = document.createAttribute("name");  
			attribute.setValue(t.getName());  
			rootElement.setAttributeNode(attribute); 
	   
			ArrayList<Column> column = t.getColumns();
	 
			for(Column col : column){
				Element e = document.createElement("column");
				rootElement.appendChild(e);
				attribute = document.createAttribute("name");  
	   			attribute.setValue(col.getName());
	   			e.setAttributeNode(attribute);
	   			
	   			attribute = document.createAttribute("type");  
	   			attribute.setValue(col.getType());
	   			e.setAttributeNode(attribute);
	   			
	   			for(String s : col.getCells()){
	   				Element cell = document.createElement("cell");  
	   				cell.appendChild(document.createTextNode(s)); 
	 
	   				e.appendChild(cell); 
				   }
				   
			   }
	 
			TransformerFactory transformerFactory = TransformerFactory  
					.newInstance();  
			Transformer transformer = transformerFactory.newTransformer();  
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource domSource = new DOMSource(document);  
			StreamResult streamResult = new StreamResult();;
			try {
				streamResult = new StreamResult(new FileOutputStream(new File(path,TName), false));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			transformer.transform(domSource, streamResult);  
	  
		} catch (ParserConfigurationException pce) {  
			pce.printStackTrace();  
		} catch (TransformerException tfe) {  
			tfe.printStackTrace();  
		}  
 	}
	
	public Table readTable(String DBName, String TName) throws Exception{
		String path = new String();
		TName = TName.concat(".xml");
		
		File theDir = new File(DBName,TName);
		 
		if (theDir.exists()) {
		
			try{
				path = theDir.getAbsolutePath();
			
			} catch(SecurityException e){
			
			}        
		
		}
		else {
			return null;
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		 
		DocumentBuilder builder = factory.newDocumentBuilder();
 
		Document document = builder.parse(new FileInputStream(path));
 
		NodeList nodeList = document.getDocumentElement().getChildNodes();
		Table table = new Table();
		table.setName(document.getDocumentElement().getAttribute("name"));
		for (int i = 0; i < nodeList.getLength(); i++) {
			
			Node node = nodeList.item(i);
			if (node instanceof Element) {
				Column column = new Column();
				column.setName(node.getAttributes().getNamedItem("name").getNodeValue());
				column.setType(node.getAttributes().getNamedItem("type").getNodeValue());
				
				NodeList childNodes = node.getChildNodes();
 
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node cNode = childNodes.item(j);
 
					if (cNode instanceof Element) {
						String content = cNode.getTextContent();
						column.getCells().add(content);
					}
				}
				table.getColumns().add(column);
			}
		}
		return table;
	}
	 
}  