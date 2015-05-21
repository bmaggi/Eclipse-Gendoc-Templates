/*****************************************************************************
 * Copyright (c) 2013 CEA LIST.
 * 
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 
 * Sebastien Poissonnet (CEA LIST) sebastien.poissonnet@cea.fr
 *
 *****************************************************************************/

package org.eclipse.papyrus.sysml.gendoc.queries.common;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.util.UMLUtil;
import org.eclipse.papyrus.sysml.portandflows.FlowPort;
import org.eclipse.papyrus.sysml.portandflows.FlowDirection;


public class SysML_Queries {
	static public String displayNamedElement(NamedElement e){
		String s="";
		if(e instanceof org.eclipse.uml2.uml.Port){
			//afficher port
			for(Stereotype st : e.getAppliedStereotypes()){
				s+="Flow port ";
				s+=e.getValue(st, "direction");
			}
			if(s.isEmpty()){
				s+="Port";
			}
			s+=" : ";
			s+=((Property)e).getType().getName();
			s+=" : ";
			s+=e.getName();
		}else if(((Property)e).getType() instanceof org.eclipse.uml2.uml.DataType){
			//afficher attribut
			s+="Property : ";
			s+=e.getName() + " : ";
			s+=((Property)e).getType().getName();
		}else if(e instanceof org.eclipse.uml2.uml.Connector){
			//afficher connecteur
		}
		else if(((Property) e).getType() instanceof org.eclipse.uml2.uml.Class){
			//afficher sous systeme
			s+="Part : ";
			s+=e.getName();
			s+=" : ";
			s+=((Property)e).getType().getName();
		}
		return s;
	}
	
	static public String displayClass(NamedElement e){
		String s="";
		
		//commentaire associe
		if(!e.getOwnedComments().isEmpty()){
			s+="Comment : ";
			for(Comment com : e.getOwnedComments()){
				s+=com.getBody();
				s+="\n";
			}
		}
		//Ports
		for(NamedElement ne : getPort(e)){
			for(Stereotype st : ne.getAppliedStereotypes()){
				s+="Flow port ";
				s+=ne.getValue(st, "direction");
			}
			if(ne.getAppliedStereotypes().isEmpty()){
				s+="Port";
			}
			s+=" : ";
			s+=ne.getName();
			s+="\n";
		}
		//sous systemes
		for(NamedElement ne : getAttributs((Classifier)e)){
			s+="Part : ";
			s+=ne.getName();
			if(((Property)ne).getType()!=null){
				s+=" : ";
				s+=((Property)ne).getType().getName();
			}
			s+="\n";
		}
		for(NamedElement ne : ((Classifier)e).getAllOperations()){
			s+="Operation : ";
			s+=ne.getName();
			s+="\n";
		}
		
		return s;
	}
	
	static public String displayComment(NamedElement e){
		String s="";
		for(Comment com : e.getOwnedComments()){
			s+=com.getBody();
			s+="\n";
		}
		return s;
	}
	
	static public String displayPart(NamedElement e){
		String s="";
		for(NamedElement ne : getParts((Classifier)e)){
			s+="Part : ";
			s+=ne.getName();
			if(((Property)ne).getType()!=null){
				s+=" : ";
				s+=((Property)ne).getType().getName();
			}
			s+="\n";
		}
		return s;
	}
	
	static public String displayPort(NamedElement e){
		String s="";
		for(NamedElement ne : getPort(e)){
			//flow port in
			if(!ne.getAppliedStereotypes().isEmpty()){
				FlowPort f = UMLUtil.getStereotypeApplication(ne, FlowPort.class);
				if(f.getDirection().equals(FlowDirection.IN)){
					s+="Flow port in : ";
					s+=" : ";
					s+=((org.eclipse.uml2.uml.Port)ne).getType().getName();
					s+=ne.getName();
					s+="\n";
				}
			}
		}
		for(NamedElement ne : getPort(e)){
			//flow port out
			if(!ne.getAppliedStereotypes().isEmpty()){
				FlowPort f = UMLUtil.getStereotypeApplication(ne, FlowPort.class);
				if(f.getDirection().equals(FlowDirection.OUT)){
					s+="Flow port out : ";
					s+=" : ";
					s+=((org.eclipse.uml2.uml.Port)ne).getType().getName();
					s+=ne.getName();
					s+="\n";
				}
			}
		}
		for(NamedElement ne : getPort(e)){
			//flow port inout
			if(!ne.getAppliedStereotypes().isEmpty()){
				FlowPort f = UMLUtil.getStereotypeApplication(ne, FlowPort.class);
				if(f.getDirection().equals(FlowDirection.INOUT)){
					s+="Flow port inout : ";
					s+=ne.getName();
					s+=" : ";
					s+=((org.eclipse.uml2.uml.Port)ne).getType().getName();
					s+="\n";
				}
			}
		}
		for(NamedElement ne : getPort(e)){
			//other
			if(ne.getAppliedStereotypes().isEmpty()){
				s+="Port";
				s+=" : ";
				s+=ne.getName();
				s+=" : ";
				s+=((org.eclipse.uml2.uml.Port)ne).getType().getName();
				s+="\n";
			}
		}
		return s;
	}
	
	static public String displayAttribute(NamedElement e){
		String s="";
		for(NamedElement ne : getAttributs((Classifier)e)){
			s+=ne.getName();
			s+="\n";
		}
		return s;
	}
	
	static public String displayOperation(NamedElement e){
		String s="";
		for(NamedElement ne : getOperations((Classifier)e)){
			s+=ne.getName();
			s+="\n";
		}
		return s;
	}
	
	static public boolean isInputPort(NamedElement e){
		boolean isInput=false;
		//flow port in
		if(!e.getAppliedStereotypes().isEmpty()){
			FlowPort f = UMLUtil.getStereotypeApplication(e, FlowPort.class);
			isInput=f.getDirection().equals(FlowDirection.IN);
		}
		return isInput;
	}
	
	static public boolean isOutputPort(NamedElement e){
		boolean isOutput=false;
		//flow port in
		if(!e.getAppliedStereotypes().isEmpty()){
			FlowPort f = UMLUtil.getStereotypeApplication(e, FlowPort.class);
			isOutput=f.getDirection().equals(FlowDirection.OUT);
		}
		return isOutput;
	}
	
	static public boolean isInOutPort(NamedElement e){
		boolean isInOut=false;
		//flow port in
		if(!e.getAppliedStereotypes().isEmpty()){
			FlowPort f = UMLUtil.getStereotypeApplication(e, FlowPort.class);
			isInOut=f.getDirection().equals(FlowDirection.INOUT);
		}
		return isInOut;
	}
	
	static public List<NamedElement> getPackage(Model model){
		LinkedList<NamedElement> found_elts = new LinkedList<NamedElement>();
		for (Element ne : model.getOwnedElements()) {
			if (ne instanceof NamedElement) {
				if (ne instanceof org.eclipse.uml2.uml.Package) {
					found_elts.add((NamedElement)ne);
				}
			} else if (ne instanceof Model) {
				found_elts.addAll(getPackage((Model)ne));
			}
			getPackage(ne,found_elts);
		}
		
		return found_elts;
	}
	
	private static void getPackage(Element parent_elt, LinkedList<NamedElement> target_list) {
		
		for (Element ne : parent_elt.getOwnedElements()) {
			if (ne instanceof org.eclipse.uml2.uml.Package) {
				target_list.add((NamedElement)ne);
			}
			getPackage(ne,target_list);
		}
	}
	
	static public List<NamedElement> getClass(NamedElement p) {
		LinkedList<NamedElement> found_elts = new LinkedList<NamedElement>();
		for (Element ne : p.getOwnedElements()) {
			if (ne instanceof NamedElement) {
				if (ne instanceof org.eclipse.uml2.uml.Class) {
					found_elts.add((NamedElement)ne);
				}
			}
		}
		return found_elts;
	}
	
	static public List<NamedElement> getPort(NamedElement c){
		LinkedList<NamedElement> sub_syst_found = new LinkedList<NamedElement>();
		for(Element ne : c.getOwnedElements()){
			if(ne instanceof org.eclipse.uml2.uml.Port){
				sub_syst_found.add((NamedElement)ne);
			}
		}
		return sub_syst_found;
	}
	
	static public List<NamedElement> getParts(Classifier c){
		LinkedList<NamedElement> sub_syst_found = new LinkedList<NamedElement>();
		for(Property prop : c.getAllAttributes()){
			if(prop.getType() instanceof org.eclipse.uml2.uml.Class){
				sub_syst_found.add((NamedElement)prop);
			}
		}
		return sub_syst_found;
	}
	
	static public List<NamedElement> getAttributs(Classifier c){
		LinkedList<NamedElement> sub_syst_found = new LinkedList<NamedElement>();
		for(Property prop : c.getAllAttributes()){
			if(!(prop instanceof Port) && !(prop.getType() instanceof org.eclipse.uml2.uml.Class)){
				sub_syst_found.add((NamedElement)prop);
			}
		}
		return sub_syst_found;
	}
	
	static public List<NamedElement> getOperations(Classifier c){
		LinkedList<NamedElement> sub_syst_found = new LinkedList<NamedElement>();
		for(Operation op : c.getAllOperations()){
			sub_syst_found.add((NamedElement)op);
		}
		return sub_syst_found;
	}
	
	static public List<NamedElement> getPackage_root(Model model){
		LinkedList<NamedElement> found_elts = new LinkedList<NamedElement>();
		for (Element ne : model.getOwnedElements()) {
			if (ne instanceof org.eclipse.uml2.uml.Package) {
				found_elts.add((NamedElement)ne);
			}
		}
		
		return found_elts;
	}
	
	static public List<NamedElement> getBlock_root(Model model){
		LinkedList<NamedElement> found_elts = new LinkedList<NamedElement>();
		for (Element ne : model.getOwnedElements()) {
			if (ne instanceof org.eclipse.uml2.uml.Class) {
				found_elts.add((NamedElement)ne);
			}
		}
		
		return found_elts;
	}
	
	static public List<NamedElement> getChilds(Model model){
		
		LinkedList<NamedElement> found_elts = new LinkedList<NamedElement>();
		for (Element ne : model.getOwnedElements()) {
			if ((ne instanceof org.eclipse.uml2.uml.Class || ne instanceof org.eclipse.uml2.uml.Package) && !is_in(found_elts,(NamedElement)ne)) {
				found_elts.add((NamedElement)ne);
			}
		}
		
		return found_elts;
	}
	
	static public boolean is_in(LinkedList<NamedElement> found_elts, NamedElement n){
		boolean ok=false;
		for (NamedElement ne : found_elts) {
			if(ne.getQualifiedName().equals(n.getQualifiedName())){
				ok=true;
			}
		}
		
		return ok;
	}
	
	static public String displayPackage(NamedElement p){
		String s="";
		s+="Package : ";
		s+=p.getName();
		s+="\n";
		for(NamedElement ne : getChilds(p)){
			if (ne instanceof org.eclipse.uml2.uml.Class){
				s+="\t" + displayBlock(ne);
			}
			else if(ne instanceof org.eclipse.uml2.uml.Package){
				s+="\t" + displayPackage(ne);
			}
		}
		return s;
	}
	
	static public String displayBlock(NamedElement b){
		String s="";
		s+="Block : ";
		s+=b.getName();
		s+="\n";
		for(NamedElement ne : getChilds(b)){
			if (ne instanceof org.eclipse.uml2.uml.Class){
				s+="\t" + displayBlock(ne);
			}
			else if(ne instanceof org.eclipse.uml2.uml.Package){
				s+="\t" + displayPackage(ne);
			}
		}
		return s;
	}
	
	static public List<NamedElement> getChilds(NamedElement n){
		LinkedList<NamedElement> found_elts = new LinkedList<NamedElement>();
		for (Element ne : n.getOwnedElements()) {
			if (ne instanceof org.eclipse.uml2.uml.Class || ne instanceof org.eclipse.uml2.uml.Package) {
				found_elts.add((NamedElement)ne);
			}
		}
		
		return found_elts;
	}
	
	static public String displayModel(Model model){
		String s="";
		for(NamedElement nE : getChilds(model)){
			if(nE instanceof org.eclipse.uml2.uml.Package){
				s+=displayPackage(nE);
			}else{
				s+=displayBlock(nE);
			}
		}
		
		s+="\n";
		return s;
	}
}

