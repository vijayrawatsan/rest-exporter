package in.vijay.service;

import in.vijay.domain.RequestMappingMember;
import in.vijay.domain.RestObject;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.List;

public class MethodVisitorService {

	private static final String REQUEST_MAPPING = "RequestMapping";
	private static final String PATH_VARIABLE = "PathVariable";
	private static final String REQUEST_PARAMETER = "RequestParameter";

	public List<RestObject> getRestObject(CompilationUnit compilationUnit, RestObject classLevelRestObject) {
		MethodVisitor methodVisitor = new MethodVisitor();
		methodVisitor.visit(compilationUnit, classLevelRestObject);
		return methodVisitor.getRestObjects();
	}
	
	private static class MethodVisitor extends VoidVisitorAdapter<RestObject> {
		
		private List<RestObject> restObjects = new ArrayList<RestObject>();
		
		public List<RestObject> getRestObjects() {
			return restObjects;
		}

		@Override
		public void visit(MethodDeclaration n, RestObject classObject) {
			boolean checkForParameters = false;
			RestObject restObject = null;
			List<AnnotationExpr> annotations = n.getAnnotations();
			if (annotations != null && annotations.size() > 0) {
				// We have some annotation on this class
				for (AnnotationExpr annotation : annotations) {
					if (annotation.getName().toString().equals(REQUEST_MAPPING)) {
						checkForParameters = true;
						restObject = RestObject.copyOf(classObject);
						addAnnotationData(restObject, annotation);
						addParameterData(n, checkForParameters, restObject);
						restObjects.add(restObject);
					}
				}
			}
			
		}

		private void addAnnotationData(RestObject restObject,
				AnnotationExpr annotation) {
			if(annotation.getClass().equals(SingleMemberAnnotationExpr.class)) {
				SingleMemberAnnotationExpr annotationExpr = (SingleMemberAnnotationExpr) annotation;
				restObject.setMapping(restObject.getMapping() + annotationExpr.getMemberValue().toString().replace("\"", ""));
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

		private void addParameterData(MethodDeclaration n,
				boolean checkForParameters, RestObject restObject) {
			if (checkForParameters) {
				List<Parameter> parameters = n.getParameters();
				List<String> pathVariables = new ArrayList<String>();
				List<String> requestParameters = new ArrayList<String>();
				
				if(parameters!=null && parameters.size()>0) {
					for(Parameter parameter: parameters) {
						List<AnnotationExpr> parameterAnnotations = parameter.getAnnotations();
						if (parameterAnnotations != null && parameterAnnotations.size() > 0) {
							// We have some annotation on this class
							for (AnnotationExpr paramAnnotation : parameterAnnotations) {
								if (paramAnnotation.getName().toString().equals(PATH_VARIABLE)) {
									pathVariables.add(parameter.getType().toString()+" "+parameter.getId().toString());
								} else if (paramAnnotation.getName().toString().equals(REQUEST_PARAMETER)) {
									requestParameters.add(parameter.getType().toString()+" "+parameter.getId().toString());
								}
							}
						}
					}
				}
				restObject.setPathVariables(pathVariables);
				restObject.setRequestParameters(requestParameters);
			}
		}    
	}
}
