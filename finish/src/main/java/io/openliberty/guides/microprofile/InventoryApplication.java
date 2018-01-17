// tag::comment[]
/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
 // end::comment[]
package io.openliberty.guides.microprofile;

import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.metrics.MetricRegistry;

@ApplicationPath("inventory")
public class InventoryApplication extends Application {

    public static MetricRegistry registry; 
    
    private Set<Class<?>> classes = new HashSet<Class<?>>();
    private Set<Object> singletons = new HashSet<Object>();

    public InventoryApplication() {
        try {
            singletons.add(new InventoryManager());
        } catch (Exception e) {
            System.out.println("damn");
            // generic exception handling
            e.printStackTrace();
        }
    }
    
    @Inject
    public void setup(MetricRegistry registry) {
        InventoryApplication.registry = registry;
        System.out.println("Test: " + registry);
        System.out.println("Test 2: " + InventoryApplication.registry);
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }    
}
