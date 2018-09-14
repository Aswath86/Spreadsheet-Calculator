import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedHashMap;
import java.util.Stack;

public class Spreadsheet {

	// Collection for Storing the Cell data
	LinkedHashMap<String, String> hMap = new LinkedHashMap<>();

	// Record the list to avoid re-calculation
	ArrayList<String> completedList = new ArrayList<String>();

	// Record the visited list
	ArrayList<String> visitedList = new ArrayList<String>();

	String header = null;

	public static void main(String[] args) {
		Spreadsheet spreadsheet = new Spreadsheet();
		try {
			Boolean successFlag = spreadsheet.readInput();
			if (successFlag) {
				spreadsheet.printResults();
			} else {
				spreadsheet.printError();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**********
	 * Method to read the input text file and store in the Collection object.
	 **********/
	public Boolean readInput() throws Exception {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));

		// BufferedReader reader = new BufferedReader(new InputStreamReader(
		// new
		// FileInputStream("C:\\spreadsheet.txt")));

		header = reader.readLine();

		String[] headerArry = header.split(" ");

		String[] alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

		for (int j = 0; j < Integer.parseInt(headerArry[1]); j++) {
			for (int i = 1; i < Integer.parseInt(headerArry[0]) + 1; i++) {
				hMap.put(alphabets[j] + i, reader.readLine().trim());
			}
		}

		for (String value : hMap.keySet()) {
			if (deadlockDetector()) {
				return false;
			} else {
				typeFinder(value);
			}
		}
		return true;
	}

	/**********
	 * Method to Print the Final Result
	 **********/
	public void printResults() throws Exception {
		System.out.println(header);
		for (Object value : hMap.values()) {
			System.out.println(String.format("%.5f", (Double.parseDouble((String) value))));
		}
		// System.out.println("**********The End**********");
	}

	/**********
	 * Method to Print the Error Message for dead-lock situation
	 **********/
	public void printError() {

		System.out.println(
				"Possibility of a dead-lock situation. Please check the input file for infinite reference looping. Exiting the sequence.");
	}

	/**********
	 * Method to look for possible dead-lock situation (infinite reference
	 * looping). Dead-lock condition to warn and exit the sequence
	 **********/
	private Boolean deadlockDetector() throws Exception {
		if (visitedList.size() > hMap.size() * 2) {
			return true;
		}
		return false;
	}

	/**********
	 * Method to find the type of the incoming data The series of if-else
	 * condition determines the sort of operation that has to be done
	 * 
	 * @throws Exception
	 **********/
	private void typeFinder(String hMapKey) throws Exception {
		visitedList.add(hMapKey);
		try {
			String cellValue = hMap.get(hMapKey);
			if (!isCompleted(hMapKey)) {
				if (isSingleLenght(cellValue)) {
					if (isAlphabet(cellValue)) {
						if (!completedList.toString().contains(hMapKey)) {
							typeFinder(cellValue);
							hMap.put(hMapKey, hMap.get(cellValue));
							completedList.add(hMapKey + " ");
						}
					} else {
						Double d = Double.parseDouble(cellValue);
						hMap.put(hMapKey, d.toString());
						completedList.add(hMapKey + " ");
					}
				} else {
					if (isAlphabet(cellValue)) {
						String[] checkLengthAry = cellValue.split("\\s+");
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < checkLengthAry.length; i++) {
							if (isCompleted(checkLengthAry[i]) && isAlphabet(checkLengthAry[i])) {
								sb.append(hMap.get(checkLengthAry[i]) + " ");
							} else if (isAlphabet(checkLengthAry[i])) {
								typeFinder(checkLengthAry[i]);
								// completedList.add(checkLengthAry[i] + " ");
								sb.append(hMap.get(checkLengthAry[i]) + " ");
							} else {
								sb.append(checkLengthAry[i] + " ");
							}
						}
						hMap.put(hMapKey, calculate(hMapKey, sb.toString().trim()));
						completedList.add(hMapKey + " ");

					} else {
						hMap.put(hMapKey, calculate(hMapKey, cellValue));
						completedList.add(hMapKey + " ");
					}
				}
			}
		} catch (StackOverflowError e) {
			// e.printStackTrace();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**********
	 * Method to check if calculation is already done
	 **********/
	private boolean isCompleted(String checkCompletion) throws Exception {
		if (completedList.contains(checkCompletion)) {
			return true;
		}
		return false;
	}

	/**********
	 * Method to check if the data is yet to be calculated
	 **********/
	private Boolean isAlphabet(String checkAlphabet) throws Exception {
		if (checkAlphabet.matches(".*[A-Z].*")) {
			return true;
		}
		return false;
	}

	/**********
	 * Method to check if the data has one for more operands
	 **********/
	private Boolean isSingleLenght(String checkLenght) {
		try {
			String[] checkLengthAry = checkLenght.split("\\s+");
			if (checkLengthAry.length == 1) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**********
	 * Method to do the Math operation for the RPN data. The method uses switch
	 * case with Arithmetic operator precedence order
	 **********/
	public String calculate(String hMapKey, String cellValue) throws ArithmeticException, EmptyStackException {
		Stack<Double> stack = new Stack<>();
		for (String literalValue : cellValue.split("\\s+")) {
			switch (literalValue) {
			case "++":
				double incrementalD = stack.pop();
				stack.push(++incrementalD);
				break;
			case "--":
				double decerementalD = stack.pop();
				stack.push(--decerementalD);
				break;
			case "*":
				stack.push(stack.pop() * stack.pop());
				break;
			case "/":
				double divisor = stack.pop();
				stack.push(stack.pop() / divisor);
				break;
			case "+":
				stack.push(stack.pop() + stack.pop());
				break;
			case "-":
				stack.push(-stack.pop() + stack.pop());
				break;
			default:
				stack.push(Double.parseDouble(literalValue));
				break;
			}
		}
		return stack.pop().toString();
	}

}
