package com.voxela.javashell;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Created by Ladinn & Zhanger
 *
 * @on 52/26/2017
 */
public class Runner {

	public static void runCode(String s) throws Exception {

		JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager sjfm = jc.getStandardFileManager(null, null, null);
		File jf = new File("run.java"); // create file in current working
										// directory
		PrintWriter pw = new PrintWriter(jf);
		pw.println("import org.bukkit.*;");
		pw.println("public class run {");
		pw.print("public static void main(){" + s + "}}");
		pw.close();
		Iterable<? extends JavaFileObject> fO = sjfm.getJavaFileObjects(jf);
		if (!jc.getTask(null, sjfm, null, null, null, fO).call()) {
			throw new Exception("Compilation Failure!");
		}
		URL[] urls = new URL[] { new File("").toURI().toURL() };
		URLClassLoader ucl = new URLClassLoader(urls);
		Object o = ucl.loadClass("run").newInstance();
		o.getClass().getMethod("main").invoke(o);
		ucl.close();
	}

}