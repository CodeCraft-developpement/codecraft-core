package org.codecraft.apps.core.module;

import com.axelor.app.AxelorModule;
import com.axelor.studio.app.web.AppController;
import org.codecraft.apps.core.web.CodecraftCoreAppController;

public class CoreModule extends AxelorModule {

  @Override
  protected void configure() {

    bind(AppController.class).to(CodecraftCoreAppController.class);
  }
}
