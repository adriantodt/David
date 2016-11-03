/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 13:12]
 */

package cf.adriantodt.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.ReflectionUtil;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

public class Log4jUtils {
	public static void hackStdout() {
		System.setOut(new LoggerStream(System.out, Level.INFO));
		System.setErr(new LoggerStream(System.err, Level.ERROR));

		System.out.println("Redirected!");
	}

	public static class LoggerStream extends PrintStream {
		private final Level logLevel;

		public LoggerStream(PrintStream stream, Level logLevel) {
			super(stream);
			this.logLevel = logLevel;
		}

		@Override
		public void write(int b) {
			log(String.valueOf((char) b));
		}

		@Override
		public void write(byte[] b) throws IOException {
			String string = new String(b);
			if (!string.trim().isEmpty())
				log(string);
		}

		@Override
		public void write(byte[] buf, int off, int len) {
			String string = new String(buf, off, len);
			if (!string.trim().isEmpty())
				log(string);
		}

		@Override
		public void print(boolean b) {
			log(String.valueOf(b));
		}

		@Override
		public void print(char c) {
			log(String.valueOf(c));
		}

		@Override
		public void print(int i) {
			log(String.valueOf(i));
		}

		@Override
		public void print(long l) {
			log(String.valueOf(l));
		}

		@Override
		public void print(float f) {
			log(String.valueOf(f));
		}

		@Override
		public void print(double d) {
			log(String.valueOf(d));
		}

		@Override
		public void print(char[] s) {
			log(String.valueOf(s));
		}

		public void print(String s) {
			log(String.valueOf(s));
		}

		@Override
		public void print(Object obj) {
			log(String.valueOf(obj));
		}

		@Override
		public void println() {
			log("");
		}

		@Override
		public void println(boolean x) {
			log(String.valueOf(x));
		}

		@Override
		public void println(char x) {
			log(String.valueOf(x));
		}

		@Override
		public void println(int x) {
			log(String.valueOf(x));
		}

		@Override
		public void println(long x) {
			log(String.valueOf(x));
		}

		@Override
		public void println(float x) {
			log(String.valueOf(x));
		}

		@Override
		public void println(double x) {
			log(String.valueOf(x));
		}

		@Override
		public void println(char[] x) {
			log(new String(x));
		}

		@Override
		public void println(String x) {
			log(String.valueOf(x));
		}

		@Override
		public void println(Object x) {
			log(String.valueOf(x));
		}

		@Override
		public PrintStream printf(String format, Object... args) {
			log(String.format(format, args));
			return this;
		}

		@Override
		public PrintStream printf(Locale l, String format, Object... args) {
			log(String.format(l, format, args));
			return this;
		}

		@Override
		public PrintStream format(String format, Object... args) {
			log(String.format(format, args));
			return this;
		}

		@Override
		public PrintStream format(Locale l, String format, Object... args) {
			log(String.format(l, format, args));
			return this;
		}

		@Override
		public PrintStream append(CharSequence csq) {
			log(csq.toString());
			return this;
		}

		@Override
		public PrintStream append(CharSequence csq, int start, int end) {
			log(csq.subSequence(start, end).toString());
			return this;
		}

		@Override
		public PrintStream append(char c) {
			log(String.valueOf(c));
			return this;
		}

		public void log(String in) {
			Class c = ReflectionUtil.getCallerClass(3);
			LogManager.getLogger(c == null ? "Unknown" : c.getSimpleName()).log(logLevel, in);
		}
	}

	public static Logger logger() {
		Class c = ReflectionUtil.getCallerClass(2);
		return LogManager.getLogger(c == null ? "Unknown" : c.getSimpleName());
	}
}