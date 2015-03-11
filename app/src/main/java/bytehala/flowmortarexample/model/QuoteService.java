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
package bytehala.flowmortarexample.model;

import retrofit.http.GET;

public interface QuoteService {
  class Quote {
    public final String quote;

    public Quote(String quote) {
      this.quote = quote;
    }
  }

  @GET("/random?format=json&source=zippy&show_permalink=false&show_source=false") //
  Quote getQuote();
}
