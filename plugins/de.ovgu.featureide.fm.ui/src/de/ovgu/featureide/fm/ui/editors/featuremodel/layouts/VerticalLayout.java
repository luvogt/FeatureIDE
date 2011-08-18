 package de.ovgu.featureide.fm.ui.editors.featuremodel.layouts;
/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2011  FeatureIDE Team, University of Magdeburg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */

import java.util.LinkedList;

import org.eclipse.draw2d.geometry.Point;

import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.fm.ui.editors.FeatureUIHelper;

/**
 * ordering features from left to right without any intersections or overlapping
 * 
 * @author David Halm
 * @author Patrick Sulkowski
 */
public class VerticalLayout extends FeatureDiagramLayoutManager{
	
	private LinkedList<LinkedList<LayoutableFeature>> levelList = new LinkedList<LinkedList<LayoutableFeature>>();
	private int highestLevel = 0;
	private int height = 0;
	private int yOffset = 0;
	private LinkedList<Integer> positionsX = new LinkedList<Integer>();
	private LinkedList<Integer> positionsY = new LinkedList<Integer>();
	private LinkedList<Integer> maxFeatureWidthOnLevel = new LinkedList<Integer>();
	private int childlessNum = 0;
	
	public void layoutFeatureModel(FeatureModel featureModel) {
		
		LayoutableFeature root = new LayoutableFeature(featureModel.getRoot(), showHidden);
		
		createLevelList(root, 0);
		calculateLevelXPositions();	
		getYcenterForChildlessFeatures();
		centerChildren(root,0);
		centerOther(highestLevel);
		layout(yOffset, featureModel.getConstraints());
	}
	
	/**
	 * calculates positions for each level (horizontal)
	 * 
	 */
	private void calculateLevelXPositions(){	
		int width = 0;
		for(int i = 0; i <= highestLevel; i++){
			width += maxFeatureWidthOnLevel.get(i)+FEATURE_SPACE_Y;
		}
		width -= FEATURE_SPACE_Y;
		positionsX.add((controlWidth - width) /2);
		for(int i = 1; i <= highestLevel; i++){
			positionsX.add(positionsX.get(i-1)+maxFeatureWidthOnLevel.get(i-1)+FEATURE_SPACE_Y);
		}
	}
	
	/**
	 * creates lists of features for every level
	 */
	private void createLevelList(LayoutableFeature feature, int level){
		if (feature.getFeature() == null)
			return;
		if(level > highestLevel){
			highestLevel = level;
		}
		if(levelList.size()-1 >= level){
			levelList.get(level).add(feature);
			if(maxFeatureWidthOnLevel.get(level) < FeatureUIHelper.getSize(feature.getFeature()).width){
				maxFeatureWidthOnLevel.set(level, FeatureUIHelper.getSize(feature.getFeature()).width);
			}
		} else {
			LinkedList<LayoutableFeature> subLevelList = new LinkedList<LayoutableFeature>();
			subLevelList.add(feature);
			levelList.add(subLevelList);
			maxFeatureWidthOnLevel.add(FeatureUIHelper.getSize(feature.getFeature()).width);
		}
		if(!feature.hasChildren()){
			positionsY.add(height);
			height += FeatureUIHelper.getSize(feature.getFeature()).getCopy().height+FEATURE_SPACE_X;
		}
		for(LayoutableFeature next : feature.getChildren()){
			createLevelList(next,level+1);
		}
	}
	
	/**
	 * calculates positions for each child (vertical)
	 * 
	 */
	private void getYcenterForChildlessFeatures(){
		height -= FEATURE_SPACE_X;
		for(int i = 0; i < positionsY.size(); i++){
			int newPos =  positionsY.get(i)+(controlHeight/2-height/2);
			positionsY.set(i,newPos);
		}
	}
	
	/**
	 * positions of features that have no more children are set
	 * 
	 */
	private void centerChildren(LayoutableFeature feature, int level){
			
		if(feature.hasChildren()){
			for(LayoutableFeature child : feature.getChildren()){
				centerChildren(child, level+1);
			}
		} else {
			FeatureUIHelper.setLocation(feature.getFeature(), new Point (positionsX.get(level),
					positionsY.get(childlessNum)));
			childlessNum++;
			yOffset = FeatureUIHelper.getLocation(feature.getFeature()).getCopy().y
					+ FeatureUIHelper.getSize(feature.getFeature()).getCopy().height
					+ FEATURE_SPACE_X;
		}
		
	}
	
	/**
	 * positions of features that have children are now set from right to left (for each level)
	 * (centered by their children's positions
	 * 
	 */	
	private void centerOther(int level) {
		for(int i = 0; i < levelList.get(level).size(); i++){
			LayoutableFeature feature = levelList.get(level).get(i);
			
			if(feature.hasChildren()){				
				int yPos = (FeatureUIHelper.getLocation(feature.getFirstChild().getFeature()).y
						+ FeatureUIHelper.getLocation(feature.getLastChild().getFeature()).y) / 2;
				FeatureUIHelper.setLocation(feature.getFeature(), new Point (positionsX.get(level),yPos));
			}
			
		}
		if(level != 0){
			centerOther(level-1);
		}
	}
		
}
