package edu.uiowa.csense.analyzer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceSignatureVisitor;
import org.objectweb.asm.signature.*;


public class Analyzer {
	public static class FieldSignature extends SignatureVisitor {
		public FieldSignature() {
			super(Opcodes.ASM4);
		}

		@Override
		public SignatureVisitor visitArrayType() {
			// TODO Auto-generated method stub
			return super.visitArrayType();
		}

		@Override
		public void visitBaseType(char arg0) {
			// TODO Auto-generated method stub
			System.out.println("\tbaseType: " + arg0);
			super.visitBaseType(arg0);
		}

		@Override
		public SignatureVisitor visitClassBound() {
			// TODO Auto-generated method stub
			return super.visitClassBound();
		}

		@Override
		public void visitClassType(String arg0) {
			System.out.println("\tclassType: " + arg0);
			// TODO Auto-generated method stub
			super.visitClassType(arg0);
		}

		@Override
		public void visitEnd() {
			super.visitEnd();
		}

		@Override
		public SignatureVisitor visitExceptionType() {
			// TODO Auto-generated method stub
			return super.visitExceptionType();
		}

		@Override
		public void visitFormalTypeParameter(String arg0) {
			System.out.println("\tformal: " + arg0);
			// TODO Auto-generated method stub
			super.visitFormalTypeParameter(arg0);
		}

		@Override
		public void visitInnerClassType(String arg0) {
			System.out.println("classType: " + arg0);
			// TODO Auto-generated method stub
			super.visitInnerClassType(arg0);
		}

		@Override
		public SignatureVisitor visitInterface() {
			// TODO Auto-generated method stub
			return super.visitInterface();
		}

		@Override
		public SignatureVisitor visitInterfaceBound() {
			// TODO Auto-generated method stub
			return super.visitInterfaceBound();
		}

		@Override
		public SignatureVisitor visitParameterType() {
			// TODO Auto-generated method stub
			return super.visitParameterType();
		}

		@Override
		public SignatureVisitor visitReturnType() {			
			// TODO Auto-generated method stub
			return super.visitReturnType();
		}

		@Override
		public SignatureVisitor visitSuperclass() {
			// TODO Auto-generated method stub
			return super.visitSuperclass();
		}

		@Override
		public void visitTypeArgument() {			
			System.out.println("typeArgument<void>");
			// TODO Auto-generated method stub
			super.visitTypeArgument();
		}

		@Override
		public SignatureVisitor visitTypeArgument(char arg0) {			
			// TODO Auto-generated method stub
			SignatureVisitor v = super.visitTypeArgument(arg0);
			System.out.println("typeArgument: " + arg0 + " " + v);
			return v;
			
		}

		@Override
		public void visitTypeVariable(String arg0) {
			System.out.println("typeVariable: " + arg0);
			// TODO Auto-generated method stub
			super.visitTypeVariable(arg0);
		}						
	}
	
	public static void analyzeDoInput(MethodNode m) {
		
	}
	
	public static void analyzeFields(List<FieldNode> fields) {
		System.out.println("====== Fields ======");
		for (FieldNode field : fields) {
			System.out.println("\tname: " + field.name);
			System.out.println("\tsignature: " + field.signature);
			SignatureReader sr = new SignatureReader(field.signature);
			FieldSignature tv = new FieldSignature();
			sr.accept(tv);
						
			System.out.println("\taccess: " + field.access);
			System.out.println("\tattrs: " + field.attrs);
			System.out.println("\tdecl: " + field.desc);
		}
	}
	
	public static void analyzeComponent(File componentClass) throws IOException, AnalyzerException {
		ClassReader reader = new ClassReader(new FileInputStream(componentClass));
		ClassNode cn = new ClassNode();
		reader.accept(cn, ClassReader.EXPAND_FRAMES);
		
		
		
		if ("api/CSenseComponent".equals(cn.superName) == false) {
			throw new AnalyzerException(AnalyzerException.NOT_A_CSENSE_COMPONENT);			
		}
		
		System.out.println("className: " + cn.name);
		System.out.println("super: " + cn.superName);
		
		List<FieldNode> fields = cn.fields;
		analyzeFields(fields);
		
		
		List<MethodNode> methods = cn.methods;
		for (MethodNode method : methods) {
			System.out.println("name=" +method.name);
			if ("doInput".equals(method.name)) {
				analyzeDoInput(method);
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException, AnalyzerException {
		File tapComponent = new File("/Users/ochipara/Working/CSense/svn/trunk/csense/sdk/java/Base/bin/components/basic/TapComponent.class");
		analyzeComponent(tapComponent);
	}
}
