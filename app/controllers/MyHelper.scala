package controllers

/**
 * Created by evans on 5/30/14.
 */
object MyHelper {
  import views.html.helper.FieldConstructor
  implicit val myFields = FieldConstructor(views.html.tpl.form.f)
}
