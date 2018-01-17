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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Timer;

import io.openliberty.guides.microprofile.util.InventoryUtil;
import io.openliberty.guides.microprofile.util.ReadyJson;

// tag::header[]
// tag::cdi-scope[]
@ApplicationScoped
// end::cdi-scope[]
public class InventoryManager {
// end::header[]

    private ConcurrentMap<String, JsonObject> inv = new ConcurrentHashMap<>();
    private MetricRegistry registry;
    private Timer testDataCalcTimer;

    public InventoryManager() {
        registry = InventoryApplication.registry;
        setupMetrics();
    }

    public void setupMetrics() {
        // Timer
        Metadata testDataCalcTimerMetadata = new Metadata(
           "testDataCalcTimer",                             // name
           "Test Data Calculation Time",                    // display name
           "Processing time to find the test data",         // description
           MetricType.TIMER,                                   // type
           MetricUnits.NANOSECONDS);                           // units
           System.out.println("Test 3: " + registry);
           testDataCalcTimer = registry.timer(testDataCalcTimerMetadata);
   }

    // tag::imp[]
    // tag::get[]
    public JsonObject get(String hostname) {
        // tag::method-contents[]
        JsonObject properties = inv.get(hostname);
        if (properties == null) {
            if (InventoryUtil.responseOk(hostname)) {
                properties = InventoryUtil.getProperties(hostname);
                this.add(hostname, properties);
            } else {
                return ReadyJson.SERVICE_UNREACHABLE.getJson();
            }
        }
        return properties;
        // end::method-contents[]
    }
    // end::get[]

    // tag::add[]
    public void add(String hostname, JsonObject systemProps) {
        // tag::method-contents[]
        inv.putIfAbsent(hostname, systemProps);
        // end::method-contents[]
    }
    // end::add[]

    // tag::list[]
    public JsonObject list() {
        // Start timing here
        Timer.Context context = testDataCalcTimer.time();
        // tag::method-contents[]
        JsonObjectBuilder systems = Json.createObjectBuilder();
        inv.forEach((host, props) -> {
            JsonObject systemProps = Json.createObjectBuilder()
                                              .add("os.name", props.getString("os.name"))
                                              .add("user.name", props.getString("user.name"))
                                              .build();
            systems.add(host, systemProps);
        });
        systems.add("hosts", systems);
        systems.add("total", inv.size());
        JsonObject result = systems.build();
        
        // Stop timing
        context.close();
        return result;
        // end::method-contents[]
    }
    // end::list[]
    // end::imp[]
}
