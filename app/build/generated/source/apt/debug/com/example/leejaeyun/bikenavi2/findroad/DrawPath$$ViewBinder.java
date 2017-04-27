// Generated code from Butter Knife. Do not modify!
package com.example.leejaeyun.bikenavi2.findroad;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DrawPath$$ViewBinder<T extends com.example.leejaeyun.bikenavi2.findroad.DrawPath> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624069, "field 'tv_pathInfo'");
    target.tv_pathInfo = finder.castView(view, 2131624069, "field 'tv_pathInfo'");
    view = finder.findRequiredView(source, 2131623982, "field 'startBtn'");
    target.startBtn = finder.castView(view, 2131623982, "field 'startBtn'");
  }

  @Override public void unbind(T target) {
    target.tv_pathInfo = null;
    target.startBtn = null;
  }
}
