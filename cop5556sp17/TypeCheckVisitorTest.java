/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.Statement;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;

public class TypeCheckVisitorTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testAssignmentBoolLit0() throws Exception{
		String input = "p {\nboolean y \ny <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}

	@Test
	public void testAssignmentBoolLitError0() throws Exception{
		String input = "p {\nboolean y \ny <- 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}	
	
	@Test
	public void testBinaryExpression0() throws Exception{
	  String input = "1+2";
	  Scanner scanner = new Scanner(input);
	  scanner.scan();
	  Parser parser = new Parser(scanner);
	  ASTNode expression = parser.expression();
	  TypeCheckVisitor v = new TypeCheckVisitor();
	  expression.visit(v, null);		
	}

//	@Test
//	public void testFrame0() throws Exception{
//	  String input = "hide -> show";
//	  Scanner scanner = new Scanner(input);
//	  scanner.scan();
//	  Parser parser = new Parser(scanner);
//	  ASTNode chain = parser.chain();
//	  TypeCheckVisitor v = new TypeCheckVisitor();
//	  chain.visit(v, null);		
//	}	

	@Test
	public void testProgram0() throws Exception{
	  String input = "a123{integer abc \ninteger x \nboolean y \nx<-3;}";
	  Scanner scanner = new Scanner(input);
	  scanner.scan();
	  Parser parser = new Parser(scanner);
	  ASTNode program = parser.parse();
	  TypeCheckVisitor v = new TypeCheckVisitor();
	  program.visit(v, null);		
	}

	@Test
	public void testProgram1() throws Exception{
	  String input = "abc{}";
	  Scanner scanner = new Scanner(input);
	  scanner.scan();
	  Parser parser = new Parser(scanner);
	  ASTNode program = parser.parse();
	  TypeCheckVisitor v = new TypeCheckVisitor();
	  program.visit(v, null);		
	}

	@Test
	public void testProgram2() throws Exception{
	  String input = "abc{integer c integer ax image a frame b sleep 3-1;}";
	  Scanner scanner = new Scanner(input);
	  scanner.scan();
	  Parser parser = new Parser(scanner);
	  ASTNode program = parser.parse();
	  TypeCheckVisitor v = new TypeCheckVisitor();
	  program.visit(v, null);		
	}

	@Test
	public void testStatement0() throws Exception{
	  String input = "while true{integer c \n c<-1*4;}";
	  Scanner scanner = new Scanner(input);
	  scanner.scan();
	  Parser parser = new Parser(scanner);
	  ASTNode statement = parser.statement();
	  TypeCheckVisitor v = new TypeCheckVisitor();
	  statement.visit(v, null);		
	}

	@Test
	public void testStatement1() throws Exception{
	  String input = "if (2>3){integer c \n c<-1*4;}";
	  Scanner scanner = new Scanner(input);
	  scanner.scan();
	  Parser parser = new Parser(scanner);
	  ASTNode statement = parser.statement();
	  TypeCheckVisitor v = new TypeCheckVisitor();
	  statement.visit(v, null);		
	}
	
	@Test
	public void testScope() throws Exception{
		String input = "p integer a, integer b {image img1 image img2 if(img1 != img2) {image a a <- img1; } if(a != b) {boolean a a <- img1 != img2; }}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);	
	}
	
	@Test
	public void testImageOp3() throws Exception{
		String input= "prog  boolean y , file x { convolve -> blur -> gray |-> gray -> width;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);	
	}
	
	@Test
	public void testArrow2Error() throws Exception{
		String input="p url y {frame i  y->i;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);	
	}
	
	@Test
	public void testArrow3Error() throws Exception{
		String input="p url y {integer i  y->i;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);	
	}
	
	@Test
	public void testArrow4Error() throws Exception{
		String input="p url y {boolean i  y->i;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);	
	}
	
	@Test
	public void testArrow5() throws Exception{
		String input="p \nurl y {\n  image i\n  y|->i;\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);	
	}
	
	@Test
	public void testNested() throws Exception{
		String input="program { image first image last if(true) { integer first image second last->second->first;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
	//	thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);	
	}
	
	@Test
	public void complicatedProgram() throws Exception{
		String input= "prog1  file file1, integer itx, boolean b1{ integer ii1 boolean bi1 \n image IMAGE1 frame fram1 sleep itx+ii1; while (b1){if(bi1)\n{sleep ii1+itx*2;}}\nfile1->blur |->gray;fram1 ->yloc;\n IMAGE1->blur->scale (ii1+1)|-> gray;\nii1 <- 12345+54321;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);	
	}
	
	@Test
	public void compProg1() throws Exception{
	//	String input= "compProg1 integer a, integer b, integer c, boolean bool0 { a <- 4;  b <- 5; boolean boolA  boolean boolB  boolA <- true;  boolB <- false;  if(boolA == true)  {boolean a a <- boolA; bool0 <- false;while(a != boolB){integer d  integer e c <- 3 + 5; d <- 10 - 1; c <- c * d; e <- d / 3; if(c > d) {     c <- d;     if(c <= d)     {        boolA <- false;    }    if(boolA < boolB)     {        c <- 0;    }}} } if(c >= 1) {     /*boolB <- bool0 | true;*/} a <- 7;}";
		//String input="compProg1 integer a, integer b, integer c, boolean bool0 { a <- 4;  b <- 5; boolean boolA  boolean boolB  boolA <- true;  boolB <- false;  if(boolA == true)  {boolean a a <- boolA; bool0 <- false;while(a != boolB){integer d  integer e c <- 3 + 5; d <- 10 - 1; c <- c * d; e <- d / 3; a <- boolB;} }  a <- 7;}";
		String input="compProg1 integer a { boolean boolB if(boolB == false){ boolean a a <- true; if(a != false){ a <- boolB; }}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);	
	}
	
	@Test
	public void compProg2() throws Exception{
		String input= "compProg1 integer a, integer b, integer c, boolean bool0 { a <- 4;  b <- 5; boolean boolA  boolean boolB  boolA <- true;  boolB <- false;  if(boolA == true)  {boolean a a <- boolA; bool0 <- false;while(a != boolB){integer d  integer e c <- 3 + 5; d <- 10 - 1; c <- c * d; e <- d / 3; if(c > d) {     c <- d;     if(c <= d)     {        boolA <- false;    }    if(boolA < boolB)     {        c <- 0;    }}} } if(c >= 1) {     /*boolB <- bool0 | true;*/} a <- 7;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);	
	}
	
	@Test
	public void compProg3() throws Exception{
		String input="compProg1 integer a, integer b, integer c, boolean bool0 { a <- 4;  b <- 5; boolean boolA  boolean boolB  boolA <- true;  boolB <- false;  if(boolA == true)  {boolean a a <- boolA; bool0 <- false;while(a != boolB){integer d  integer e c <- 3 + 5; d <- 10 - 1; c <- c * d; e <- d / 3; a <- boolB;} }  a <- 7;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.program();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);	
	}
}
