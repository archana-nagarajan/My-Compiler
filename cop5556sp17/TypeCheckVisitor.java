package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
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
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		Chain ch=binaryChain.getE0();
		ch.visit(this, arg);
		TypeName t1=binaryChain.getE0().getIdentType();
		ChainElem ce=binaryChain.getE1();
		ce.visit(this, arg);	
		TypeName t2=binaryChain.getE1().getIdentType();
		Token op=binaryChain.getArrow();
		if((t1.equals(TypeName.URL)|| t1.equals(TypeName.FILE)) && t2.equals(TypeName.IMAGE) && op.isKind(ARROW)){
			binaryChain.setIdentType(IMAGE);
		}
		else if(t1.equals(TypeName.FRAME) && op.isKind(ARROW) && ce instanceof FrameOpChain){
			if(binaryChain.getE1().firstToken.kind.equals(Kind.KW_XLOC) || binaryChain.getE1().firstToken.kind.equals(Kind.KW_YLOC)){
				binaryChain.setIdentType(INTEGER);
			}
			else if(binaryChain.getE1().firstToken.kind.equals(Kind.KW_SHOW) || binaryChain.getE1().firstToken.kind.equals(Kind.KW_HIDE) 
					|| binaryChain.getE1().firstToken.kind.equals(Kind.KW_MOVE)){
				binaryChain.setIdentType(FRAME);
			}
			else{
				throw new TypeCheckException("Illegal");
			}
		}
		else if(t1.equals(TypeName.IMAGE) && op.isKind(ARROW) && ce instanceof ImageOpChain){
			if(binaryChain.getE1().firstToken.kind.equals(Kind.KW_SCALE)){
				binaryChain.setIdentType(IMAGE);
			}
			else if(binaryChain.getE1().firstToken.kind.equals(Kind.OP_WIDTH) || binaryChain.getE1().firstToken.kind.equals(Kind.OP_HEIGHT)){
				binaryChain.setIdentType(INTEGER);
			}
			
			else{
				throw new TypeCheckException("Illegal");
			}
		}
		else if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.FRAME) && op.isKind(ARROW)){
			binaryChain.setIdentType(FRAME);
		}
		else if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.FILE) && op.isKind(ARROW)){
			binaryChain.setIdentType(NONE);
		}
		else if(t1.equals(TypeName.IMAGE) && (op.isKind(Kind.ARROW) || op.isKind(Kind.BARARROW)) && ce instanceof FilterOpChain){
			if(binaryChain.getE1().firstToken.kind.equals(Kind.OP_GRAY) || binaryChain.getE1().firstToken.kind.equals(Kind.OP_BLUR)
					|| binaryChain.getE1().firstToken.kind.equals(Kind.OP_CONVOLVE)){
				binaryChain.setIdentType(IMAGE);
			}
			else{
				throw new TypeCheckException("Illegal");
			}
		}
		else if(t1.equals(TypeName.IMAGE) && op.isKind(Kind.ARROW) && ce instanceof IdentChain && ce.getIdentType().equals(IMAGE)){
			binaryChain.setIdentType(IMAGE);
		}
		else if(t1.equals(TypeName.INTEGER) && op.isKind(Kind.ARROW) && ce instanceof IdentChain && ce.getIdentType().equals(INTEGER)){
			binaryChain.setIdentType(INTEGER);
		}
		else if(t1.equals(TypeName.IMAGE) && op.isKind(Kind.ARROW) && ce instanceof IdentChain && ce.getIdentType().equals(INTEGER)){
			binaryChain.setIdentType(INTEGER);
		}
		else{
			throw new TypeCheckException("Illegal");
		}		
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
			
		binaryExpression.getE0().visit(this, arg);
		TypeName t1=binaryExpression.getE0().getIdentType();
		binaryExpression.getE1().visit(this, arg);
		TypeName t2=binaryExpression.getE1().getIdentType();
		Token op=binaryExpression.getOp();
		// TODO - add op MOD 
		if(t1.equals(TypeName.INTEGER) && t2.equals(TypeName.INTEGER) && (op.isKind(DIV) || 
				op.isKind(PLUS) || op.isKind(MINUS) || op.isKind(TIMES) || op.isKind(MOD))){
			binaryExpression.setIdentType(INTEGER);
		}
		else if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.IMAGE) && 
				(op.isKind(PLUS) || op.isKind(MINUS))){
			binaryExpression.setIdentType(IMAGE);
		}
		// TODO add div & mod 
		else if((t1.equals(TypeName.IMAGE) && t2.equals(TypeName.INTEGER) && 
				((op.isKind(TIMES) || op.isKind(MOD) || op.isKind(DIV)))) || (t1.equals(TypeName.INTEGER) && t2.equals(TypeName.IMAGE) && 
						(op.isKind(TIMES)))){
			binaryExpression.setIdentType(IMAGE);
		}
		else if(t1.equals(TypeName.INTEGER) && t2.equals(TypeName.INTEGER) && 
				(op.isKind(LT) || op.isKind(GT) || op.isKind(LE) || op.isKind(GE))){
			binaryExpression.setIdentType(BOOLEAN);
		}
		//FIXME: check if and and or are correct
		else if(t1.equals(TypeName.BOOLEAN) && t2.equals(TypeName.BOOLEAN) && 
				(op.isKind(LT) || op.isKind(GT) || op.isKind(LE) || op.isKind(GE)) || op.isKind(AND) || op.isKind(OR)){
			binaryExpression.setIdentType(BOOLEAN);
		}
		else if(op.isKind(Kind.EQUAL) || op.isKind(NOTEQUAL)){
			if(t1.equals(t2)){
				binaryExpression.setIdentType(BOOLEAN);
			}
			else{
				throw new TypeCheckException("Illegal expression");
			}
		}
		else{
			throw new TypeCheckException("Illegal expression");
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		List<Statement> statList=new ArrayList<Statement>();
		List<Dec> decList=new ArrayList<Dec>();
	//	String className=null;
		symtab.enterScope();
		decList=block.getDecs();
		if(decList.size()>0){
			Iterator<Dec> itr=decList.iterator();
			while(itr.hasNext()){
				itr.next().visit(this, arg);
			}
		}
		statList=block.getStatements();
		if(statList.size()>0){
			Iterator<Statement> itr1=statList.iterator();
			while(itr1.hasNext()){
				Statement st=itr1.next();
		//		System.out.println(st.getFirstToken().getText());
				st.visit(this, arg);
			}
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		booleanLitExpression.setIdentType(BOOLEAN);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		int size=filterOpChain.getArg().getExprList().size();
		if(size==0){
			filterOpChain.setIdentType(IMAGE);
			filterOpChain.getArg().visit(this, arg);
		}
		else{
			throw new TypeCheckException("Should be 0");
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		if(frameOpChain.firstToken.isKind(Kind.KW_SHOW) || frameOpChain.firstToken.isKind(Kind.KW_HIDE)){
			int size=frameOpChain.getArg().getExprList().size();
			if(size!=0){
				throw new TypeCheckException("Length should be 0");
			}
			else{
				frameOpChain.setIdentType(NONE);
				frameOpChain.getArg().visit(this, arg);
			}
		}
		else if(frameOpChain.firstToken.isKind(Kind.KW_XLOC) || frameOpChain.firstToken.isKind(Kind.KW_YLOC)){
			int size=frameOpChain.getArg().getExprList().size();
			if(size!=0){
				throw new TypeCheckException("Length should be 0");
			}
			else{
				frameOpChain.setIdentType(INTEGER);
				frameOpChain.getArg().visit(this, arg);
			}
		}
		else if(frameOpChain.firstToken.isKind(Kind.KW_MOVE)){
			int size=frameOpChain.getArg().getExprList().size();
			if(size!=2){
				throw new TypeCheckException("Length should be 2");
			}
			else{
				frameOpChain.setIdentType(NONE);
				frameOpChain.getArg().visit(this, arg);
			}
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		Dec dec=symtab.lookup(identChain.firstToken.getText());
		if(dec==null){
			throw new TypeCheckException("Is null");
		}
		else{
			TypeName type=Type.getTypeName(dec.getFirstToken());
			identChain.setIdentType(type);
			dec.setIdentType(type);
			identChain.setDec(dec);
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		Dec dec=symtab.lookup(identExpression.firstToken.getText());
		if(dec==null){
			throw new TypeCheckException("Is null");
		}
		else{
			TypeName type=Type.getTypeName(dec.getFirstToken());
			identExpression.setIdentType(type);
			dec.setIdentType(type);
			identExpression.setDec(dec);
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
	//	String className =ifStatement.getE().getClass().getSimpleName();
		Expression e=ifStatement.getE();
		e.visit(this, arg);
		TypeName tp=e.getIdentType(); 
		if(!tp.equals(TypeName.BOOLEAN)){
			throw new TypeCheckException("Should be boolean");
		}
//		checkExpressionType(className, e, arg);
		Block bk=ifStatement.getB();
		bk.visit(this, arg);
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		intLitExpression.setIdentType(INTEGER);
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		//String className =sleepStatement.getE().getClass().getSimpleName();
		Expression e=sleepStatement.getE();
		e.visit(this, arg);
	//	System.out.println(e.toString());
		//TypeName tp=checkExpressionType(className,e,arg);
	//	System.out.println("type:"+e.getIdentType());
		if(e.getIdentType()!=TypeName.INTEGER){
			throw new TypeCheckException("Should be an Integer");
		}
		return null;
	}
	
	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
	//	String className =whileStatement.getE().getClass().getSimpleName();
		Expression e=whileStatement.getE();
		e.visit(this, arg);
		TypeName tp=e.getIdentType();
		if(!tp.equals(TypeName.BOOLEAN)){
			throw new TypeCheckException("Should be boolean");
		}
//		checkExpressionType(className, e, arg);
		//Block bk=whileStatement.getB();
		whileStatement.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//String varType=declaration.firstToken.getText();
		boolean val=symtab.insert(declaration.getIdent().getText(), declaration);
		if(!val){
			throw new TypeCheckException("Variable already declared");
		}
		declaration.setIdentType(Type.getTypeName(declaration.getFirstToken()));
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		List<ParamDec> arrList=new ArrayList<ParamDec>();
		arrList=program.getParams();
		if(arrList.size()>0){
			Iterator<ParamDec> itr=arrList.iterator();
			while(itr.hasNext()){
				itr.next().visit(this, arg);
			//	visitParamDec(itr.next(), arg);
			}
		}
		program.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getVar().visit(this, arg);
		TypeName tp= assignStatement.getVar().getDec().getIdentType();
		//TypeName tp=visitIdentLValue(assignStatement.var, arg);
	//	System.out.println(assignStatement.getVar().getDec().getClass().getSimpleName());
	//	String className =assignStatement.getE().getClass().getSimpleName();
	//	System.out.println(className);
		Expression e=assignStatement.getE();
		e.visit(this, arg);
		TypeName tp1=assignStatement.getE().getIdentType();
//		TypeName tp1=checkExpressionType(className, e, arg);
	//	System.out.println(tp.name());
		//System.out.println(tp1.name());
		if(!tp.equals(tp1)){
			throw new TypeCheckException("Types not the same");
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
	//	System.out.println(identX.firstToken.getText());
		Dec dec=symtab.lookup(identX.firstToken.getText());
		if(dec==null){
			throw new TypeCheckException("Dec is null");
		}
		else{
			TypeName type=Type.getTypeName(dec.getFirstToken());
			dec.setIdentType(type);
			identX.setDec(dec);
		}
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		boolean val=symtab.insert(paramDec.getIdent().getText(), paramDec);
		if(!val){
			throw new TypeCheckException("Variable already declared");
		}
		paramDec.setIdentType(Type.getTypeName(paramDec.getFirstToken()));
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		constantExpression.setIdentType(INTEGER);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		if(imageOpChain.firstToken.isKind(OP_WIDTH) || imageOpChain.firstToken.isKind(Kind.OP_HEIGHT)){
			int size=imageOpChain.getArg().getExprList().size();
			if(size!=0){
				throw new TypeCheckException("Length should be 0");
			}
			else{
				imageOpChain.setIdentType(INTEGER);
				imageOpChain.getArg().visit(this, arg);
			}
		}
		else if(imageOpChain.firstToken.isKind(Kind.KW_SCALE)){
			int size=imageOpChain.getArg().getExprList().size();
			if(size!=1){
				throw new TypeCheckException("Length should be 1");
			}
			else{
				imageOpChain.setIdentType(TypeName.IMAGE);
				imageOpChain.getArg().visit(this, arg);
			}
		}
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		List<Expression> expList=tuple.getExprList();
		for(Expression expression : expList){
			expression.visit(this, arg);
			if(expression.getIdentType()!=(TypeName.INTEGER)){
				throw new TypeCheckException("Tuple should be of type Integer");
			}
		}
		return null;
	}
}
