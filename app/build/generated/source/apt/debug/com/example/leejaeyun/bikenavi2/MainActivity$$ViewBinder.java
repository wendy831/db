// Generated code from Butter Knife. Do not modify!
package com.example.leejaeyun.bikenavi2;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.example.leejaeyun.bikenavi2.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624065, "field 'fab_findroad'");
    target.fab_findroad = finder.castView(view, 2131624065, "field 'fab_findroad'");
    view = finder.findRequiredView(source, 2131624066, "field 'fab_nowdeletePath'");
    target.fab_nowdeletePath = finder.castView(view, 2131624066, "field 'fab_nowdeletePath'");
  }

  @Override public void unbind(T target) {
    target.fab_findroad = null;
    target.fab_nowdeletePath = null;
  }
}
