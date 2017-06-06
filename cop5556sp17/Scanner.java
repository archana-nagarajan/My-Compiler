package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Scanner {
	
	List<Integer> startingIndex=new ArrayList<Integer>();
	/**
	 * Kind enum
	 */
	
	public static enum Kind { 
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
	
	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
	}

/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			StringBuffer sb=new StringBuffer();
			if(!chars.isEmpty() && !kind.text.equals("eof")){
				for(int i=pos;i<pos+length;i++){
					sb.append(chars.charAt(i));
				}
			}
			return sb.toString();
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			int line=0;
			int finalPos=0;
			for(int k=0;k<startingIndex.size();k++){
				if(startingIndex.get(k)==pos){
					line=k;
					finalPos=pos-(startingIndex.get(line));
					break;
				}
				else{
					if(startingIndex.get(k)<pos){
						continue;
					}
					else{
						line=k-1;
						if(line>0){
							finalPos=pos-(startingIndex.get(line))-1;
						}
						else{
							finalPos=pos-(startingIndex.get(line));
						}
						break;
					}				
				}
			}		
			LinePos lpos=new LinePos(line, finalPos);
			
			return lpos;
		}
		
		@Override
		  public int hashCode() {
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		  }
		
		@Override
		  public boolean equals(Object obj) {
		   if (this == obj) {
		    return true;
		   }
		   if (obj == null) {
		    return false;
		   }
		   if (!(obj instanceof Token)) {
		    return false;
		   }
		   Token other = (Token) obj;
		   if (!getOuterType().equals(other.getOuterType())) {
		    return false;
		   }
		   if (kind != other.kind) {
		    return false;
		   }
		   if (length != other.length) {
		    return false;
		   }
		   if (pos != other.pos) {
		    return false;
		   }
		   return true;
		  }
		
		  private Scanner getOuterType() {
			   return Scanner.this;
			  }

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			int value=0;
			try{
			StringBuffer sb=new StringBuffer();
			for(int i=pos;i<pos+length;i++){
				sb.append(chars.charAt(i));
			}
			value=Integer.parseInt(sb.toString());
			}
			catch(NumberFormatException e){
				System.out.println(e);
			}
			catch(Exception e){
				System.out.println(e);
			}
			return value;
		}
		
		public boolean isKind(Kind kind){
			if(this.kind.equals(kind)){
				return true;
			}
			return false;
			
		}
	}

	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		int i=0;
		StringBuffer sb=new StringBuffer(); 
		Kind val=null;
		startingIndex.add(0);
		for(;i<chars.length();i++){
			char character=chars.charAt(i);
			if(Character.isWhitespace(character) && chars.charAt(i)!='\n'){
				if(sb.length()>0){
					String check=checkIfReserved(sb);
					if(check!=null){
						tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));					
					}
					else{
						if(val==Kind.INT_LIT){
							long l= Long.parseLong(sb.toString());
							if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
								throw new IllegalNumberException("Out of range of Integer datatype");
							}
						}
						tokens.add(new Token(val, i-sb.length(),sb.length()));
					}
					val=null;
					sb=new StringBuffer();
				}
				continue;
			}
			if(character=='\n'){
				startingIndex.add(i);
			}
			if(sb.length()==0 && Character.isJavaIdentifierStart(character)){
				sb.append(character);
				val=Kind.IDENT;
			}
			else if(Character.isJavaIdentifierPart(character)){
				if(val==Kind.IDENT){
					sb.append(character);
				}
				else if(Character.isDigit(character)){
					sb.append(character);
					if(sb.length()==1 && sb.toString().charAt(0)=='0'){
						tokens.add(new Token(Kind.INT_LIT, i,1));
						sb=new StringBuffer();
					}
					else{
						val=Kind.INT_LIT;
					}
				}
				else{
					throw  new IllegalCharException("Illegal Character Encountered.");
				}
			}
			else{
				switch(character){
				case '+': 
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));			
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));
						}
					}
					tokens.add(new Token(Kind.PLUS, i,1));
					val=null;
					sb=new StringBuffer();
					break;
				case '-': 
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					i++;
					if(i<chars.length() && chars.charAt(i)=='>'){
						tokens.add(new Token(Kind.ARROW, i-1,2));
					}
					else{
						i--;
						tokens.add(new Token(Kind.MINUS, i,1));
					}
					val=null;
					sb=new StringBuffer();
					break;
				case '*': 
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));					
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));
						}
					}
					tokens.add(new Token(Kind.TIMES, i,1));
					val=null;
					sb=new StringBuffer();
					break;
				case '/': 
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));					
						}
					}
					i++;
					if(i<chars.length() && chars.charAt(i)=='*'){
						val=null;
						sb=new StringBuffer();
						for(int j=i;j<chars.length();j++){
							if(chars.charAt(j)=='\n'){
								startingIndex.add(j);
							}
							if(chars.charAt(j)=='*'){
								j=j+1;
								if(j<chars.length() && chars.charAt(j)=='/'){
									i=j;
									break;
								}
								else{
									j--;
								}
							}
							
							else if(j==chars.length()-1){
								i=j;
								break;
							}
							else{
								continue;
							}
						}
						
					}
					else{
						i--;
						tokens.add(new Token(Kind.DIV, i,1));
					}
					val=null;
					sb=new StringBuffer();
					break;
				case '%': 
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					tokens.add(new Token(Kind.MOD, i,1));
					val=null;
					sb=new StringBuffer();
					break;
				case '=': 
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));
						}
					}
					i++;
					if(i>chars.length()-1 || chars.charAt(i)!='='){
						i--;
						throw  new IllegalCharException("Illegal Character Encountered. '=' is Invalid");
					}
					if(chars.charAt(i)=='='){
						tokens.add(new Token(Kind.EQUAL, i-1,2));
					}
					val=null;
					sb=new StringBuffer();
					break;
				case '\n': 
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));					
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));
						}
					}
					val=null;
					sb=new StringBuffer();
					break;
				case '&':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));
						}
					}
					tokens.add(new Token(Kind.AND, i,1));
					val=null;
					sb=new StringBuffer();
					break;
				case '|':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));
						}
					}
					i++;
					if(i<chars.length() && chars.charAt(i)=='-'){
						i++;
						if(i<chars.length() && chars.charAt(i)=='>'){
							tokens.add(new Token(Kind.BARARROW, i-2,3));
						}
						else{
							i--;
							tokens.add(new Token(Kind.OR, i-1,1));
							tokens.add(new Token(Kind.MINUS, i,1));
						}
					}
					else{
						i--;
						tokens.add(new Token(Kind.OR, i,1));
					}
					val=null;
					sb=new StringBuffer();
					break;
				case '!':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					i++;
					if(i<chars.length() && chars.charAt(i)=='='){
						tokens.add(new Token(Kind.NOTEQUAL, i-1,2));
					}
					else{
						i--;
						tokens.add(new Token(Kind.NOT, i,1));
					}
					val=null;
					sb=new StringBuffer();
					break;
				case '<':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					i++;
					if(i<chars.length() && chars.charAt(i)=='='){
						tokens.add(new Token(Kind.LE, i-1,2));
					}
					else if(i<chars.length() && chars.charAt(i)=='-'){
						tokens.add(new Token(Kind.ASSIGN, i-1,2));
					}
					else{
						i--;
						tokens.add(new Token(Kind.LT, i,1));
					}
					val=null;
					sb=new StringBuffer();
					break;
				case '>':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					i++;
					if(i<chars.length() && chars.charAt(i)=='='){
						tokens.add(new Token(Kind.GE, i-1,2));
					}
					else{
						i--;
						tokens.add(new Token(Kind.GT, i,1));
					}
					val=null;
					sb=new StringBuffer();
					break;
				case ';':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					tokens.add(new Token(Kind.SEMI, i,1));
					val=null;
					sb=new StringBuffer();
					break;
				case ',':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					tokens.add(new Token(Kind.COMMA, i,1));
					val=null;
					sb=new StringBuffer();
					break;
				case '(':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					tokens.add(new Token(Kind.LPAREN, i,1));
					val=null;
					sb=new StringBuffer();
					break;
				case ')':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					tokens.add(new Token(Kind.RPAREN, i,1));
					val=null;
					sb=new StringBuffer();
					break;
				case '{':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					tokens.add(new Token(Kind.LBRACE, i,1));
					val=null;
					sb=new StringBuffer();
					break;
				case '}':
					if(sb.length()>0){ 
						String check=checkIfReserved(sb);
						if(check!=null){
							tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
						}
						else{
							if(val==Kind.INT_LIT){
								long l= Long.parseLong(sb.toString());
								if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
									throw new IllegalNumberException("Out of range of Integer datatype");
								}
							}
							tokens.add(new Token(val, i-sb.length(),sb.length()));	
						}
					}
					tokens.add(new Token(Kind.RBRACE, i,1));
					val=null;
					sb=new StringBuffer();
					break;
				default:
					val=null;
					sb=new StringBuffer();
					throw new IllegalCharException("Illegal character found. Character not supported by the grammar");	
				}
			}
		}
		startingIndex.add(i);
		
		if(sb.length()>0){
			String check=checkIfReserved(sb);
			if(check!=null){
				tokens.add(new Token(Kind.valueOf(check), i-sb.length(),sb.length()));
			}
			else{
				if(val==Kind.INT_LIT){
					long l= Long.parseLong(sb.toString());
					if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
						throw new IllegalNumberException("Out of range of Integer datatype");
					}
				}
				tokens.add(new Token(val, i-sb.length(),sb.length()));
			}
			sb=new StringBuffer();
		}
		tokens.add(new Token(Kind.EOF,i,1));
		
//		for(int j=0;j<tokens.size();j++)
//			System.out.println(tokens.get(j).kind + ":" + tokens.get(j).kind.text);
		return this;  
	}
	
	public String checkIfReserved(StringBuffer sb) {
		Map<String,Map<String,String>> reservedWords=new HashMap<String,Map<String,String>>();
		Map<String,String> keywordList=new HashMap<String,String>();
		keywordList.put("KW_INTEGER","integer");
		keywordList.put("KW_BOOLEAN","boolean");
		keywordList.put("KW_IMAGE","image");
		keywordList.put("KW_URL","url");
		keywordList.put("KW_FILE","file");
		keywordList.put("KW_FRAME","frame");
		keywordList.put("KW_WHILE","while");
		keywordList.put("KW_IF","if");
		keywordList.put("OP_SLEEP","sleep");
		keywordList.put("KW_SCREENHEIGHT","screenheight");
		keywordList.put("KW_SCREENWIDTH","screenwidth");
		reservedWords.put("keyword" , keywordList);

		Map<String,String> filter_op_keywords =new HashMap<String,String>();
		filter_op_keywords.put("OP_GRAY","gray");
		filter_op_keywords.put("OP_CONVOLVE","convolve");
		filter_op_keywords.put("OP_BLUR","blur");
		filter_op_keywords.put("KW_SCALE","scale");
		reservedWords.put("filter_op_keyword" , filter_op_keywords);
		
		Map<String,String> image_op_keywords =new HashMap<String,String>();
		image_op_keywords.put("OP_WIDTH","width");
		image_op_keywords.put("OP_HEIGHT","height");
		reservedWords.put("image_op_keyword", image_op_keywords);
		
		Map<String,String> frame_op_keywords =new HashMap<String,String>();
		frame_op_keywords.put("KW_XLOC","xloc");
		frame_op_keywords.put("KW_YLOC","yloc");
		frame_op_keywords.put("KW_HIDE","hide");
		frame_op_keywords.put("KW_SHOW","show");
		frame_op_keywords.put("KW_MOVE","move");
		reservedWords.put("frame_op_keywords", frame_op_keywords);
		
		Map<String,String> booleanLiteral=new HashMap<String,String>();
		booleanLiteral.put("KW_TRUE","true");
		booleanLiteral.put("KW_FALSE","false");
		reservedWords.put("booleanLiteral", booleanLiteral);
		
		final Set<Map.Entry<String, Map<String,String>>> entries = reservedWords.entrySet();

		for (Map.Entry<String, Map<String,String>> entry : entries) {
			Map<String,String> tempList = entry.getValue();
			final Set<Entry<String, String>> innerEntries = tempList.entrySet();
			for (Entry<String, String> innerEntry : innerEntries) {
				String key=innerEntry.getKey();
			    if(innerEntry.getValue().equals(sb.toString())){
			    	return key;
			    }
			}
		}
		return null;		
	}

	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	 /*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */public Token peek() {
	    if (tokenNum >= tokens.size())
	        return null;
	    return tokens.get(tokenNum);
	}
	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		LinePos lpos=t.getLinePos();
		return lpos;
	}
}