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

package com.philbeaudoin.quebec.client.menu;

import java.util.Date;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.philbeaudoin.quebec.client.widget.FullCanvas;

/**
 * Main view, containing the board and the player information bars. The canvas is automatically
 * scaled to fill the entire window. It is mapped to a virtual coordinate space with range:
 *   (0, 1.7) x (0, 1)
 * This horizontal range is determined by the aspect ratio specified in MainPageView.ui.xml.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MenuView extends ViewImpl implements MenuPresenter.MyView {

  interface Binder extends UiBinder<Widget, MenuView> { }
  protected static final Binder binder = GWT.create(Binder.class);

  // Timer refresh rate, in milliseconds.
  static final int refreshRate = 15;

  private final Widget widget;
  private final Canvas canvas;
  private final Context2d context;
  private final Canvas staticLayerCanvas;
  private final Context2d staticLayerContext;

  // For animations
  private double time;
  private boolean isRefreshing;

  // To track mouse
  private double lastMouseX;
  private double lastMouseY;

  // For performance info.
  private long lastTimeMs;
  private long fps;
  private int cnt;

  @UiField
  FullCanvas fullCanvas;

  private MenuPresenter presenter;

  // Setup refresh timer.
  final Timer refreshTimer = new Timer() {
    @Override
    public void run() {
      doRenderStaticLayer();
      isRefreshing = false;
    }
  };

  @Inject
  public MenuView() {
    widget = binder.createAndBindUi(this);
    canvas = fullCanvas.asCanvas();
    context = canvas.getContext2d();

    staticLayerCanvas = Canvas.createIfSupported();
    staticLayerContext = staticLayerCanvas.getContext2d();

    fullCanvas.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        int width = canvas.getCoordinateSpaceWidth();
        int height = canvas.getCoordinateSpaceHeight();
        context.setFillStyle("#ddd");
        context.setStrokeStyle("#000");
        context.rect(0, 0, width, height);
        context.fill();
        context.stroke();
        refreshStaticLayer(650);
      }
    });

    // Setup animation timer.
    final Timer animTimer = new Timer() {
      @Override
      public void run() {
        doUpdate();
      }
    };
    animTimer.scheduleRepeating(refreshRate);

    fullCanvas.addMouseMoveHandler(new MouseMoveHandler() {
      @Override
      public void onMouseMove(MouseMoveEvent event) {
        double height = canvas.getOffsetHeight();
        lastMouseX = event.getRelativeX(fullCanvas.getElement()) / height;
        lastMouseY = event.getRelativeY(fullCanvas.getElement()) / height;
        presenter.onMouseMove(lastMouseX, lastMouseY, time);
      }
    });

    fullCanvas.addMouseDownHandler(new MouseDownHandler() {
      @Override
      public void onMouseDown(MouseDownEvent event) {
        double height = canvas.getOffsetHeight();
        lastMouseX = event.getRelativeX(fullCanvas.getElement()) / height;
        lastMouseY = event.getRelativeY(fullCanvas.getElement()) / height;
        presenter.onMouseClick(lastMouseX, lastMouseY, time);
      }
    });
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setPresenter(MenuPresenter presenter) {
    this.presenter = presenter;
  }

  void refreshStaticLayer(int delayMs) {
    isRefreshing = true;
    refreshTimer.cancel();
    refreshTimer.schedule(delayMs);
  }

  void doRenderStaticLayer() {
    time = 0;

    int width = canvas.getCoordinateSpaceWidth();
    int height = canvas.getCoordinateSpaceHeight();
    context.setFillStyle("#fff");
    context.fillRect(0, 0, width, height);

    if (presenter != null) {
      staticLayerCanvas.setPixelSize(width, height);
      staticLayerCanvas.setCoordinateSpaceWidth(width);
      staticLayerCanvas.setCoordinateSpaceHeight(height);

      staticLayerContext.save();
      try {
        staticLayerContext.scale(height, height);
        staticLayerContext.setLineWidth(0.001);
        presenter.drawStaticLayers(staticLayerContext);
      } finally {
        staticLayerContext.restore();
      }
      presenter.onMouseMove(lastMouseX, lastMouseY, time);
    }
  }

  void doUpdate() {
    if (isRefreshing) {
      return;
    }
    if (presenter.isRefreshNeeded()) {
      refreshStaticLayer(50);
      return;
    }
    cnt++;
    if (cnt == 60) {
      cnt = 0;
      long timeMs = new Date().getTime();
      if (lastTimeMs > 0) {
        long msFor60Frames = timeMs - lastTimeMs;
        if (msFor60Frames > 0) {
          fps = 60000 / msFor60Frames;
        }
      }
      lastTimeMs = timeMs;
    }
    time += refreshRate / 1000.0;
    if (presenter != null) {
      int height = canvas.getCoordinateSpaceHeight();
      context.save();
      try {
        context.drawImage(staticLayerCanvas.getCanvasElement(), 0, 0);
        context.setFillStyle("#bbb");
        context.fillRect(0, height - 13, 40, 13);
        context.setFillStyle("#000");
        context.fillText("FPS: " + fps, 2, height - 2);
        context.scale(height, height);
        context.setLineWidth(0.001);
        presenter.drawDynamicLayers(time, context);
      } finally {
        context.restore();
      }
    }
  }

}