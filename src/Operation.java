import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Operation {
	private ArrayList<OntologyClass> classes;					//it contains all the classes of the ontology
	public ArrayList<OntologyClass> getClasses() {
		return classes;
	}

	public void setClasses(ArrayList<OntologyClass> classes) {
		this.classes = classes;
	}

	public ArrayList<OntologyProperty> getProperties() {
		return properties;
	}

	public void setProperties(ArrayList<OntologyProperty> properties) {
		this.properties = properties;
	}

	public ArrayList<OntologyInstance> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<OntologyInstance> instances) {
		this.instances = instances;
	}

	public ArrayList<String> getOntologyPreamble() {
		return ontologyPreamble;
	}

	public void setOntologyPreamble(ArrayList<String> ontologyPreamble) {
		this.ontologyPreamble = ontologyPreamble;
	}

	public ArrayList<CloudService> getServices() {
		return services;
	}

	public void setServices(ArrayList<CloudService> services) {
		this.services = services;
	}

	private ArrayList<OntologyProperty> properties;				//it contains all the properties of the ontology
	private ArrayList<OntologyInstance> instances;				//it contains all the instances of the ontology
	private ArrayList<String> ontologyPreamble;	
	private ArrayList<CloudService> services;
	
	public Operation() {
		super();
		classes = new ArrayList<OntologyClass>();
		properties = new ArrayList<OntologyProperty>();
		instances = new ArrayList<OntologyInstance>();
		ontologyPreamble = new ArrayList<String>();
		services = new ArrayList<CloudService>();
	}

	public int[] parseOntology(String path_file){//this method parse the ontology
		//takes in input the path of the file and it fills the four arrays
		//stored this java-class (classes, properties, instances and the ontologyPreamble)
		//returns an array with the number of classes, properties and instances loaded
		String line = null;
		FileReader reader = null;
		
		try {
			reader = new FileReader(path_file);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			
		}
		Scanner scanner = new Scanner(reader);
		//Set the variable "startPreamble" to detect when the preamble finish
		boolean preamble = true;
		ArrayList<String> temp_type = null;
		String temp_name = null;
		ArrayList<OntologyAttribute> temp_attributes = null;
		boolean titleLine = true;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			
			if (preamble){
				//here we are in the preamble
				if (!line.startsWith(".")){
				ontologyPreamble.add(line);
			}else {
				//here we are in the first dot
				//where the ontology elements starts
				preamble = false;
				
			}
			//here we are still in the preamble
			//where the line doesn't start with "."
			
			}else {
				//here we are not in the preamble and we start to analyse
				//the elements of the ontology
				if (titleLine && !line.startsWith(".")){
					//first line of the object of the ontology
					//here we can find prefix:name of the object
					//and we instantiate all the data structures
					temp_type = new ArrayList<String>();
					temp_name = new String();
					temp_attributes = new ArrayList<OntologyAttribute>();
		
					temp_name = line;
					titleLine = false;
				} else if (!titleLine && line.trim().startsWith(".")){
					//end of the object of ontology
					titleLine = true;
					for (int t = 0; t < temp_type.size();t++){
					if (temp_type.get(t).equals("owl:Class")){
						//Parsing a Class
						OntologyClass c = new OntologyClass(temp_name,temp_type,temp_attributes);
						classes.add(c);
						break;
					} else if (temp_type.get(t).equals("owl:AnnotationProperty") ||
								temp_type.get(t).equals("owl:DatatypeProperty") ||
								temp_type.get(t).equals("owl:DeprecatedProperty") ||
								temp_type.get(t).equals("owl:FunctionalProperty") ||
								temp_type.get(t).equals("owl:ObjectProperty")) {
						//Parsing a Property
						OntologyProperty p = new OntologyProperty(temp_name,temp_type,temp_attributes);
						properties.add(p);
						break;
					
					} else if (t == temp_type.size()-1){
						//Parsing an Istance
						OntologyInstance i = new OntologyInstance(temp_name,temp_type,temp_attributes);
						instances.add(i);
					}
					}
						
				} else {
					//body of the object of the ontology
					String[] arraySplittate = parseAttributeName(line);
					if (arraySplittate[0].equals("rdf:type")){
						temp_type.add(arraySplittate[1].replaceAll(";","").trim());
					} else {
						//parsing attributes
						OntologyAttribute oa;
						String[] arraySplittate2 = arraySplittate[1].trim().split("\\^\\^"); //splitting the string with "^^" so we can define type and value
						if (arraySplittate2.length == 2){	//if the split has type and value
						oa = new OntologyAttribute(
								arraySplittate[0].replaceAll(";","").trim(),
								arraySplittate2[1].replaceAll(";","").trim(),
								arraySplittate2[0].replaceAll("\"", "").replaceAll(";","").trim()); //name,type,value
						
						} else {
						oa = new OntologyAttribute(
								arraySplittate[0].replaceAll(";","").trim(),
								"",
								arraySplittate2[0].replaceAll(";","").trim());	//name,type,value
						}
						temp_attributes.add(oa);
					}
				}
				
		
			
	}//end not preamble
	
}//end while scanner
		scanner.close();
	
		//-------------TESTING THE WHOLE ONTOLOGY-------------
		/*
	 	System.out.println("Number of lines of preamble: " + ontologyPreamble.size());
		System.out.println("Number of classes: "+classes.size());
		for (int i = 0; i < classes.size(); i++){
			System.out.print("    "+classes.get(i).getPrefix()+":"+classes.get(i).getName());
			System.out.print(" - Type: ");
			for(int j=0;j<classes.get(i).getTypes().size();j++){
				System.out.print(classes.get(i).getTypes().get(j)+" ,");
			}
		System.out.println("");
		}
		System.out.println("Number of properties: "+properties.size());
		for (int i = 0; i < properties.size(); i++){
			System.out.print("    "+properties.get(i).getPrefix()+":"+properties.get(i).getName());
			System.out.print(" - Type: ");
			for(int j=0;j<properties.get(i).getTypes().size();j++){
				System.out.print(properties.get(i).getTypes().get(j)+" ,");
			}
		System.out.println("");
		}
		System.out.println("Number of instances: "+instances.size());
		for (int i = 0; i < instances.size(); i++){
			System.out.print("    "+instances.get(i).getPrefix()+":"+instances.get(i).getName());
			System.out.print(" - Type: ");
			for(int j=0;j<instances.get(i).getTypes().size();j++){
				System.out.print(instances.get(i).getTypes().get(j)+" ,");
			}
		System.out.println("");
		}
		*/
		
		//--------------------------TESTING THE CLASSES OF ONTOLOGY---------------------
		/*
		System.out.println("Classes:");
		for (int i = 0; i < classes.size(); i++){
			System.out.println("   Name: "+classes.get(i).getPrefix()+":"+classes.get(i).getName());
			System.out.println("     Types:");
			for (int j = 0; j < classes.get(i).getTypes().size();j++){
				System.out.println("       "+classes.get(i).getTypes().get(j));
			}
			System.out.println("     Attributes:");
			for (int j = 0; j < classes.get(i).getAttributes().size(); j++){
				 System.out.println("       name: "+classes.get(i).getAttributes().get(j).getName());
				 System.out.println("       type: "+classes.get(i).getAttributes().get(j).getType());
				 System.out.println("       value: "+classes.get(i).getAttributes().get(j).getValue());
				 System.out.println("       -------");
			}
			System.out.println("--------------------");
		}
		
		//--------------------------TESTING THE PROPERTIES OF ONTOLOGY---------------------
		
		System.out.println("Property:");
		for (int i = 0; i < properties.size(); i++){
			System.out.println("   Name: "+properties.get(i).getPrefix()+":"+properties.get(i).getName());
			System.out.println("     Types:");
			for (int j = 0; j < properties.get(i).getTypes().size();j++){
				System.out.println("       "+properties.get(i).getTypes().get(j));
			}
			System.out.println("     Attributes:");
			for (int j = 0; j < properties.get(i).getAttributes().size(); j++){
				 System.out.println("       name: "+properties.get(i).getAttributes().get(j).getName());
				 System.out.println("       type: "+properties.get(i).getAttributes().get(j).getType());
				 System.out.println("       value: "+properties.get(i).getAttributes().get(j).getValue());
				 System.out.println("       -------");
			}
			System.out.println("--------------------");
		}
		
		//--------------------------TESTING THE PROPERTIES OF ONTOLOGY---------------------
		System.out.println("Instances:");
		for (int i = 0; i < instances.size(); i++){
			System.out.println("   Name: "+instances.get(i).getPrefix()+":"+instances.get(i).getName());
			System.out.println("     Types:");
			for (int j = 0; j < instances.get(i).getTypes().size();j++){
				System.out.println("       "+instances.get(i).getTypes().get(j));
			}
			System.out.println("     Attributes:");
			for (int j = 0; j < instances.get(i).getAttributes().size(); j++){
				 System.out.println("       name: "+instances.get(i).getAttributes().get(j).getName());
				 System.out.println("       type: "+instances.get(i).getAttributes().get(j).getType());
				 System.out.println("       value: "+instances.get(i).getAttributes().get(j).getValue());
				 System.out.println("       -------");
			}
			System.out.println("--------------------");
		}
		*/
		return new int[] {classes.size(),properties.size(),instances.size()};
		
	}//end method
	
	private String[] parseAttributeName(String attribute_line){	//this method parse the name-value of an Ontology attribute
		attribute_line.replaceAll(";", "").trim();	//we remove the final ";"
		String[] arraySplittate = attribute_line.trim().split(" ");
		String temp_value = "";
		for (int i = 1; i < arraySplittate.length;i++){
		temp_value = temp_value + " " + arraySplittate[i].trim();
		}
		return new String[]{arraySplittate[0],temp_value};
	}

	public void parseExcelFile(String path_file){
		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(new File(path_file));
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()){
				//parsing the rows
				Row row = rowIterator.next();
				CloudService cs = new CloudService();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()){
					Cell cell = cellIterator.next();
					//if (row.getRowNum() > 1){
					if (row.getRowNum() == 2){
						
						switch(cell.getColumnIndex()){
						case 0:
							cs.setName(cell.toString());
							break;
						case 8:
							//Here i am parsing Object
								for (int j = 0; j < classes.size(); j++){
								
									if (classes.get(j).getLabel().equals(cell.toString())){
										cs.properties.add(new CloudServiceProperty("bpaas:cloudServiceHasObject",classes.get(j).getName()));
								}
							}
						}
						
					}
				}
				services.add(cs);
			}
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
