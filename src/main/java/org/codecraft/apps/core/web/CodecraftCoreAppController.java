package org.codecraft.apps.core.web;

import com.axelor.common.Inflector;
import com.axelor.db.Model;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaView;
import com.axelor.meta.db.repo.MetaViewRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.axelor.studio.app.web.AppController;
import com.axelor.studio.exception.StudioExceptionMessage;
import com.axelor.utils.helpers.ExceptionHelper;
import java.util.ArrayList;

public class CodecraftCoreAppController extends AppController {

  @Override
  public void configure(ActionRequest request, ActionResponse response) {
    try {
      Context context = request.getContext();

      MetaView formView = null;
      String code = (String) context.get("code");
      String appName = Inflector.getInstance().camelize(code);
      Model config = (Model) context.get("app" + appName);
      // String model = "com.axelor.studio.db.App" + appName;

      ArrayList<String> models = defineModels(appName);

      if (config != null) {
        formView =
            Beans.get(MetaViewRepository.class)
                .all()
                .filter(
                    "self.type = 'form' AND self.model IN ? AND self.name like '%-config-form'",
                    models)
                .fetchOne();
      }

      if (formView == null) {
        response.setInfo(I18n.get(StudioExceptionMessage.NO_CONFIG_REQUIRED));
      } else {
        response.setView(
            ActionView.define(I18n.get("Configure") + ": " + context.get("name"))
                .add("form", formView.getName())
                .model(formView.getModel())
                .context("_showRecord", config.getId())
                .param("forceEdit", "true")
                .map());
      }
    } catch (Exception e) {
      ExceptionHelper.trace(response, e);
    }
  }

  protected ArrayList<String> defineModels(String appName) {

    ArrayList<String> models = new ArrayList<>();

    models.add(String.format("com.axelor.studio.db.App%s", appName));
    models.add(String.format("org.codecraft.apps.base.db.App%s", appName));

    return models;
  }
}
