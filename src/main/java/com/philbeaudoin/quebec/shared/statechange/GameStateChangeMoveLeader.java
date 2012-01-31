/**
 * Copyright 2012 Philippe Beaudoin
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

package com.philbeaudoin.quebec.shared.statechange;

import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * A change of the game state obtained by moving a leader card from one location to another.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeMoveLeader implements GameStateChange {

  private final LeaderDestination from;
  private final LeaderDestination to;

  public GameStateChangeMoveLeader(LeaderDestination from, LeaderDestination to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public void apply(GameState gameState) {
    from.removeFrom(gameState);
    to.addTo(gameState);
  }

  @Override
  public void accept(GameStateChangeVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Access the leader card destination from which the card starts.
   * @return The origin cube destination.
   */
  public LeaderDestination getFrom() {
    return from;
  }

  /**
   * Access the leader card destination to which the card goes.
   * @return The final cube destination.
   */
  public LeaderDestination getTo() {
    return to;
  }
}
