package cop5556sp17;

import java.awt.Window.Type;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}
	
	class ChainObject {
		Kind arrowOperator;
		boolean left;
		public ChainObject(boolean left, Kind arrowOperator) {
			this.left = left;
			this.arrowOperator = arrowOperator;
		}
		public Kind getArrowOperator() {
			return arrowOperator;
		}
		public void setArrowOperator(Kind arrowOperator) {
			this.arrowOperator = arrowOperator;
		}
		public boolean getLeft() {
			return left;
		}
		public void setLeft(boolean left) {
			this.left = left;
		}
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	int argCount=0;
	int slotNumber=1;

	MethodVisitor mv; // visitor of method currently under construction
	
	int index=0;

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null,  startRun, endRun, 1);
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getIdentType());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		ChainObject ch=new ChainObject(true, binaryChain.getArrow().kind);
		binaryChain.getE0().visit(this,ch);
		if(binaryChain.getE0().getIdentType().equals(TypeName.URL)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
		}
		else if(binaryChain.getE0().getIdentType().equals(TypeName.FILE)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
		}
		
		ch=new ChainObject(false, binaryChain.getArrow().kind);
		binaryChain.getE1().visit(this,ch);
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		TypeName t1=binaryExpression.getE0().getIdentType();
		TypeName t2=binaryExpression.getE1().getIdentType();
		if(binaryExpression.getE0() instanceof IntLitExpression && binaryExpression.getE1() instanceof IdentExpression) {
			binaryExpression.getE1().visit(this, arg);
			binaryExpression.getE0().visit(this, arg);
		}
		else {
			binaryExpression.getE0().visit(this, arg);
			binaryExpression.getE1().visit(this, arg);
		}
		Kind op=binaryExpression.getOp().kind;
		Label startLabel=new Label();
		Label endLabel=new Label();
		if(op.equals(Kind.PLUS)){
			if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.IMAGE)){
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
			}
			else{
				mv.visitInsn(IADD);
			}	
		}
		else if(op.equals(Kind.MINUS)){
			if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.IMAGE)){
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
			}
			else{
				mv.visitInsn(ISUB);
			}
		}
		else if(op.equals(Kind.DIV)){
			if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.INTEGER)){
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
			}
			else{
				mv.visitInsn(IDIV);
			}
		}
		else if(op.equals(Kind.TIMES)){
			if((t1.equals(TypeName.INTEGER) && t2.equals(TypeName.IMAGE)) || (t1.equals(TypeName.IMAGE) && t2.equals(TypeName.INTEGER))){
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			}
			else{
				mv.visitInsn(IMUL);
			}
		}
		else if(op.equals(Kind.MOD)){
			if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.INTEGER)){
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
			}
			else{
				mv.visitInsn(IREM);
			}
		}
		else if(op.equals(Kind.OR)){
			mv.visitInsn(IOR);
		}
		else if(op.equals(Kind.AND)){
			mv.visitInsn(IAND);
		}
		else if(op.equals(Kind.EQUAL)){
			if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.IMAGE) || t1.equals(TypeName.URL) && t2.equals(TypeName.URL)){
				mv.visitJumpInsn(IF_ACMPEQ, startLabel);
			}
			else{
				mv.visitJumpInsn(IF_ICMPEQ, startLabel);
			}
			mv.visitLdcInsn(false);
		}
		else if(op.equals(Kind.NOTEQUAL)){
			if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.IMAGE) || t1.equals(TypeName.URL) && t2.equals(TypeName.URL)){
				mv.visitJumpInsn(IF_ACMPNE, startLabel);
			}
			else{
				mv.visitJumpInsn(IF_ICMPNE, startLabel);
			}
			mv.visitLdcInsn(false);
		}
		else if(op.equals(Kind.LE)){
			mv.visitJumpInsn(IF_ICMPLE, startLabel);
			mv.visitLdcInsn(false);
		}
		else if(op.equals(Kind.GE)){
			mv.visitJumpInsn(IF_ICMPGE, startLabel);
			mv.visitLdcInsn(false);
		}
		else if(op.equals(Kind.GT)){
			mv.visitJumpInsn(IF_ICMPGT, startLabel);
			mv.visitLdcInsn(false);
		}
		else if(op.equals(Kind.LT)){
			mv.visitJumpInsn(IF_ICMPLT, startLabel);
			mv.visitLdcInsn(false);
		}
		mv.visitJumpInsn(GOTO, endLabel);
		mv.visitLabel(startLabel);
		mv.visitLdcInsn(true);
		mv.visitLabel(endLabel);		
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		Label startBlock = new Label();
		mv.visitLabel(startBlock);
		
		List<Statement> statList=new ArrayList<Statement>();
		List<Dec> decList=new ArrayList<Dec>();
		decList=block.getDecs();
		statList=block.getStatements();
		for(Dec item : decList){
			item.visit(this, arg);
		}
		for(Statement st : statList){
			if(st instanceof AssignmentStatement){
				if(((AssignmentStatement) st).getVar().getDec() instanceof ParamDec){
					mv.visitVarInsn(ALOAD, 0);
				}
			}
			st.visit(this, arg);
			if(st instanceof Chain){
				mv.visitInsn(POP);
			}
		}
		Label endBlock = new Label();
		mv.visitLabel(endBlock);
		for(Dec dec : decList){
			mv.visitLocalVariable(dec.getIdent().getText(), dec.getIdentType().getJVMTypeDesc(), null, startBlock, endBlock, dec.getSlot());
		}
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(booleanLitExpression.getValue());
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		if(constantExpression.firstToken.isKind(KW_SCREENHEIGHT)){
			mv.visitMethodInsn(INVOKESTATIC,PLPRuntimeFrame.JVMClassName ,"getScreenHeight", "()I", false);
		}
		else if(constantExpression.firstToken.isKind(Kind.KW_SCREENWIDTH)){
			mv.visitMethodInsn(INVOKESTATIC,PLPRuntimeFrame.JVMClassName ,"getScreenWidth", "()I", false);
		}
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		declaration.setSlot(slotNumber++);
		if(declaration.getIdentType().equals(TypeName.FRAME)){
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, declaration.getSlot());
		}
		
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		if(((ChainObject) arg).getArrowOperator().equals(Kind.ARROW)){
			mv.visitInsn(ACONST_NULL);
		}
		else {
			mv.visitInsn(DUP);
			mv.visitInsn(SWAP);
		}
		if(filterOpChain.firstToken.isKind(OP_BLUR)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);
		}
		else if(filterOpChain.firstToken.isKind(OP_CONVOLVE)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig, false);
		}
		else if(filterOpChain.firstToken.isKind(OP_GRAY)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		frameOpChain.getArg().visit(this, arg);
		if(frameOpChain.firstToken.isKind(KW_HIDE)){
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc, false);
		}
		else if(frameOpChain.firstToken.isKind(KW_SHOW)){
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc, false);
		}
		else if(frameOpChain.firstToken.isKind(KW_MOVE)){
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc, false);
		}
		else if(frameOpChain.firstToken.isKind(Kind.KW_XLOC)){
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc, false);
		}
		else if(frameOpChain.firstToken.isKind(KW_YLOC)){
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc, false);
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		Dec dec=identChain.getDec();
		if(((ChainObject) arg).getLeft()){
			if(dec instanceof ParamDec){
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), identChain.getIdentType().getJVMTypeDesc());
			}
			else{
				if(identChain.getDec().getIdentType().equals(TypeName.INTEGER)){
					mv.visitVarInsn(ILOAD, ((Dec) dec).getSlot());
				}
				else{
					mv.visitVarInsn(ALOAD, ((Dec) dec).getSlot());
				}
			}
		}
		else{
			if(identChain.getDec().getIdentType().equals(TypeName.INTEGER) || identChain.getDec().getIdentType().equals(TypeName.BOOLEAN) ){
				mv.visitInsn(DUP);
				if(identChain.getDec() instanceof ParamDec) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(PUTFIELD, className, identChain.getFirstToken().getText(), identChain.getIdentType().getJVMTypeDesc());
				}
				else{
					mv.visitVarInsn(ISTORE, identChain.getDec().getSlot());
				}
			}
			else if(identChain.getDec().getIdentType().equals(TypeName.IMAGE)){
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, ((Dec) dec).getSlot());
			}
			else if(identChain.getDec().getIdentType().equals(TypeName.FILE)){
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), identChain.getDec().getIdentType().getJVMTypeDesc());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
			}
			else if(identChain.getDec().getIdentType().equals(TypeName.FRAME)){
		//		mv.visitInsn(ACONST_NULL);
				mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, identChain.getDec().getSlot());
			}
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		Dec dec=identExpression.getDec();
		if(dec instanceof ParamDec){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, identExpression.getFirstToken().getText(), identExpression.getIdentType().getJVMTypeDesc());
		}
		else{
			if(dec.getIdentType().equals(TypeName.INTEGER) || dec.getIdentType().equals(TypeName.BOOLEAN)){
				mv.visitVarInsn(ILOAD, dec.getSlot());
			}
			else{
				mv.visitVarInsn(ALOAD, dec.getSlot());
			}
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		Dec dec=identX.getDec();
		if(dec instanceof ParamDec){
			mv.visitFieldInsn(PUTFIELD, className, identX.getFirstToken().getText(), dec.getIdentType().getJVMTypeDesc());
		}
		else{
			if(dec.getIdentType().equals(TypeName.INTEGER) || dec.getIdentType().equals(TypeName.BOOLEAN)){
				mv.visitVarInsn(ISTORE, dec.getSlot());
			}
			else if(dec.getIdentType().equals(TypeName.IMAGE)){
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
				mv.visitVarInsn(ASTORE, dec.getSlot());
			}
			else{
				mv.visitVarInsn(ASTORE, dec.getSlot());
			}
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		Label endIf=new Label();
		ifStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFEQ, endIf);
		ifStatement.getB().visit(this, arg);
		mv.visitLabel(endIf);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		if(imageOpChain.firstToken.isKind(Kind.OP_HEIGHT)){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getHeight", PLPRuntimeImageOps.getHeightSig, false);
		}
		else if(imageOpChain.firstToken.isKind(Kind.OP_WIDTH)){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getWidth", PLPRuntimeImageOps.getWidthSig, false);
		}
		else if(imageOpChain.firstToken.isKind(Kind.KW_SCALE)){
			imageOpChain.getArg().visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(intLitExpression.value);
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		FieldVisitor fv=cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), paramDec.getIdentType().getJVMTypeDesc(), null, null);
		fv.visitEnd();
		mv.visitVarInsn(ALOAD, 0); //this
		if(paramDec.getIdentType().equals(TypeName.INTEGER)){
			mv.visitVarInsn(ALOAD, 1); //args
			mv.visitLdcInsn(argCount++);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "I");	
		}
		else if(paramDec.getIdentType().equals(TypeName.BOOLEAN)){
			mv.visitVarInsn(ALOAD, 1); //args
			mv.visitLdcInsn(argCount++);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Z");
		}
		else if(paramDec.getIdentType().equals(TypeName.URL)){
			mv.visitVarInsn(ALOAD, 1); //args
			mv.visitLdcInsn(argCount++);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), PLPRuntimeImageIO.URLDesc);
		}
		else if(paramDec.getIdentType().equals(TypeName.FILE)){
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1); //args
			mv.visitLdcInsn(argCount++);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), PLPRuntimeImageIO.FileDesc);
		}
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		sleepStatement.getE().visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		List<Expression> expList=new ArrayList<Expression>();
		expList=tuple.getExprList();
		for(Expression exp : expList){
			exp.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		Label GUARD = new Label();
		Label BODY = new Label();
		mv.visitJumpInsn(GOTO, GUARD);
		mv.visitLabel(BODY);
		whileStatement.getB().visit(this, arg);
		mv.visitLabel(GUARD);
		whileStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFNE, BODY);
		return null;
	}

}