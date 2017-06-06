package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	ASTNode parse() throws SyntaxException {
		Program pg=null;
		pg=program();
		matchEOF();
		return pg;
	}
	
	Program program() throws SyntaxException {
		Program pg=null;
		Token firstToken=t;
		Block bk=null;
		Expression expr=null;
		ArrayList<ParamDec> paramList=new ArrayList<ParamDec>();
		try{
			expr=factor();
		}
		catch(Exception e){
			throw new SyntaxException("Program should start with an IDENT! Position in line:"+t.pos);
		}
		if(t.isKind(LBRACE)){
			bk=block();
			pg=new Program(firstToken, paramList, bk);
			}
		else if(t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN)){
			paramList.add(paramDec());
			while(t.isKind(Kind.COMMA)){
				consume();
				try{
					paramList.add(paramDec());
				}
				catch(Exception e){
					throw new SyntaxException("Param expected! Position in line:"+t.pos);
				}
			}
			try{
				bk=block();
				pg=new Program(firstToken, paramList, bk);
			}
			catch(Exception e){
				throw new SyntaxException("Expected a block. Not present. Illegal syntax. Position in line:"+t.pos);
			}
		}		
		else{
			throw new SyntaxException("Syntax error! Program cannot start with "+ t.kind+ ".Position in line:"+t.pos);
		}
		return pg;
	}
	
	Expression factor() throws SyntaxException {
		Token firstToken=t;
		Expression exp=null;
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			exp=new IdentExpression(firstToken);
			consume();
		}
			break;
		case INT_LIT: {
			exp=new IntLitExpression(firstToken);
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			exp=new BooleanLitExpression(firstToken);
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			exp=new ConstantExpression(firstToken);
			consume();
		}
			break;
		case LPAREN: {
			consume();
			try{
				exp=expression();
			}
			catch(Exception e){
				throw new SyntaxException("Lparen should be followed by an expression. Position in line:"+t.pos);
			}
			while(t.kind.equals(COMMA)){
				consume();
				try{
					exp=expression();
				}
				catch(Exception e){
					throw new SyntaxException("Expression missing!!Position in line:"+t.pos);
				}
			}
			if(t.isKind(RPAREN)){
				match(RPAREN);
			}
			else{
				throw new SyntaxException("Lparen should be matched with a Rparen. Position in line:"+t.pos);
			}
		}
			break;
		default:
			throw new SyntaxException("Factor not recognized by the grammar. Position in line:"+t.pos);
		}
		return exp;
	}
	
	Block block() throws SyntaxException {
		Block bk=null;
		ArrayList<Dec> decList=new ArrayList<Dec>();
		ArrayList<Statement> statementList=new ArrayList<Statement>();
		Token firstToken=t;
		if(t.isKind(LBRACE)){
			consume();
			while(t.isKind(Kind.KW_INTEGER) || t.isKind(Kind.KW_BOOLEAN) || t.isKind(Kind.KW_FRAME) || t.isKind(Kind.KW_IMAGE) || t.isKind(OP_SLEEP) || t.isKind(Kind.KW_WHILE) || t.isKind(KW_IF) || t.isKind(IDENT)
					|| t.isKind(Kind.OP_BLUR) || t.isKind(Kind.OP_GRAY) || t.isKind(Kind.OP_CONVOLVE)
					|| t.isKind(Kind.KW_SHOW) || t.isKind(Kind.KW_HIDE) || t.isKind(Kind.KW_MOVE) || t.isKind(Kind.KW_XLOC) || t.isKind(Kind.KW_YLOC)
					|| t.isKind(Kind.OP_WIDTH) || t.isKind(Kind.OP_HEIGHT) || t.isKind(Kind.KW_SCALE)){
				if(t.isKind(Kind.KW_INTEGER) || t.isKind(Kind.KW_BOOLEAN) || t.isKind(Kind.KW_FRAME) || t.isKind(Kind.KW_IMAGE)){
					decList.add(dec());
				}
				if(t.isKind(OP_SLEEP) || t.isKind(Kind.KW_WHILE) || t.isKind(KW_IF) || t.isKind(IDENT)|| t.isKind(Kind.OP_BLUR) || t.isKind(Kind.OP_GRAY) || t.isKind(Kind.OP_CONVOLVE)
						|| t.isKind(Kind.KW_SHOW) || t.isKind(Kind.KW_HIDE) || t.isKind(Kind.KW_MOVE) || t.isKind(Kind.KW_XLOC) || t.isKind(Kind.KW_YLOC)
						|| t.isKind(Kind.OP_WIDTH) || t.isKind(Kind.OP_HEIGHT) || t.isKind(Kind.KW_SCALE)){
					statementList.add(statement());
				}
				
			}
			bk=new Block(firstToken, decList, statementList);
			if(t.isKind(RBRACE)){
				consume();
			}
			else{
				throw new SyntaxException("Should be followed by '}' Position in line:"+t.pos);
			}
		}
		else{
			throw new SyntaxException("Block always starts with  '{' Position in line:"+t.pos);
		}
		return bk;
	}

	Expression expression() throws SyntaxException {
		Token firstToken=t;
		Expression e0=null;
		Expression e1=null;
		try{
			e0=term();
		}
		catch(Exception e){
			throw new SyntaxException("Illegal term syntax! Position in line:"+t.pos);
		}
		while (t.isKind(LT) || t.isKind(LE) || t.isKind(GT) || t.isKind(GE) ||t.isKind(EQUAL) || t.isKind(NOTEQUAL)){
			Token op=t;
			consume(); 
			try{
				e1=term();
				e0=new BinaryExpression(firstToken, e0, op, e1);
			}
			catch(Exception e){
				throw new SyntaxException("Missing token. Term expected! Position in line:"+t.pos);
			}
		}
		return e0;
	}

	Expression term() throws SyntaxException {
		Token firstToken=t;
		Expression expr1=null;
		Expression expr2=null;
		try{
			expr1=elem();
		}
		catch(Exception e){
			throw new SyntaxException("Missing token. Elem expected! Position in line:"+t.pos);
		}
		while (t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR)){
			Token op=t;
			consume(); 
			try{
				expr2=elem();
				expr1 = new BinaryExpression(firstToken, expr1, op, expr2);
			}
			catch(Exception e){
				throw new SyntaxException("Missing token. Elem expected! Position in line:"+t.pos);
			}
		} 
		return expr1;
	}

	Expression elem() throws SyntaxException {
		Token firstToken=t;
		Expression expr1=null;
		Expression expr2=null;
		try{
			expr1=factor();
		}
		catch(Exception e){
			throw new SyntaxException("Expected a factor! Position in line:"+t.pos);
		}
		while (t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD)){
			Token op=t;
			consume(); 
			expr2=factor(); 
			expr1 = new BinaryExpression(firstToken, expr1, op, expr2);
		} 
		return expr1;
	}

	ParamDec paramDec() throws SyntaxException {
		ParamDec dec=null;
		Expression expr=null;
		Token firstToken=t;
		Kind kind = t.kind;
		switch (kind) {
		case KW_URL: {
			consume();
		}
			break;
		case KW_FILE: {
			consume();
		}
			break;
		case KW_INTEGER:{
			consume();
		}
			break;
		case KW_BOOLEAN:{
			consume();
		}
			break;
		
		default:
			throw new SyntaxException("illegal parameter!! Position in line:"+t.pos);
		}
		try{
			expr=factor();
			dec=new ParamDec(firstToken, expr.firstToken);
		}
		catch(Exception e){
			throw new SyntaxException("Identifier should be followed by the keyword. Position in line:"+t.pos);
		}
		return dec;
	}

	Dec dec() throws SyntaxException {
		Dec d=null;
		Token firstToken=t;
		Expression expr=null;
		Kind kind = t.kind;
		switch (kind) {
		case KW_INTEGER: {
			consume();
		}
			break;
		case KW_BOOLEAN: {
			consume();
		}
			break;
		case KW_IMAGE:{
			consume();
		}
			break;
		case KW_FRAME:{
			consume();
		}
			break;
		
		default:
			throw new SyntaxException("illegal declaration. Position in line:"+t.pos);
		}
		try{
			expr=factor();
			d=new Dec(firstToken, expr.getFirstToken());
		}
		catch(Exception e){
			throw new SyntaxException("Factor expected! Position in line:"+t.pos);
		}
		return d;
	}
	
	Token arrowOp() throws SyntaxException {
		Token firstToken=t;
		Kind kind = t.kind;
		switch (kind) {
		case ARROW: {
			consume();
		}
			break;
		case BARARROW: {
			consume();
		}
			break;
		default:
			throw new SyntaxException("Illegal arror operator. Position in line:"+t.pos);
		}
		return firstToken;
	}
	
	Statement statement() throws SyntaxException {
		List<Statement> statementList=new ArrayList<Statement>();
		Token firstToken=t;
		Statement st=null;
		Block bk=null;
		Expression expr=null;
		Chain ch=null;
		if(t.isKind(OP_SLEEP)){
			consume();
			try{
				expr=expression();
				st=new SleepStatement(firstToken, expr);
			}
			catch(Exception e){
				throw new SyntaxException("Expression expected! Position in line:"+t.pos);
			}
			if(t.isKind(SEMI)){
				consume();
			}
			else{
				throw new SyntaxException("Semi colon should be present. Position in line:"+t.pos);
			}
		}
		else if(t.isKind(KW_WHILE)){
			consume();
			try{
				expr=factor();
			}
			catch(Exception e){
				throw new SyntaxException("Illegal factor!! Position in line:"+t.pos);
			}
			try{
				bk=block();
				st=new WhileStatement(firstToken, expr, bk);
			}
			catch(Exception e){
				throw new SyntaxException("Illegal block expression! Position in line:"+t.pos);
			}
		}
		else if(t.isKind(KW_IF)){
			consume();
			try{
				expr=factor();
			}
			catch(Exception e){
				throw new SyntaxException("Illegal factor!! Position in line:"+t.pos);
			}
			try{
				bk=block();
				st=new IfStatement(firstToken, expr, bk);
			}
			catch(Exception e){
				throw new SyntaxException("Illegal block expression! Position in line:"+t.pos);
			}
		}
		else if(t.isKind(IDENT)){
			IdentLValue id= new IdentLValue(t);
			if(scanner.peek().isKind(ASSIGN)){
				consume();
				expr=assign();
				st=new AssignmentStatement(firstToken, id, expr);
				
			}
			else if((scanner.peek().isKind(ARROW)) || (scanner.peek().isKind(BARARROW)) ){
				st=chain();
				
			}
			else{
				throw new SyntaxException("Illegal statement. Position in line:"+t.pos);
			}
			if(t.isKind(SEMI)){
				consume();
			}
			else{
				throw new SyntaxException("Semi colon should be present. Position in line:"+t.pos);
			}
			
		}
		else if((t.isKind(OP_BLUR)) || (t.isKind(OP_GRAY)) || (t.isKind(OP_CONVOLVE))
				|| (t.isKind(KW_SHOW)) || (t.isKind(KW_HIDE)) || (t.isKind(KW_MOVE)) || 
				(t.isKind(KW_XLOC)) || (t.isKind(KW_YLOC)) || (t.isKind(OP_WIDTH)) || (t.isKind(OP_HEIGHT)) 
				|| (t.isKind(KW_SCALE))){
			st=chain();
			if(t.isKind(SEMI)){
				consume();
			}
			else{
				throw new SyntaxException("Semi colon should be present. Position in line:"+t.pos);
			}
			
		}
		else{
			throw new SyntaxException("Illegal statement. Position in line:"+t.pos);
		}
		return st;
	}
	
	Expression assign() throws SyntaxException{
		Token firstToken=t;
		Expression expr=null;
		consume();
		try{
			expr=expression();
		}
		catch(Exception e){
			throw new SyntaxException("Expression expected! Position in line:"+t.pos);
		}
		return expr;
	}
	Chain chain() throws SyntaxException {
		Chain ch=null;
		ChainElem ce=null;
		ChainElem ce1=null;
		Token firstToken=t;
		Token op=null;
		try{
			ce=chainElem();
			op=arrowOp();
			ce1=chainElem();
			ch=new BinaryChain(firstToken, ce, op, ce1);
		}
		catch(Exception e){
			throw new SyntaxException("Expected chain element! Position in line:"+t.pos);
		}
		while(t.isKind(ARROW) || t.isKind(BARARROW)){
			op=arrowOp();
			ce1=chainElem();
			ch=new BinaryChain(firstToken, ch, op, ce1);
		}
		return ch;
	}

	ChainElem chainElem() throws SyntaxException {
		ChainElem ce=null;
		Token firstToken=t;
		Tuple tuple=null;
		Kind kind= t.kind;
		if((kind.equals(OP_BLUR)) || (kind.equals(OP_GRAY)) || (kind.equals(OP_CONVOLVE))){
			consume();	
			try{
				tuple=arg();
				ce=new FilterOpChain(firstToken,tuple);
			}
			catch(Exception e){
				throw new SyntaxException("Illegal argument. Position in line:"+t.pos);
			}
		}
		else if((kind.equals(KW_SHOW)) || (kind.equals(KW_HIDE)) || (kind.equals(KW_MOVE)) || 
				(kind.equals(KW_XLOC)) || (kind.equals(KW_YLOC))){
			consume();	
			try{
				tuple=arg();
				ce=new FrameOpChain(firstToken,tuple);
			}
			catch(Exception e){
				throw new SyntaxException("Illegal argument. Position in line:"+t.pos);
			}
		}
		else if((kind.equals(OP_WIDTH)) || (kind.equals(OP_HEIGHT)) || (kind.equals(KW_SCALE))){
			consume();	
			try{
				tuple=arg();
				ce=new ImageOpChain(firstToken,tuple);
			}
			catch(Exception e){
				throw new SyntaxException("Illegal argument. Position in line:"+t.pos);
			}
		}
		else if(kind.equals(IDENT)){
			consume();
			ce= new IdentChain(firstToken);
		}
		else{
			throw new SyntaxException("Illegal chain element. Position in line:"+t.pos);
		}
		return ce;
	}

	Tuple arg() throws SyntaxException {
		ArrayList<Expression> argList=new ArrayList<Expression>();
		Token firstToken=t;
		if(t.getText().equals("") || t.isKind(LPAREN)){
			if(t.isKind(LPAREN)){
				consume();
				try{
					argList.add(expression());
				}
				catch(Exception e){
					throw new SyntaxException("Lparen should be followed by an expression. Position in line:"+t.pos);
				}
				while(t.kind.equals(COMMA)){
					consume();
					try{
						argList.add(expression());
					}
					catch(Exception e){
						throw new SyntaxException("Expression missing!!Position in line:"+t.pos);
					}
				}
				if(t.isKind(RPAREN)){
					match(RPAREN);
				}
				else{
					throw new SyntaxException("Lparen should be matched with a Rparen. Position in line:"+t.pos);
				}
			}
			else if(t.getText().equals("")){
				return new Tuple(firstToken, argList);
			}
			else{
				throw new SyntaxException("Illegal argument! Position in line:"+t.pos);
			}
		}
		return new Tuple(firstToken, argList);
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + " expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		return null; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}