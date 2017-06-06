package cop5556sp17;

import static cop5556sp17.Scanner.Kind.SEMI;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.LinePos;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}
	
	@Test
	public void posCheck() throws IllegalCharException, IllegalNumberException{
		String input="integer ij==\n023 4554 + archana\nfile 534\nsdf{\n} /* blah blah */";
	//	String input="integer\n name == 1\n test";
	//	String input="abc! !d";
	//	String input="a\nbc! !\nd";
	//	String input="}{+)!(";
	//	String input="!!!=!=!";
	//	String input="--->->-";
	//	String input="|;|--->->-|->";
	//	String input="000123";
	//	String input="ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image test";	
	//	String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck1() throws IllegalCharException, IllegalNumberException{
		String input="integer\n name == 1\n test";
	//	String input="abc! !d";
	//	String input="a\nbc! !\nd";
	//	String input="}{+)!(";
	//	String input="!!!=!=!";
	//	String input="--->->-";
	//	String input="|;|--->->-|->";
	//	String input="000123";
	//	String input="ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image test";	
	//	String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck2() throws IllegalCharException, IllegalNumberException{
		String input="abc! !d";
	//	String input="a\nbc! !\nd";
	//	String input="}{+)!(";
	//	String input="!!!=!=!";
	//	String input="--->->-";
	//	String input="|;|--->->-|->";
	//	String input="000123";
	//	String input="ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image test";	
	//	String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck3() throws IllegalCharException, IllegalNumberException{
		String input="a\nbc! !\nd";
	//	String input="}{+)!(";
	//	String input="!!!=!=!";
	//	String input="--->->-";
	//	String input="|;|--->->-|->";
	//	String input="000123";
	//	String input="ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image test";	
	//	String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck4() throws IllegalCharException, IllegalNumberException{
		String input="}{+)!(";
	//	String input="!!!=!=!";
	//	String input="--->->-";
	//	String input="|;|--->->-|->";
	//	String input="000123";
	//	String input="ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image test";	
	//	String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck5() throws IllegalCharException, IllegalNumberException{
		String input="!!!=!=!";
	//	String input="--->->-";
	//	String input="|;|--->->-|->";
	//	String input="000123";
	//	String input="ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image test";	
	//	String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck6() throws IllegalCharException, IllegalNumberException{
		String input="--->->-";
	//	String input="|;|--->->-|->";
	//	String input="000123";
	//	String input="ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image test";	
	//	String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck7() throws IllegalCharException, IllegalNumberException{
		String input="|;|--->->-|->";
	//	String input="000123";
	//	String input="ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image test";	
	//	String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck8() throws IllegalCharException, IllegalNumberException{
		String input="000123";
	//	String input="ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image test";	
	//	String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck9() throws IllegalCharException, IllegalNumberException{
		String input="ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image test";	
	//	String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck10() throws IllegalCharException, IllegalNumberException{
		String input="<<<=>>>=>< ->-->";
	//	String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck11() throws IllegalCharException, IllegalNumberException{
		String input="/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	//	String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck12() throws IllegalCharException, IllegalNumberException{
		String input="***%&";
	//	String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck13() throws IllegalCharException, IllegalNumberException{
		String input="show\r\n hide \n move \n file";
	//	String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void posCheck14() throws IllegalCharException, IllegalNumberException{
		String input="false123 false true true123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		for(int j=0;j<scanner.tokens.size();j++)
			System.out.println(scanner.tokens.get(j).kind + ":" + scanner.tokens.get(j).getText() + " " + (scanner.tokens.get(j).getLinePos()) );
	}
	
	@Test
	public void commentsCheck() throws IllegalCharException, IllegalNumberException{
		String input = "-< <- <+ <= <\n-<--";
		Scanner scanner = new Scanner(input);

		scanner.scan();		
	}
}
