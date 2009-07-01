/*
 * CMSLib.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.liftcode.lib

import net.liftweb._
import http._
import SHtml._
import js._
import JsCmds._
import JE._
import mapper._
import sitemap._
import Loc._
import util._
import Helpers._
import net.liftweb.http.js.jquery._
import model._

import scala.xml._

object CMSLoc extends Loc[Content] {

  // the name of the page
  def name = "cms"

  // the default parameters (used for generating the menu listing)
  def defaultParams = Full(Content.create.name("Home"))

  // no extra parameters
  def params = List()

  /**
   * Check for page-specific snippets and
   * do appropriate dispatching
   */
  override val snippets: SnippetTest = {
    case ("cms", Full(wp)) => displayRecord(wp) _
    case ("edit_button", Full(wp)) => doEditButton(wp) _
  }


  /**
   * Generate a link based on the current page
   */
  val link =
  new Loc.Link[Content](List("cms"), false) {
    override def createLink(in: Content) = {
      Full(Text("/cms/"+urlEncode(in.name)))
    }
  }

  /**
   * What's the text of the link?
   */
  val text = new Loc.LinkText(calcLinkText _)


  def calcLinkText(in: Content): NodeSeq = Text("Page "+in.name)

  /**
   * Rewrite the request and emit the type-safe parameter
   */
  override val rewrite: LocRewrite =
  Full(NamedPF("CMS Rewrite") {
      case RewriteRequest(ParsePath("cms" :: Content(page) :: Nil, _, _,_),  _, _) =>
        (RewriteResponse("cms" :: Nil), page)
    })


  //  def url(page: String) = createLink(WikiLoc(page, false))

  def displayRecord(entry: Content)(in: NodeSeq): NodeSeq =
  <span id="content">
    {entry.contentHTML}
  </span>

  def doEditButton(entry: Content)(in: NodeSeq): NodeSeq = {
    var current = entry

    def editInfo(ignore: String): JsCmd = {
      var text = current.content.is

      JqJsCmds.ModalDialog(bind("cms",
                                TemplateFinder.findAnyTemplate(List("_modal_editor")) openOr NodeSeq.Empty,
                                "text_area" -> textarea(text, ignore => (), "rows" -> "20", "cols" -> "100", "id" -> "new_content"),
                                AttrBindParam("preview_click",ajaxCall(ValById("new_content"),
                                                                       str => {text = str
                                                                               SetHtml("content", Content.create.content(str).contentHTML)
              })._2,"onclick"),
                                AttrBindParam("cancel_click",ajaxCall("",
                                                                       str => {
                                                                               SetHtml("content", current.contentHTML) & JqJsCmds.Unblock
              })._2,"onclick"),
                                AttrBindParam("save_click",ajaxCall(ValById("new_content"),
                                                                       str => {current.content(str).saveMe
                                                                               SetHtml("content", current.contentHTML) & JqJsCmds.Unblock
              })._2,"onclick")
        ))
    }

    val foo = SHtml.ajaxCall("", editInfo _)._2


    bind("cms", in,
         AttrBindParam("onclick", foo, "onclick"))
  }

}
