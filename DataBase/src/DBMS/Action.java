package DBMS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Action {

	private String[] getCondition(String condition) {
		String[] ans = new String[3];
		for (int i = 0; i < condition.length(); i++) {
			if (condition.charAt(i) == '=') {
				ans[1] = "=";
				break;
			} else if (condition.charAt(i) == '<') {
				ans[1] = "<";
				break;
			} else if (condition.charAt(i) == '>') {
				ans[1] = ">";
				break;
			}
		}
		String[] parts = condition.split(ans[1]);

		ans[0] = parts[0].trim();
		ans[2] = parts[1].trim();
		
		String ans2 = "";
		for(int i=0; i<ans[2].length(); i++)
			if(ans[2].charAt(i)!='\'' && ans[2].charAt(i)!='\"' )
				ans2+=ans[2].charAt(i);
		ans[2] = ans2;
		return ans;
	}
	
	private Document getDocument(String dbName, String tName) throws Throwable {
		String fileName = dbName+"/"+tName+".xml";
		File file = new File(fileName);
		if(file.exists()){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file.getAbsolutePath());
			return document;
		}
		return null;
	}
	
	private Node getColNode(String dbName, String tName, String colName)
			throws Throwable {
		Document document = getDocument(dbName, tName);
		NodeList nodeList = document.getDocumentElement().getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element && colName.equalsIgnoreCase(((Element) node).getAttribute("name")))
				return node;
		}
		return null;
	}
	
	private String getCellFromCol(String dbName, String tName, String colName,int cellNum) throws Throwable{
		Node colNode = getColNode(dbName, tName, colName);
		NodeList cellNodes = colNode.getChildNodes();
		return cellNodes.item(cellNum*2+1).getTextContent();
	}
	private String getRow(String dbName, String tName, int rowNum) throws Throwable {
		 String row = "";
		 Document doc = getDocument(dbName, tName);
		 NodeList colNodes = doc.getDocumentElement().getChildNodes();
		 for(int i=0; i<colNodes.getLength(); i++){
			 Node colNode = colNodes.item(i);
			 if(colNode instanceof Element)
				 row+=getCellFromCol(dbName, tName, ((Element) colNode).getAttribute("name"), rowNum)+" ";
		 }
		 return row.trim();
	 }
	
	private String getRow(String dbName, String tName, int rowNum, ArrayList<String> columns) throws Throwable {
		 String row = "";
		 for(int i=0; i<columns.size(); i++)
			row+=getCellFromCol(dbName, tName, columns.get(i), rowNum)+" ";
		 return row.trim();
	 }
	
	private int getNumOfRows(String dbName, String tName) throws Throwable{
		Document doc = getDocument(dbName, tName);
		int nOfCols = doc.getElementsByTagName("column").getLength();		//can't be zero
		int nOfCells = doc.getElementsByTagName("cell").getLength();
		int nOfRows = nOfCells/nOfCols;
		return nOfRows;
	}
	
	private ArrayList<Integer> selRowsFromTable(String dbName, String tName, String condition)throws Throwable {
		ArrayList<Integer> rows = new ArrayList<Integer>();
		String[] cnd = getCondition(condition);
		
		Node colNode = getColNode(dbName, tName, cnd[0]);
		if (colNode != null) {
			NodeList childList = colNode.getChildNodes();
			for (int j = 0; j < childList.getLength(); j++) {
				Node cNode = childList.item(j);
				if (cNode instanceof Element) {
					String content = cNode.getTextContent();
					if (((Element) colNode).getAttribute("type").equals("1")) { // String
						if (cnd[1].equals("=") && cnd[2].equals(content))
							rows.add((j - 1) / 2);
					} else { // int
						try {
							if (cnd[1].equals("=")) {
								if (Integer.parseInt(cnd[2]) == Integer.parseInt(content))
									rows.add((j - 1) / 2);
							} else if (cnd[1].equals("<")) {
								if (Integer.parseInt(content) < Integer.parseInt(cnd[2]))
									rows.add((j - 1) / 2);
							} else {
								if (Integer.parseInt(content) > Integer.parseInt(cnd[2]))
									rows.add((j - 1) / 2);
							}
						} catch (Exception e) {
						}
					}

				}
			}
		}
		return rows;
	}

	public String selectFromTable(String dbName, String tName, String condition)throws Throwable {
		String ans = "";
		ArrayList<Integer> rows = selRowsFromTable(dbName, tName, condition);
		for(int r:rows)
			ans+= getRow(dbName, tName, r)+"\n";
		return ans.length()==0? null:ans.trim();
	}
	
	public String selectFromTable(String dbName, String tName) throws Throwable{
		String ans = "";
		for(int i=0; i<getNumOfRows(dbName, tName); i++)
			ans+= getRow(dbName, tName, i)+"\n";
		return ans.trim();
	}
	
	public String selectFromTable(String dbName, String tName, ArrayList<String> columns) throws Throwable{
		String ans = "";
		for(int i=0; i<getNumOfRows(dbName, tName); i++)
			ans+= getRow(dbName, tName, i, columns)+"\n";
		return ans.trim();
	}
	
	public String selectFromTable(String dbName, String tName, ArrayList<String> columns, String condition) throws Throwable{
		String ans = "";
		ArrayList<Integer> rows = selRowsFromTable(dbName, tName, condition);
		for(int r:rows)
			ans+= getRow(dbName, tName, r, columns)+"\n";
		return ans.length()==0? null:ans.trim();
	}

    private void transform(Document doc, String dbName, String tName) throws TransformerException{        
    	String path = new File(dbName).getAbsolutePath();
    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource domSource = new DOMSource(doc);
		StreamResult streamResult = new StreamResult();
		try {
			streamResult = new StreamResult(new FileOutputStream(new File(path, tName+".xml"), false));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		transformer.transform(domSource, streamResult);        
    }
    
    private void removeAllChildren(Node node){
      for (Node child; (child = node.getFirstChild()) != null; node.removeChild(child));
    }
    
    private void removeSomeChildren(Node node, ArrayList<Integer> indexOfNodesTBD){
		NodeList childNodes = node.getChildNodes();
		ArrayList<Node> nodesTBD = new ArrayList<Node>();
		for(int j:indexOfNodesTBD)
			nodesTBD.add(childNodes.item(j));
		
		for(Node n:nodesTBD)
			node.removeChild(n);
	}

    public boolean deleteFromTable(String dbName, String tName) throws Throwable{
		Document document = getDocument(dbName, tName);
		NodeList nodeList = document.getDocumentElement().getChildNodes();
		for(int i= 0; i< nodeList.getLength(); i++){
			Node node= nodeList.item(i);
			if(node instanceof Element)				
				removeAllChildren(node);
		}
		transform(document, dbName, tName);
		return true;
	}
    
	public boolean deleteFromTable(String dbName, String tName, String condition)throws Throwable {
		Document document = getDocument(dbName, tName);
		
		ArrayList<Integer> rows = selRowsFromTable(dbName, tName, condition);
		if(rows.size() == 0)
			return false;
		
		ArrayList<Integer> indexOfNodesTBD = new ArrayList<Integer>();
    	for(int r:rows){
    		indexOfNodesTBD.add(2*r);
    		indexOfNodesTBD.add(2*r+1);
    	}
    	
    	NodeList nodeList = document.getDocumentElement().getChildNodes();
		for(int i= 0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			if(node instanceof Element)
				removeSomeChildren(node, indexOfNodesTBD);
		}
		transform(document, dbName, tName);
		return true;
	}

	private  String[] adjustCond(String cond){
		
		String[] condParts ;
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < cond.length(); i++) {
			
			if(cond.charAt(i)=='"'){
				continue;
			}
			
			else if(cond.charAt(i)=='=' ||cond.charAt(i)=='>'||cond.charAt(i)=='<' ){
				str.append("#");
				str.append(cond.charAt(i));
				str.append("#");
			}
			else
				str.append(cond.charAt(i));
			
		}
		
		
		return condParts = str.toString().split("#");
		
	}
	
	private void transform(Document doc , String directory) throws TransformerException{
		
		TransformerFactory tf =  TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(directory));
		
		trans.transform(source, result);
		
	}

	public boolean updateTable(String dbName,String tableName , ArrayList<String> col , ArrayList<String> val , String cond){
		
		String tableURL = "./"+dbName+"/"+tableName+".xml" ;
		String[] condParts ;
		int colType =0 ;
		condParts = adjustCond(cond);

		try {
			
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(new File(tableURL));
				
				ArrayList<Integer> rowsNum = new ArrayList<Integer>();
				NodeList colsTags = doc.getElementsByTagName("column");
				int len = colsTags.getLength();
				
				for (int i = 0; i < len; i++) {
					
					if(colsTags.item(i).getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase(condParts[0]))
					{
						if(colsTags.item(i).getAttributes().getNamedItem("type").getNodeValue().equals("1"))
								colType=1;
						
						NodeList colu = colsTags.item(i).getChildNodes();
						int len2 = colu.getLength();
						
						for (int j = 1; j < len2 ; j+=2) {
							
							if(condParts[1].equals("="))
							{	
								if(colType==0 && Double.parseDouble(colu.item(j).getTextContent())==Double.parseDouble(condParts[2]) )
									rowsNum.add(j);
								
								else 
								{
									if(condParts[2].contains("'")){
										String[] tempor = condParts[2].split("'");
										
										if(colu.item(j).getTextContent().equals(tempor[1]))
											rowsNum.add(j);
									}
									else if(colu.item(j).getTextContent().equals(condParts[2]))
										rowsNum.add(j);
									
								}
							}
							else if(condParts[1].equals(">") && Double.parseDouble(colu.item(j).getTextContent())>Double.parseDouble(condParts[2]) )
								rowsNum.add(j);
							
							else if(condParts[1].equals("<") && Double.parseDouble(colu.item(j).getTextContent())<Double.parseDouble(condParts[2]))
								rowsNum.add(j);
						
						}
						break;
					}
				}
				
				if(rowsNum.isEmpty())
					return false ;
				
				for (int i = 0; i <col.size(); i++) {
					for (int j = 0; j < colsTags.getLength(); j++) {
						
						if(colsTags.item(j).getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase(col.get(i)))
						{
							NodeList elements = colsTags.item(j).getChildNodes(); 
							for (int j2 = 0; j2 <  rowsNum.size(); j2++){
								
								String v = val.get(i);
								if(v.contains("'")||v.charAt(0)=='"')
									v = v.substring(1, v.length()-1);
								
								elements.item(rowsNum.get(j2)).setTextContent(v);
							}
						}
					}
				}
				
				transform(doc,tableURL);
			
		} catch (Exception e) {
			e.printStackTrace();
			return false ;
		}
		
		
		return true ;
	}
	public boolean updateTable(String dbName ,String tableName , ArrayList<String> col , ArrayList<String> val ){

		String tableURL = "./"+dbName+"/"+tableName+".xml" ;
		
		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(tableURL));
			
			NodeList colsTags = doc.getElementsByTagName("column");
			for (int i = 0; i <col.size(); i++) {
				for (int j = 0; j < colsTags.getLength(); j++) {
					
					if(colsTags.item(j).getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase(col.get(i)))
					{
						NodeList elements = colsTags.item(j).getChildNodes(); 
						for (int j2 = 1; j2 < elements.getLength(); j2+=2){
							String v = val.get(i);
							if(v.contains("'")||v.charAt(0)=='"')
								v = v.substring(1, v.length()-1);
							
							elements.item(j2).setTextContent(v);
						}
						
						break;
					}
				}
			}

			transform(doc,tableURL);
			
		} catch (Exception e) {			
			return false ;
		}
		
		
		return true ;
	}
		
}
