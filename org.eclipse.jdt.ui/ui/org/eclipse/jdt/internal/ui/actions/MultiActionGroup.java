/*******************************************************************************
 * Copyright (c) 2000, 2002 International Business Machines Corp. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v0.5 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.jdt.internal.ui.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.ui.actions.ActionGroup;

import org.eclipse.jdt.internal.ui.JavaPlugin;

/**
 * A MultiActionGroup will display a list of IActions in a menu by transforming them
 * into MenuItems. The list of labels given will be what is displayed in the ViewMenu for 
 * the corresponding action (the action at the same position in the action array).
 * The actions are currently implemented as state based
 * so that after an action is executed the label will have a selection check.
 * 
 * @since 2.1
 */
public class MultiActionGroup extends ActionGroup {
	
	private IAction[] fActions; 
	
	private int fCurrentSelection;
	private MenuItem[] fItems;

	
	/**
	 * Creates a new action group with a given set of actions.
	 * 
	 * @param actions			the actions for this multi group
	 * @param currentSelection	decides which action is selected in the menu on start up.
	 * 							Denotes the location in the actions array of the current
	 * 							selected state. It cannot be null.
	 */
	public MultiActionGroup(IAction[] actions, int currentSelection) {
		super();
		
		fCurrentSelection= currentSelection;
		fActions= actions;
	}

	/**
	 * Add the actions to the given menu manager.
	 */
	protected void addActions(IMenuManager viewMenu) {

		viewMenu.add(new Separator());
		fItems= new MenuItem[fActions.length];

		for (int i= 0; i < fActions.length; i++) {
			final int j= i;

			viewMenu.add(new ContributionItem() {

				public void fill(Menu menu, int index) {
					MenuItem mi= new MenuItem(menu, SWT.CHECK, index);
					ImageDescriptor d= fActions[j].getImageDescriptor();
					mi.setImage(JavaPlugin.getImageDescriptorRegistry().get(d));
					fItems[j]= mi;
					mi.setText(fActions[j].getText());
					mi.setSelection(fCurrentSelection == j);
					mi.addSelectionListener(new SelectionAdapter() {

						public void widgetSelected(SelectionEvent e) {
							if (fCurrentSelection == j) {
								fItems[fCurrentSelection].setSelection(true);
								return;
							}
							fActions[j].run();

							// Update checked state
							fItems[fCurrentSelection].setSelection(false);
							fCurrentSelection= j;
							fItems[fCurrentSelection].setSelection(true);
						}

					});
				}
				public boolean isDynamic() {
					return false;
				}
			});
		}
	}
}
