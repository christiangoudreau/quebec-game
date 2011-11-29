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

package com.philbeaudoin.quebec.client.sprites;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.inject.Inject;
import com.philbeaudoin.quebec.client.utils.LoggerFactory;
import com.philbeaudoin.quebec.shared.utils.MutableVector2d;

/**
 * This class knows how to render a single sprite into a canvas.
 *
 * @author Philippe Beaudoin
 */
public class Sprite {
  private final Logger logger;
  private final SpriteResources spriteResources;

  private SpriteResources.Info info;
  private MutableVector2d pos = new MutableVector2d();
  private double sizeFactor = 1;
  private double angle = 0;

  @Inject
  Sprite(SpriteResources spriteImages) {
    logger = LoggerFactory.get(Sprite.class);
    this.spriteResources = spriteImages;
  }

  /**
   * Sets the type of sprite.
   * @param type The desired type.
   */
  public void setType(SpriteResources.Type type) {
    info = spriteResources.get(type);
  }

  /**
   * Sets the position of the sprite, relative to the canvas. The position should be between 0 and 1
   * along both axis.
   * @param x The desired X position.
   * @param y The desired Y position.
   */
  public void setPos(double x, double y) {
    this.pos.set(x, y);
  }

  /**
   * Sets the relative size of the sprite. A size factor of 1 means the sprite will be rendered so
   * that it looks OK on the board.
   * @param sizeFactor The desired size factor.
   */
  public void setSizeFactor(double sizeFactor) {
    this.sizeFactor = sizeFactor;
  }

  /**
   * Sets the rotation angle of the sprite.
   * @param angle The desired rotation angle, in radians.
   */
  public void setAngle(double angle) {
    this.angle = angle;
  }

  /**
   * Renders the sprite to the canvas.
   * @param context The canvas context into which to render.
   */
  public void render(Context2d context) {
    if (info == null || info.getElement() == null) {
      logger.log(Level.SEVERE, "Trying to render sprite with null image element.");
    }
    context.save();
    try {
      double factor = sizeFactor * info.getSizeFactor();
      context.translate(pos.getX(), pos.getY());
      context.scale(factor, factor);
      context.rotate(angle);
      ImageElement imageElement = info.getElement();
      context.drawImage(imageElement, -imageElement.getWidth() / 2, -imageElement.getHeight() / 2);
    } finally {
      context.restore();
    }
  }

}
