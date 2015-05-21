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

package org.eclipse.papyrus.robotml.gendoc.queries.common;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.RobotML.DataFlowDirectionKind;
import org.eclipse.papyrus.RobotML.DataFlowPort;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.util.UMLUtil;

public class RobotML_Queries {

	
	static public Boolean hasStereotype(org.eclipse.uml2.uml.Class cl, Class stereotype) {
		for(EObject stereoApplication : cl.getStereotypeApplications()) {
			// check whether the stereotype is an instance of the passed parameter clazz
			if(stereotype.isInstance(stereoApplication)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return true if a given class has specified stereotype.
	 */
	static public Boolean hasStereotype(org.eclipse.uml2.uml.Class cl, String s)
	{
		for (Stereotype st : cl.getAppliedStereotypes()) {
			if(st.getName().equals(s)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Recursive retrieve of all elements with a given stereotype in the model.
	 * @param model Model to search in.
	 * @param stereotype The stereotype name (e.g. "Sensor", "Actuator", "SubSystem"...)
	 * @return A list of NamedElements having the specified stereotype.
	 */
	static public List<NamedElement> getElementsWithStereotype(Model model, String stereotype) {		
		LinkedList<NamedElement> found_elts = new LinkedList<NamedElement>();
		for (Element ne : model.getOwnedElements()) {
			if (ne instanceof NamedElement) {
				if (ne instanceof org.eclipse.uml2.uml.Class) {					
					if (hasStereotype((org.eclipse.uml2.uml.Class)ne, stereotype)) {
						found_elts.add((NamedElement)ne);
					}
				}
			} else if (ne instanceof Model) {
				found_elts.addAll(getElementsWithStereotype((Model)ne,stereotype));
			}
			getElementsWithStereotype(ne,stereotype,found_elts);
		}
		return found_elts;
	}
	
	/**
	 * Same as getElementsWithStereotype(Model model...) searches in an Element members, not in a Model members.
	 * This function is private and used by getElementsWithStereotype(Model model...)
	 * @param parent_elt
	 * @param stereotype
	 * @param target_list
	 */
	static private void getElementsWithStereotype(Element parent_elt, String stereotype, LinkedList<NamedElement> target_list) {
		for (Element ne : parent_elt.getOwnedElements()) {
			if (ne instanceof NamedElement) {
				if (ne instanceof org.eclipse.uml2.uml.Class) {
					if(hasStereotype((org.eclipse.uml2.uml.Class)ne, stereotype)){
						target_list.add((NamedElement)ne);
					}					
				}
				getElementsWithStereotype(ne,stereotype,target_list);
			}
		}
		return;
	}
	
	static public List<NamedElement> getSensorSystems(Model model) {
		return getElementsWithStereotype(model, "SensorSystem");
	}
	static public List<NamedElement> getActuatorSystems(Model model) {
		return getElementsWithStereotype(model, "ActuatorSystem");
	}
	static public List<NamedElement> getRobots(Model model) {		
		return getElementsWithStereotype(model, "Robot");
	}
	static public List<NamedElement> getSystems(Model model) {		
		return getElementsWithStereotype(model, "System");
	}
	static public List<NamedElement> getRoboticSystems(Model model) {		
		return getElementsWithStereotype(model, "RoboticSystem");
	}
	static public List<NamedElement> getHardwares(Model model) {		
		return getElementsWithStereotype(model, "Hardware");
	}
	static public List<NamedElement> getSoftwares(Model model) {		
		return getElementsWithStereotype(model, "Software");
	}
	static public List<NamedElement> getSensorDrivers(Model model) {		
		return getElementsWithStereotype(model, "SensorDriver");
	}
	static public List<NamedElement> getSimulatedSystems(Model model) {		
		return getElementsWithStereotype(model, "SimulatedSystem");
	}
	static public List<NamedElement> getWheelSystems(Model model) {		
		return getElementsWithStereotype(model, "WheelSystem");
	}
	static public List<NamedElement> getEngineSystems(Model model) {		
		return getElementsWithStereotype(model, "EngineSystem");
	}
	static public List<NamedElement> getImageSensorSystems(Model model) {		
		return getElementsWithStereotype(model, "ImageSensorSystem");
	}
	static public List<NamedElement> getObjectTrackingSensorSystems(Model model) {		
		return getElementsWithStereotype(model, "ObjectTrackingSensorSystem");
	}
	static public List<NamedElement> getObjectDetectionSensorSystems(Model model) {		
		return getElementsWithStereotype(model, "ObjectDetectionSensorSystem");
	}
	static public List<NamedElement> getLocalizationSensorSystems(Model model) {		
		return getElementsWithStereotype(model, "LocalizationSensorSystem");
	}
	static public List<NamedElement> getGPSSystems(Model model) {		
		return getElementsWithStereotype(model, "GPSSystem");
	}
	static public List<NamedElement> getLidarSystems(Model model) {		
		return getElementsWithStereotype(model, "LidarSystem");
	}
	static public List<NamedElement> getCameraSystems(Model model) {		
		return getElementsWithStereotype(model, "CameraSystem");
	}
	
	static public Integer countSensorSystems(Model model){
		return getSensorSystems(model).size();
	}
	static public Integer countActuatorSystems(Model model) {
		return getElementsWithStereotype(model, "ActuatorSystem").size();
	}
	static public Integer countRobots(Model model) {		
		return getElementsWithStereotype(model, "Robot").size();
	}
	static public Integer countSystems(Model model) {		
		return getElementsWithStereotype(model, "System").size();
	}
	static public Integer countRoboticSystems(Model model) {		
		return getElementsWithStereotype(model, "RoboticSystem").size();
	}
	static public Integer countHardwares(Model model) {		
		return getElementsWithStereotype(model, "Hardware").size();
	}
	static public Integer countSoftwares(Model model) {		
		return getElementsWithStereotype(model, "Software").size();
	}
	static public Integer countSensorDrivers(Model model) {		
		return getElementsWithStereotype(model, "SensorDriver").size();
	}
	static public Integer countSimulatedSystems(Model model) {		
		return getElementsWithStereotype(model, "SimulatedSystem").size();
	}
	static public Integer countWheelSystems(Model model) {		
		return getElementsWithStereotype(model, "WheelSystem").size();
	}
	static public Integer countEngineSystems(Model model) {		
		return getElementsWithStereotype(model, "EngineSystem").size();
	}
	static public Integer countImageSensorSystems(Model model) {		
		return getElementsWithStereotype(model, "ImageSensorSystem").size();
	}
	static public Integer countObjectTrackingSensorSystems(Model model) {		
		return getElementsWithStereotype(model, "ObjectTrackingSensorSystem").size();
	}
	static public Integer countObjectDetectionSensorSystems(Model model) {		
		return getElementsWithStereotype(model, "ObjectDetectionSensorSystem").size();
	}
	static public Integer countLocalizationSensorSystems(Model model) {		
		return getElementsWithStereotype(model, "LocalizationSensorSystem").size();
	}
	static public Integer countGPSSystems(Model model) {		
		return getElementsWithStereotype(model, "GPSSystem").size();
	}
	static public Integer countLidarSystems(Model model) {		
		return getElementsWithStereotype(model, "LidarSystem").size();
	}
	static public Integer countCameraSystems(Model model) {		
		return getElementsWithStereotype(model, "CameraSystem").size();
	}
	
	static public String displayClass(Model model){
		String s="";
		for(NamedElement nE : getPackage(model)){
			s+="Package : ";
			s+=nE.getName();
			s+="\n";
			for(NamedElement e : getClass(nE)){
				//nom de la class
				s+="\t-";
				s+=e.getName();
				//stereotypes appliques
				if(!e.getAppliedStereotypes().isEmpty()){
					s+=" : ";
					for(Stereotype ste : e.getAppliedStereotypes()){
						s+=ste.getName();
						s+=" ";
					}
				}
				s+="\n";
				//commentaire associe
				if(!e.getOwnedComments().isEmpty()){
					s+="\t\tComment : ";
					for(Comment com : e.getOwnedComments()){
						s+=com.getBody();
						//s+="\n";
						s+=(char)13;
					}
				}
				//Ports
				for(NamedElement ne : getPort(e)){
					s+="\t\tPort ";
					for(Stereotype st : ne.getAppliedStereotypes())
						s+=ne.getValue(st, "direction");
					s+=" : ";
					s+=ne.getName();
					s+="\n";
				}
				//sous systemes
				for(NamedElement ne : getSubSystem((Classifier)e)){
					s+="\t\tSub System : ";
					s+=ne.getName();
					s+=" : ";
					s+=((Property)ne).getType().getName();
					s+="\n";
				}
			}
		}
		s+="\n";
		return s;
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
			if (ne instanceof NamedElement) {
				if (ne instanceof org.eclipse.uml2.uml.Package) {
					target_list.add((NamedElement)ne);					
				}
				getPackage(ne,target_list);
			}
		}
		return;
		
	}

	static public List<NamedElement> getClass(Model model) {
		LinkedList<NamedElement> found_elts = new LinkedList<NamedElement>();
		for (Element ne : model.getOwnedElements()) {
			if (ne instanceof NamedElement) {
				if (ne instanceof org.eclipse.uml2.uml.Class) {
					found_elts.add((NamedElement)ne);
				}
			}
		}
		return found_elts;
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
	
	static public List<NamedElement> getClass(Package p) {
		LinkedList<NamedElement> found_elts = new LinkedList<NamedElement>();
		for (Element ne : ((NamedElement)p).getOwnedElements()) {
			if (ne instanceof NamedElement) {
				if (ne instanceof org.eclipse.uml2.uml.Class) {
					found_elts.add((NamedElement)ne);
				}
			}
		}
		return found_elts;
	}
	

	static private void getClass(Element parent_elt, LinkedList<NamedElement> target_list) {
		for (Element ne : parent_elt.getOwnedElements()) {
			if (ne instanceof NamedElement) {
				if (ne instanceof org.eclipse.uml2.uml.Class) {
					target_list.add((NamedElement)ne);					
				}
				getClass(ne,target_list);
			}
		}
		return;
	}
	
	static public List<NamedElement> getSubSystem(Classifier c){
		LinkedList<NamedElement> sub_syst_found = new LinkedList<NamedElement>();
		for(Property prop : c.getAllAttributes()){
			if((prop.getType() instanceof Classifier) && !(prop instanceof Port) && !( prop.getType() instanceof DataType)){
				sub_syst_found.add((NamedElement)prop);
			}
		}
		return sub_syst_found;
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
	
	static public String displayClass(NamedElement e){
		String s="";
		//nom de la class
		s+="\t";
		s+=e.getName();
		//stereotypes appliques
		if(!e.getAppliedStereotypes().isEmpty()){
			s+=" : ";
			for(Stereotype ste : e.getAppliedStereotypes()){
				s+=ste.getName();
				s+=" ";
			}
		}
		s+="\n";
		//commentaire associe
		if(!e.getOwnedComments().isEmpty()){
			s+="\t\tComment : ";
			for(Comment com : e.getOwnedComments()){
				s+=com.getBody();
				s+="\n";
			}
		}
		//Ports
		for(NamedElement ne : getPort(e)){
			s+="\t\tPort ";
			for(Stereotype st : ne.getAppliedStereotypes())
				s+=ne.getValue(st, "direction");
			s+=" : ";
			s+=ne.getName();
			s+="\n";
		}
		//sous systemes
		for(NamedElement ne : getSubSystem((Classifier)e)){
			s+="\t\tSub System : ";
			s+=ne.getName();
			s+=" : ";
			s+=((Property)ne).getType().getName();
			s+="\n";
		}
		return s;
	}
	static public String displayNamedElement(NamedElement e){
		String s="";
		if(e instanceof org.eclipse.uml2.uml.Port){
			//afficher port
			s+="Port ";
			for(Stereotype st : e.getAppliedStereotypes())
				s+=e.getValue(st, "direction");
			s+=" : ";
			s+=((Property)e).getType().getName();
			s+=" : ";
			s+=e.getName();
		}else if(((Property)e).getType() instanceof org.eclipse.uml2.uml.DataType){
			//afficher attribut
			s+="DataType : ";
			s+=e.getName() + " : ";
			s+=((Property)e).getType().getName();
		}
		else if(((Property) e).getType() instanceof org.eclipse.uml2.uml.Class){
			//afficher sous systeme
			
			s+="Sub System : ";
			s+=e.getName();
			s+=" : ";
			s+=((Property)e).getType().getName();
		}
		return s;
	}
	
	static public boolean isInputPort(Port e){
		boolean isInput=false;
		//flow port in
		for (Stereotype st : e.getAppliedStereotypes()) {
			if(st.getName().equals("DataFlowPort")){
				
				isInput= ((EnumerationLiteral)e.getValue(st, "direction")).getName().equals("in");				
			}
		}
//		if(!e.getAppliedStereotypes().isEmpty()){
//			DataFlowPort f = UMLUtil.getStereotypeApplication(e, DataFlowPort.class);
//			if(f!=null){isInput=f.getDirection().equals( DataFlowDirectionKind.IN);}
//		}
		return isInput;
	}
	
	static public boolean isOutputPort(Port e){
		boolean isOutput=false;
		//flow port out
		for (Stereotype st : e.getAppliedStereotypes()) {
			if(st.getName().equals("DataFlowPort")){
				isOutput= ((EnumerationLiteral)e.getValue(st, "direction")).getName().equals("out");				
			}
		}
//		if(!e.getAppliedStereotypes().isEmpty()){
//			DataFlowPort f = UMLUtil.getStereotypeApplication(e, DataFlowPort.class);
//			if(f!=null){isOutput=f.getDirection().equals(DataFlowDirectionKind.OUT);}
//		}
		return isOutput;
	}
	
	static public boolean isInOutPort(Port e){
		boolean isInOut=false;
		//flow port inout
		for (Stereotype st : e.getAppliedStereotypes()) {
			if(st.getName().equals("DataFlowPort")){
				isInOut= ((EnumerationLiteral)e.getValue(st, "direction")).getName().equals("inout");				
			}
		}
//		if(!e.getAppliedStereotypes().isEmpty()){
//			DataFlowPort f = UMLUtil.getStereotypeApplication(e, DataFlowPort.class);
//			if(f!=null){isInOut=f.getDirection().equals(DataFlowDirectionKind.INOUT);}
//		}
		return isInOut;
	}
}
