import java.io.IOException;
import java.text.ParseException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class Read {

	Medicine[] medicine = new Medicine[100];
	SocialSecurityInstitution[] ssi = new SocialSecurityInstitution[100];
	Patient patient = new Patient();

	public void create_objects() {
		for (int i = 0; i < 100; i++) {
			medicine[i] = new Medicine();
			ssi[i] = new SocialSecurityInstitution();
		}
	}

	public void readFileMedicaments(String path) {
		try {
			int count = 0;
			int length = Files.readAllLines(Paths.get(path)).size();
			String[] results = new String[length];
			for (String line : Files.readAllLines(Paths.get(path))) {
				results[count++] = line;
			}
			for (int i = 0; i < length; i++) {
				String[] word = results[i].split("	");
				medicine[i].name = word[0];
				ssi[i].code = word[1];
				medicine[i].unit_price = Float.parseFloat(word[4]);

				DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
				try {
					medicine[i].validityDate = format.parse(word[2]);
					medicine[i].expiryDate = format.parse(word[3]);
				} catch (ParseException ex) {
					ex.fillInStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readFilePrescription(String path) {

		try {
			int count = 0;
			int length = Files.readAllLines(Paths.get(path)).size();
			String[] results = new String[length];
			for (String line : Files.readAllLines(Paths.get(path))) {
				results[count++] = line;
			}
			String[] word = results[0].split("	");
			patient.socialsecurityinstitution = word[1];
			DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
			try {
				patient.date = format.parse(word[2]);
			} catch (ParseException ex) {
			}

			for (int i = 1; i < length; i++) {
				String[] word2 = results[i].split("	");
				patient.medicines[i - 1] = word2[0];
				patient.medicine_count[i - 1] = Integer.parseInt(word2[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void calculatePrice(String path, String path2) {

		try {
			int length = Files.readAllLines(Paths.get(path)).size();
			int length2 = Files.readAllLines(Paths.get(path2)).size();
			double total_price = 0;
			String[] compare_min_name = new String[length];
			double[] compare_min_unit_price = new double[length];
			int count = 0;

			for (int j = 0; j < length2 - 1; j++) {
				for (int i = 0; i < length; i++) {
					if (patient.medicines[j].equals(medicine[i].name)) {
						if (ssi[i].code.equals(patient.socialsecurityinstitution)) {
							if (patient.date.after(medicine[i].validityDate)
									&& patient.date.before(medicine[i].expiryDate)) {
								compare_min_name[count] = medicine[i].name;
								compare_min_unit_price[count] = medicine[i].unit_price;
								count++;
							}
						}
					}
				}
			}
			for (int i = 0; i < count; i++) {
				if (compare_min_name[i].equals(compare_min_name[i + 1])) {
					if (compare_min_unit_price[i] <= compare_min_unit_price[i + 1]) {
						compare_min_unit_price[i + 1] = compare_min_unit_price[i];
					} else {
						compare_min_unit_price[i] = compare_min_unit_price[i + 1];
					}
				}
			}
			for (int j = 0; j < length2 - 1; j++) {
				for (int i = 0; i < length; i++) {
					if (patient.medicines[j].equals(medicine[i].name)) {
						if (ssi[i].code.equals(patient.socialsecurityinstitution)) {
							if (patient.date.after(medicine[i].validityDate)
									&& patient.date.before(medicine[i].expiryDate)) {
								for (int k = 0; k < count; k++) {
									if (compare_min_name[k].equals(medicine[i].name))
										medicine[i].unit_price = compare_min_unit_price[k];
								}
								System.out.print(patient.medicines[j]);
								System.out.format("	%.2f", medicine[i].unit_price);
								System.out.print("	" + patient.medicine_count[j]);
								System.out.format("	%.2f", medicine[i].unit_price * patient.medicine_count[j]);
								System.out.println("");
								total_price = total_price + medicine[i].unit_price * patient.medicine_count[j];
								break;
							}
						}
					}
				}
			}
			System.out.format("Total	%.2f%n ", total_price);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
