package utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Logger {

	private static BufferedWriter writer;
	
	/**
	 * set up the logger
	 * @param name filename to log to, without .log extension
	 */
	public static void log(String name) { try { writer = new BufferedWriter(new FileWriter(name+".log")); } catch (Exception e) { System.exit(-1); } }
	
	/**
	 * log replacement for System.out.print(String)
	 * @param string the string to write
	 */
	public static void write(String string) { try { writer.write(string); writer.flush(); } catch (Exception e) {}	}

	/**
	 * log replacement for System.out.println()
	 */
	public static void writeln() { try { writer.write("\r\n"); writer.flush(); } catch (Exception e) {} }

	/**
	 * log replacement for System.out.println(String)
	 * @param string the string to write as line
	 */
	public static void writeln(String string) { try { writer.write(string+"\r\n"); writer.flush(); } catch (Exception e) {} }
	
}
