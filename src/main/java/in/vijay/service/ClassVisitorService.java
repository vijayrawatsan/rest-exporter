package in.vijay.service;

import in.vijay.domain.RequestMappingMember;
import in.vijay.domain.RestObject;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.List;

public class ClassVisitorService {

	private static final String REQUEST_MAPPING = "RequestMapping";

	public RestObject getRestObject(CompilationUnit compilationUnit) {
		RestObject restObject = RestObject.getRestObject();
		new ClassVisitor().visit(compilationUnit, restObject);
		return restObject;
	}
	
	private static class ClassVisitor extends VoidVisitorAdapter<RestObject> {
		@Override
		public void visit(ClassOrInterfaceDeclaration n, RestObject restObject) {
			
			List<AnnotationExpr> annotations = n.getAnnotations();
			if (annotations != null && annotations.size() > 0) {
				// We have some annotation on this class
				for (AnnotationExpr annotation : annotations) {
					if (annotation.getName().toString().equals(REQUEST_MAPPING)) {
						if(annotation.getClass().equals(SingleMemberAnnotationExpr.class)) {
							SingleMemberAnnotationExpr annotationExpr = (SingleMemberAnnotationExpr) annotation;
							restObject.setMapping(annotationExpr.getMemberValue().toString().replace("\"", ""));
						}
						else if(annotation.getClass().equals(NormalAnnotationExpr.class)) {
							NormalAnnotationExpr annotationExpr = (NormalAnnotationExpr) annotation;
							for(MemberValuePair nameValue : annotationExpr.getPairs()) {
								for(RequestMappingMember requestMappingMember : RequestMappingMember.values()) {
									if(requestMappingMember.getName().equals(nameValue.getName())) {
										requestMappingMember.updateRestObject(nameValue.getValue().toString(), restObject);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}  
