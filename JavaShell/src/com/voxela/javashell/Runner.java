package com.voxela.javashell;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;

import com.sun.tools.javac.Main;
import com.voxela.javashell.utils.FileScanner;

/**
 * Created by Ladinn
 *
 * @on 5/26/2017
 */
public class Runner {
	
	public static void run(String s) throws Exception {
		
		File dataFolder = JavaShell.getInstance().getDataFolder();
		File runtimeFolder = new File(dataFolder + File.separator + "runtime");
		File javaFile = new File(runtimeFolder + File.separator + "run.java");
			
		PrintWriter pw = new PrintWriter(javaFile);
		pw.println("import org.bukkit.*;");
		pw.println("public class run {");
		pw.print("public static void main(){" + s + "}}");
		pw.close();
		
		String[] args = new String[] {
				"-cp", "." + File.pathSeparator + FileScanner.jarScan(),
				"-d", runtimeFolder.getAbsolutePath() + File.separator,
				javaFile.getAbsolutePath()
		};
		
		int compileStatus = Main.compile(args);
		
		if (compileStatus != 1) {
			URL[] urls = new URL[] { runtimeFolder.toURI().toURL() };
			URLClassLoader ucl = new URLClassLoader(urls);
			Object o = ucl.loadClass("run").newInstance();
			o.getClass().getMethod("main").invoke(o);
			ucl.close();
		} else {
			throw new Exception("JavaShell compilation failure");			
		}
		
		javaFile.delete();
		File classFile = new File(runtimeFolder + File.separator + "run.class");
		classFile.delete();
	}

}