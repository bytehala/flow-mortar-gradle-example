/*
 * Copyright 2014 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bytehala.flowmortarexample.core;

import android.app.Application;

import dagger.ObjectGraph;
import mortar.MortarScope;
import mortar.dagger1support.ObjectGraphService;

public class DemoApp extends Application {
    private ObjectGraph globalGraph;
    private MortarScope rootScope;

    @Override
    public void onCreate() {
        super.onCreate();

        globalGraph = ObjectGraph.create(new RootModule());
    }

    public ObjectGraph getGlobalGraph() {
        return globalGraph;
    }

    @Override
    public Object getSystemService(String name) {
        if (rootScope == null) {
            rootScope = MortarScope.buildRootScope()
                    .withService(ObjectGraphService.SERVICE_NAME, ObjectGraph.create(new RootModule()))
                    .build();
        }

        return rootScope.hasService(name) ? rootScope.getService(name) : super.getSystemService(name);
    }
}
