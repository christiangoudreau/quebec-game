/**
 * Copyright 2011 Philippe Beaudoin
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

package com.philbeaudoin.quebec.shared;

/**
 * Information about a player. This information never changes during the game. See also
 * {@link PlayerState}.
 *
 * @author Philippe Beaudoin
 */
public class Player {

  public final String name;
  public final PlayerColor color;

  public Player(PlayerColor color, String name) {
    assert color != PlayerColor.NONE && color != PlayerColor.NEUTRAL;
    this.name = name;
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public PlayerColor getColor() {
    return color;
  }
}