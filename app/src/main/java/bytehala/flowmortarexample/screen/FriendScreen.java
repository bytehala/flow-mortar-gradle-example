/*
 * Copyright 2013 Square Inc.
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
package bytehala.flowmortarexample.screen;

import android.os.Bundle;

import bytehala.flowmortarexample.R;
import bytehala.flowmortarexample.core.RootModule;
import bytehala.flowmortarexample.model.Chats;
import bytehala.flowmortarexample.model.User;
import bytehala.flowmortarexample.mortarscreen.WithModule;
import bytehala.flowmortarexample.view.FriendView;
import dagger.Provides;
import flow.HasParent;
import flow.Layout;
import flow.Path;
import javax.inject.Inject;
import javax.inject.Singleton;
import mortar.ViewPresenter;

@Layout(R.layout.friend_view) @WithModule(FriendScreen.Module.class)
public class FriendScreen extends Path implements HasParent {
  private final int index;

  public FriendScreen(int index) {
    this.index = index;
  }

  @Override public FriendListScreen getParent() {
    return new FriendListScreen();
  }

  @dagger.Module(injects = FriendView.class, addsTo = RootModule.class)
  public class Module {
    @Provides
    User provideFriend(Chats chats) {
      return chats.getFriend(index);
    }
  }

  @Singleton
  public static class Presenter extends ViewPresenter<FriendView> {
    private final User friend;

    @Inject Presenter(User friend) {
      this.friend = friend;
    }

    @Override public void onLoad(Bundle savedInstanceState) {
      super.onLoad(savedInstanceState);
      if (!hasView()) return;
      getView().setText(friend.name);
    }
  }
}
