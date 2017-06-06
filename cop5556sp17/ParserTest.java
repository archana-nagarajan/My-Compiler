package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc url abc{}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.parse();
	}

	@Test
	public void testFactor2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "()";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}

	@Test
	public void testFactor3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "arc{ while(abc){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}

	@Test
	public void testFactor4() throws IllegalCharException, IllegalNumberException, SyntaxException {
	//	String input = "arc url a,file b,integer c,boolean text{}";
	//	String input="arc {";
		String input="__ {__->_|->$0|-> show (__/_%($$TAT$T_T%$)|true*screenwidth&$|_*$!=_==_>=(z_z),_&$|_$+_0);}";
		//while (__==$$!=$_/_){sleep z$_2+_3z%$;} blur -> width ($);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testFactor8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "arc url a,file b,integer c,boolean text{}";
		//while (__==$$!=$_/_){sleep z$_2+_3z%$;} blur -> width ($);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testFactor9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input="arc {";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void testFactor10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input="while (__==$$!=$_/_){sleep z$_2+_3z%$;} blur -> width ($)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}

	@Test
	public void testFactor5() throws IllegalCharException, IllegalNumberException, SyntaxException {
	//	String input = "sleep 5";
	//	String input= "ab <- (true > false)";
	//	String input="while ((5*2)+10){}";
	//	String input= "blur |-> arc -> ttt -> ih |-> abb;";
	//	String input="{ x -> y ; }";
	//	String input="{ x |-> y ; }";
		String input="x -> show ;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testFactor11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep 5;";
	//	String input= "ab <- (true > false)";
	//	String input="while ((5*2)+10){}";
	//	String input= "blur |-> arc -> ttt -> ih |-> abb;";
	//	String input="{ x -> y ; }";
	//	String input="{ x |-> y ; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testFactor12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input= "ab <- (true > false);";
	//	String input="while ((5*2)+10){}";
	//	String input= "blur |-> arc -> ttt -> ih |-> abb;";
	//	String input="{ x -> y ; }";
	//	String input="{ x |-> y ; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testFactor13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input="while ((5*2)+10){}";
	//	String input= "blur |-> arc -> ttt -> ih |-> abb;";
	//	String input="{ x -> y ; }";
	//	String input="{ x |-> y ; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testFactor14() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input= "blur |-> arc -> ttt -> ih |-> abb;";
	//	String input="{ x -> y ; }";
	//	String input="{ x |-> y ; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testFactor15() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input="{ x -> y ; }";
	//	String input="{ x |-> y ; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}
	
	@Test
	public void testFactor16() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input="{ x |-> y ; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}
	
	@Test
	public void testFactor6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "arc {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.expression();
	}
	
	@Test
	public void testFactor7() throws IllegalCharException, IllegalNumberException, SyntaxException {
	//	String input="{ x -> y ; }";
		String input="{ x |-> y ; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
	//	thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5)) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,)) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}

	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
	}

}