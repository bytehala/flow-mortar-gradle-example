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
import bytehala.flowmortarexample.model.Chat;
import bytehala.flowmortarexample.model.Chats;
import bytehala.flowmortarexample.mortarscreen.WithModule;
import bytehala.flowmortarexample.view.ChatListView;
import dagger.Provides;
import flow.Flow;
import flow.Layout;
import flow.Path;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import mortar.ViewPresenter;

@Layout(R.layout.chat_list_view) @WithModule(ChatListScreen.Module.class)
public class ChatListScreen extends Path {

  @dagger.Module(injects = ChatListView.class, addsTo = RootModule.class)
  public static class Module {
    @Provides List<Chat> provideConversations(Chats chats) {
      return chats.getAll();
    }
  }

  @Singleton
  public static class Presenter extends ViewPresenter<ChatListView> {
    private final List<Chat> chats;

    @Inject Presenter(List<Chat> chats) {
      this.chats = chats;
    }

    @Override public void onLoad(Bundle savedInstanceState) {
      super.onLoad(savedInstanceState);
      if (!hasView()) return;
      getView().showConversations(chats);
    }

    public void onConversationSelected(int position) {
      Flow.get(getView()).goTo(new ChatScreen(position));
    }
  }
}
