package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;


public abstract class Chain extends Statement {
	private TypeName identType;
	
	public Chain(Token firstToken) {
		super(firstToken);
	}
	
	public void setIdentType(TypeName identType){
		this.identType=identType;
	}
	
	public TypeName getIdentType(){
		return this.identType;
	}

}
