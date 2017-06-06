package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public abstract class Expression extends ASTNode {
	TypeName identType;
	protected Expression(Token firstToken) {
		super(firstToken);
	}
	
	public void setIdentType(TypeName identType){
		this.identType=identType;
	}
	
	public TypeName getIdentType(){
		return this.identType;
	}

	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}
