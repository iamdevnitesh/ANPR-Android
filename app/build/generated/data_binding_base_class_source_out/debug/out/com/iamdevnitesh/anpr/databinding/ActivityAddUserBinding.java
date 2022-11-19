// Generated by data binding compiler. Do not edit!
package com.iamdevnitesh.anpr.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.iamdevnitesh.anpr.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class ActivityAddUserBinding extends ViewDataBinding {
  @NonNull
  public final Button BtnRegister;

  @NonNull
  public final TextInputLayout EdtTxtLayoutPassword;

  @NonNull
  public final TextInputLayout EdtTxtLayoutUserName;

  @NonNull
  public final TextInputEditText EdtTxtPassword;

  @NonNull
  public final TextInputEditText EdtTxtUserName;

  protected ActivityAddUserBinding(Object _bindingComponent, View _root, int _localFieldCount,
      Button BtnRegister, TextInputLayout EdtTxtLayoutPassword,
      TextInputLayout EdtTxtLayoutUserName, TextInputEditText EdtTxtPassword,
      TextInputEditText EdtTxtUserName) {
    super(_bindingComponent, _root, _localFieldCount);
    this.BtnRegister = BtnRegister;
    this.EdtTxtLayoutPassword = EdtTxtLayoutPassword;
    this.EdtTxtLayoutUserName = EdtTxtLayoutUserName;
    this.EdtTxtPassword = EdtTxtPassword;
    this.EdtTxtUserName = EdtTxtUserName;
  }

  @NonNull
  public static ActivityAddUserBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_add_user, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static ActivityAddUserBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<ActivityAddUserBinding>inflateInternal(inflater, R.layout.activity_add_user, root, attachToRoot, component);
  }

  @NonNull
  public static ActivityAddUserBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_add_user, null, false, component)
   */
  @NonNull
  @Deprecated
  public static ActivityAddUserBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<ActivityAddUserBinding>inflateInternal(inflater, R.layout.activity_add_user, null, false, component);
  }

  public static ActivityAddUserBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static ActivityAddUserBinding bind(@NonNull View view, @Nullable Object component) {
    return (ActivityAddUserBinding)bind(component, view, R.layout.activity_add_user);
  }
}
