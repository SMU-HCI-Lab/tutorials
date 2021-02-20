package controllers

import javax.inject.Inject
import play.api.cache.SyncCacheApi
import play.api.mvc.{AbstractController, ControllerComponents}

class EntryPointController @Inject() (
                                     cc: ControllerComponents
                                     ) extends AbstractController(cc) {
  def index = Action { request =>
    Ok(views.html.index())
  }
}
