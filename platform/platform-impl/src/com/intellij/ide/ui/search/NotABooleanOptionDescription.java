// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.ide.ui.search;

/**
 * Marker-class for {@link BooleanOptionDescription} that is not a description for a boolean option.
 */
public abstract class NotABooleanOptionDescription extends BooleanOptionDescription {
  protected NotABooleanOptionDescription(String option, String configurableId) {
    super(option, configurableId);
  }
}
