
public class Main {

	public static void main(String[] args) { // first argument is medicament.txt
												//  second is prescription.txt
		Read object = new Read();
		object.create_objects();
		object.readFilePrescription(args[0]);		
		object.readFileMedicaments(args[1]);				
		object.calculatePrice(args[1], args[0]);
	}
}
