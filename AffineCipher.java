import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * 
 * @author Marj
 *
 *         main method: - ask to encrypt or decrypt a message. - ask to generate
 *         keys or read keys from pre-existing text files. - requires a specific
 *         pantherID and a message.
 * 
 *         strToInt: - converts a string containing words into a string
 *         containing numbers. - this is so that we can perform
 *         arithmetic/modular operations on the characters.
 * 
 *         intToStr: - converts a string containing numbers into a string
 *         containing words. - this displays the message.
 * 
 *         GCD: - returns true if two of its input numbers are coprime. - this
 *         is used to generate a valid list of 'a' keys.
 * 
 *         a_inverse: - returns the modular inverse of an input number. - this
 *         is used to generate a pair of inverses for each valid 'a' key.
 * 
 *         KeyGenerator: - generates a list of valid key combinations, using
 *         Panther ID. - generates a list of 'a' keys by calling GCD for each
 *         integer under the given id. - generates a list of 'b' keys, which is
 *         just the range of integers from [0,ID) - generates a list of 'a
 *         inverse' keys by calling the a_inverse function for each a key. -
 *         concatenates valid a,b, and a inverse keys into strings - repeats
 *         this a certain number of times, defined by the 'limit' input -- e.g.
 *         if limit is 10, 10 key pairs are generated. - returns the result, and
 *         the main method will save it to a text file. -- the text file has the
 *         name "[PantherID].txt", the last 4 pantherID numbers.
 * 
 *         ReadKey: - Reads a generated list of keys from a text file.
 * 
 *         Encryption: - Encrypts a message, with an input key.
 * 
 *         Decryption: - Decrypts a message, with an input key.
 * 
 *         write: - Writes an input string into a text file.
 * 
 *         read: - performs a similar function to ReadKey, except it is for the
 *         encrypted message.
 */

public class AffineCipher {

	public static void main(String[] args) throws IOException {

		int choice;
		int L4PN;

		System.out.println("What would you like to do?\n1) Encrypt a Message\n2) Decrypt an Encrypted message");
		choice = Integer.parseInt(new Scanner(System.in).nextLine());

		if (choice == 1) {
			System.out.println("What would you like to do?\n1) Generate new keys\n2) Read keys from text file");
			choice = Integer.parseInt(new Scanner(System.in).nextLine());

			if (choice == 2) {
				System.out.println("Enter the last 4 PantherID digits that generated the key list.");
				L4PN = Integer.parseInt(new Scanner(System.in).nextLine());
				ArrayList<String> savedKeys = new AffineCipher().ReadKey(L4PN + ".txt");
				for (String s : savedKeys) {
					System.out.println(s);
				}

				System.out.println("\nFormat: a,b,a inverse.\n");
			} else {

				System.out.println("Enter your last 4 PantherID digits: ");
				L4PN = Integer.parseInt(new Scanner(System.in).nextLine());

				System.out.println("Generate how many keys? ");
				int limit = Integer.parseInt(new Scanner(System.in).nextLine());

				System.out.println("Generating keys...");
				String result = new AffineCipher().KeyGenerator(L4PN, limit);
				new AffineCipher().write(result, L4PN + ".txt");
				System.out.println(result + "\nFormat: a,b,a inverse.\nSaved to file: " + L4PN
						+ ".txt ([last 4 PantherID Digits].txt)\n");

			}

			System.out.println("Type your message: ");
			String input = new Scanner(System.in).nextLine();

			System.out.println("Enter a valid key combination.");
			String keyCombo = new Scanner(System.in).nextLine();

			String encryptedMessage = new AffineCipher().Encryption(input, keyCombo, L4PN);
			System.out.println("Encrypted Message: " + encryptedMessage + "\nWritten to Encrypted.txt");
		}

		else {

			System.out.println("Enter the correct last 4 PantherID Digits.");
			L4PN = Integer.parseInt(new Scanner(System.in).nextLine());

			System.out.println("Brute Force using generated keys?\n1)Yes\n2)No");
			choice = Integer.parseInt(new Scanner(System.in).nextLine());

			String keyCombo;
			String encryptedMessage = new AffineCipher().read();
			if (choice == 1) {
				try {
					ArrayList<String> allKeys = new AffineCipher().ReadKey(L4PN+".txt");
					for(String s : allKeys) {
						String decryptedMessage = "";
						decryptedMessage = new AffineCipher().Decryption(encryptedMessage, s, L4PN);
						System.out.println("Decrypted Message: " + decryptedMessage);
					}
				}
				catch(Exception ex) {
					
				}
			} else {
				System.out.println("Enter the correct key.");
				keyCombo = new Scanner(System.in).nextLine();

				String decryptedMessage = "";
				decryptedMessage = new AffineCipher().Decryption(encryptedMessage, keyCombo, L4PN);
				System.out.println("Decrypted Message: " + decryptedMessage);
			}

		}
	}

	public String strToInt(String str) {
		char[] c = str.toCharArray();
		String result = "";
		for (int i = 0; i < c.length; i++) {
			int temp = c[i];
			result += temp + " ";
		}
		return result;
	}

	public String IntToStr(String Int) {
		String[] str = Int.split(" ");
		char[] result = new char[str.length];
		for (int i = 0; i < str.length; i++) {
			result[i] = (char) Integer.parseInt(str[i]);
		}
		return String.valueOf(result);
	}

	public boolean GCD(int input1, int input2) {
		while (input1 != 0 && input2 != 0) {
			if (input1 >= input2) {
				input1 = input1 - input2;
			} else {
				input2 = input2 - input1;
			}
		}
		if (input1 == 1 && input2 == 0) {
			return true;
		} else if (input2 == 1 && input1 == 0) {
			return true;
		} else
			return false;
	}

	public int a_inverse(int a, int modulus) {
		for (int i = 0; i < modulus; i++) {
			if (i * a % modulus == 1)
				return i;
		}
		return 0;
	}

	public String KeyGenerator(int L4PN, int limit) {

		ArrayList<Integer> a_list = new ArrayList<Integer>();

		for (int a = 1; a < L4PN; a++) {
			if (this.GCD(a, L4PN)) {
				a_list.add(a);
			}
		}

		ArrayList<Integer> b_list = new ArrayList<Integer>();

		for (int b = 0; b < L4PN; b++) {
			b_list.add(b);
		}

		ArrayList<String> temp = new ArrayList<String>();

		for (int i = 0; i < limit; i++) {
			
			int randomPosition = (int)(Math.random()*a_list.size());
			
			temp.add("" + a_list.get(randomPosition) + " " + (int) (Math.random() * b_list.size()) + " "
					+ this.a_inverse(a_list.get(randomPosition), L4PN));
		}

		String output = "";
		
		for (int i = 0; i < temp.size() - 1; i++) {
			output += temp.get((int) (Math.random() * temp.size())) + "\n";
		}
		output += temp.get(temp.size() - 1);

		return output;
	}

	public ArrayList<String> ReadKey(String pathname) throws IOException {
		File filename = new File(pathname);
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
		BufferedReader br = new BufferedReader(reader);
		String line = "";
		ArrayList<String> temp = new ArrayList<String>();
		while (line != null) {
			line = br.readLine();
			if (line != null) {
				temp.add(line);
			}
		}

		return temp;
	}

	public String Encryption(String content, String key, int L4PN) {
		String result = "";

		String contentIntegerFormat = this.strToInt(content);

		ArrayList<Integer> contentAsIntegers = new ArrayList<Integer>();
		StringTokenizer str = new StringTokenizer(contentIntegerFormat);
		while (str.hasMoreTokens()) {
			contentAsIntegers.add(Integer.parseInt(str.nextToken()));
		}

		str = new StringTokenizer(key);
		int a = Integer.parseInt(str.nextToken());
		int b = Integer.parseInt(str.nextToken());
		int a_inverse = Integer.parseInt(str.nextToken());

		for (int i = 0; i < contentAsIntegers.size(); i++) {
			contentAsIntegers.set(i, (contentAsIntegers.get(i) * a) + (b % L4PN));
		}

		for (int i : contentAsIntegers) {
			result += i + " ";
		}

		this.write(result, "Encrypted.txt");

		result = this.IntToStr(result);

		return result;
	}

	public String Decryption(String ciphertext, String key, int L4PN) {
		String result = "";

		ArrayList<Integer> contentAsIntegers = new ArrayList<Integer>();
		StringTokenizer str = new StringTokenizer(ciphertext);
		while (str.hasMoreTokens()) {
			contentAsIntegers.add(Integer.parseInt(str.nextToken()));
		}

		str = new StringTokenizer(key);
		int a = Integer.parseInt(str.nextToken());
		int b = Integer.parseInt(str.nextToken());
		int a_inverse = Integer.parseInt(str.nextToken());

		for (int i = 0; i < contentAsIntegers.size(); i++) {
			contentAsIntegers.set(i, (a_inverse * (contentAsIntegers.get(i) - b)) % L4PN);
		}

		for (int i : contentAsIntegers) {
			result += i + " ";
		}

		result = this.IntToStr(result);

		return result;
	}

	public AffineCipher() {

	}

	public void write(String data, String name) {

		String output = "";

		if (name.equals("0")) {
			output = "Keys.txt";
		} else {
			output = name;
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			writer.write(data);
			writer.close();
		} catch (IOException ex) {

		}
	}

	public String read() {

		String result = "";

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("Encrypted.txt")));
			result = br.readLine();
			br.close();
		} catch (FileNotFoundException ex) {

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}