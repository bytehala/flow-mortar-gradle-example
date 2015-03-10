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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import bytehala.flowmortarexample.GsonParceler;
import bytehala.flowmortarexample.MainActivity;
import bytehala.flowmortarexample.android.ActionBarOwner;
import dagger.Module;
import dagger.Provides;
import flow.Parceler;

@Module(
        injects = {
                MainActivity.class
        },
        includes = ActionBarOwner.ActionBarModule.class,
        library = true)
public class RootModule {
//  @Provides List<Conversation> provideConversations() {
//    return SampleData.CONVERSATIONS;
//  }

//  @Provides List<User> provideFriends() {
//    return SampleData.FRIENDS;
//  }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    Parceler provideParcer(Gson gson) {
        return new GsonParceler(gson);
    }
}
