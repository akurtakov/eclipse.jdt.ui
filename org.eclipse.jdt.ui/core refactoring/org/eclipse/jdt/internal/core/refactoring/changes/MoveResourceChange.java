/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.internal.core.refactoring.changes;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.refactoring.base.IChange;

public class MoveResourceChange extends ResourceReorgChange {
	
	public MoveResourceChange(IResource res, IContainer dest, String newName){
		super(res, dest, newName);
	}
	
	public MoveResourceChange(IResource res, IContainer dest){
		this(res, dest, null);
	}
	
	/* non java-doc
	 * @see ResourceReorgChange#doPerform(IPath, IProgressMonitor)
	 */
	protected void doPerform(IPath path, IProgressMonitor pm) throws JavaModelException{
		try{
			pm.beginTask("moving", 1);
			getResource().move(path, false, new SubProgressMonitor(pm, 1));
		}catch(CoreException e){
			throw new JavaModelException(e);
		}	
	}
	
	/* non java-doc
	 * @see IChange#getUndoChange()
	 */
	public IChange getUndoChange() {
		return null;
	}

	/* non java-doc
	 * @see IChange#isUndoable()
	 */
	public boolean isUndoable(){
		return false;
	}

	/* non java-doc
	 * @see IChange#getName()
	 */
	public String getName() {
		return "Move resource:" + getResource().getFullPath() + " to: " + getDestination().getName();
	}
}

