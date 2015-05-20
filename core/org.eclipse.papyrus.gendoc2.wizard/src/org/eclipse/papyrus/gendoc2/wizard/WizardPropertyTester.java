/*****************************************************************************
 * Copyright (c) 2011 Atos Origin.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Barthelemy HABA (Atos Origin) barthelemy.haba@atosorigin.com - 
 *
 *****************************************************************************/
package org.eclipse.papyrus.gendoc2.wizard;

import org.eclipse.core.expressions.PropertyTester;

public class WizardPropertyTester extends PropertyTester
{

    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
            return Utils.matches(receiver);
    }

}
