package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cop5556sp17.AST.Dec;


public class SymbolTable {
	
	Stack<Integer> st=null;
	Map<String,LinkedList<DecDetails>> map= null;
	int currentScope=0;
	int nextScope=1;
	
	class DecDetails{
		int scope;
		Dec dec;
		public DecDetails(int currentScope, Dec dec) {
			this.scope=currentScope;
			this.dec=dec;
		}
		public int getScope() {
			return this.scope;
		}
		public void setScope(int scope) {
			this.scope = scope;
		}
		public Dec getDec() {
			return dec;
		}
		public void setDec(Dec dec) {
			this.dec = dec;
		}
		
		@Override
		public String toString(){
			return "scopeval: "+ getScope();
		}
	}

	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		currentScope = nextScope++;
		 st.push(currentScope);
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		st.pop();
		if(!st.isEmpty()){
			currentScope = st.peek();
		}
	}
	
	public boolean insert(String ident, Dec dec){
		DecDetails decItem=new DecDetails(currentScope,dec);
		LinkedList<DecDetails> list=new LinkedList<DecDetails>();
		if(map.containsKey(ident)){
			list=map.get(ident);
			Iterator<DecDetails> itr=list.iterator();
			while(itr.hasNext()){
				DecDetails decVal=itr.next();
				if(decVal.getScope()==currentScope){
					return false;
				}
			}
			list.addFirst(decItem);
			map.put(ident, list);
		}
		else{
			list.addFirst(decItem);
			map.put(ident, list);
		}
		return true;
	}
	
	public Dec lookup(String ident){
		boolean flag=false;
		LinkedList<DecDetails> list=new LinkedList<DecDetails>();
		Dec decValue = null;
		if(map.containsKey(ident)){
			list=map.get(ident);
			Iterator<DecDetails> itr=list.iterator();
			while(itr.hasNext()){
				if(flag==true){
					break;
				}
				DecDetails decItem=itr.next();
				if(decItem.getScope()==currentScope){
					decValue=decItem.getDec();
					break;
				}
				else{
					int count=st.size()-1;
					while(count>=0){
						if(st.get(count)==decItem.getScope()){
							decValue=decItem.getDec();
							flag=true;
							break;
					    }
						else{
							count--;
						}
					}
				}
			}
		}
		return decValue;
	}
		
	public SymbolTable() {
		st=new Stack<Integer>();
		st.push(0);
		map= new HashMap<String,LinkedList<DecDetails>>();
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return "";
	}
	
	


}
