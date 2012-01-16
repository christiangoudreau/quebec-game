package com.philbeaudoin.quebec.client.renderer;

import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.GameStateChange;

/**
 * Renders a {@link GameStateChange} into a scene graph. Classes implementing this interface are
 * usually generated using {@link ChangeRendererGenerator}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface ChangeRenderer {
  /**
   * Applies only the removals of a change in-place in the specified scene graph. Follow this call
   * by {@link #applyAdditions} to apply a complete state change.
   * @param renderer The renderer containing the scene graph on which to apply removals.
   */
  public void applyRemovals(GameStateRenderer renderer);

  /**
   * Applies only the removals of a change in-place in the specified scene graph. Precede this call
   * by {@link #applyRemovals} to apply a complete state change.
   * @param root The renderer containing the scene graph on which to apply additions.
   */
  public void applyAdditions(GameStateRenderer renderer);
  
  /**
   * Undoes only the removals of a change in-place in the specified scene graph. Precede this call
   * by {@link #undoAdditions} to undo a complete state change.
   * @param root The renderer containing the scene graph on which to undo removals.
   */
  public void undoRemovals(GameStateRenderer renderer);

  /**
   * Applies only the removals of a change in-place in the specified scene graph. Follow this call
   * by {@link #undoRemovals} to undo a complete state change.
   * @param root The renderer containing the scene graph on which to undo additions.
   */
  public void undoAdditions(GameStateRenderer renderer);

  /**
   * Generates an animation corresponding to a change.
   * @param renderer The renderer containing the scene graph. It will be modifiedduring the call as
   *     if {@link #applyRemovals} and {@link #applyAdditions} had been called.
   * @param animRoot The root of the scene graph to which to add animations.
   * @param startingTime The starting time of the animations to add.
   */
  public void generateAnim(GameStateRenderer renderer, SceneNodeList animRoot, double startingTime);
}