package in.vijay.domain;

import in.vijay.service.ClassVisitorService;
import in.vijay.service.MethodVisitorService;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, ParseException {
		InputStream in = ClassLoader.getSystemResourceAsStream("CommentRestController.java");
		CompilationUnit cu = JavaParser.parse(in);
		RestObject classObject = new ClassVisitorService().getRestObject(cu);
		MethodVisitorService methodVisitorService = new MethodVisitorService();
		for(RestObject restObject : methodVisitorService.getRestObject(cu, classObject)) {
			System.out.println(restObject);
		}
    }
}
