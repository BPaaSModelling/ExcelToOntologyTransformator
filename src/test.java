
public class test {

	public static void main(String[] args) {
		String pathExcel = "C:\\Users\\adnan\\Desktop\\CouldService Prototype.xlsm";
		String pathOntology = "C:\\Users\\adnan\\Documents\\GitHub\\CloudSocket-Ontology\\";
		Operation op = new Operation();
		op.parseOntology(pathOntology + "apqc.ttl");
		op.parseOntology(pathOntology + "bdata.ttl");
		op.parseOntology(pathOntology + "bpaas.ttl");
		op.parseOntology(pathOntology + "fbpdo.ttl");
		op.parseOntology(pathOntology + "questiondata.ttl");
		op.parseOntology(pathOntology + "questionnaire.ttl");
		

		op.parseExcelFile(pathExcel);
		System.out.println(op.getServices().size());
		for (int i = 0; i < op.getServices().size(); i++){
			System.out.println("Name: "+ op.getServices().get(i).getName());
			for (int j = 0; j < op.getServices().get(i).getProperties().size(); j++){
				System.out.println(op.getServices().get(i).getProperties().get(j).name + " " + op.getServices().get(i).getProperties().get(j).value);
			}
		}
	}
//hello
} 
