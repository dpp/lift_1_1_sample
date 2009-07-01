/*
 * CMSLib.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.liftcode.lib

import net.liftweb._
import mapper._
import sitemap._
import Loc._

import model._

/**
 * The WikiStuff object that provides menu, URL rewriting,
 * and snippet support for the page that displays wiki contents
 */
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


  def calcLinkText(in: Content): NodeSeq =
  Text("Page "+in.name)

  /**
   * Rewrite the request and emit the type-safe parameter
   */
  override val rewrite: LocRewrite =
  Full(NamedPF("CMS Rewrite") {
      case RewriteRequest(ParsePath("cms" :: page :: Nil, _, _,_),
                          _, _) =>
        (RewriteResponse("cms" :: Nil), WikiLoc(page, false))

    })

  def showAll(in: NodeSeq): NodeSeq =
  WikiEntry.findAll(OrderBy(WikiEntry.name, Ascending)).flatMap(entry =>
    <div><a href={url(entry.name)}>{entry.name}</a></div>)

  def url(page: String) = createLink(WikiLoc(page, false))


  def editRecord(r: WikiEntry)(in: NodeSeq): NodeSeq =
  <span>
    <a href={createLink(AllLoc)}>Show All Pages</a><br />
    {
      val isNew = !r.saved_?
      val pageName = r.name.is
      val action = url(pageName)
      val message =
      if (isNew)
      Text("Create Entry named "+pageName)
      else
      Text("Edit entry named "+pageName)

      val hobixLink = <span>&nbsp;<a href="http://hobix.com/textile/quick.html" target="_blank">Textile Markup Reference</a><br /></span>

      val cancelLink = <a href={action}>Cancel</a>
      val textarea = r.entry.toForm

      val submitButton = SHtml.submit(isNew ? "Add" | "Edit", () => r.save)

      <form method="post" action={action}>{ // the form tag
          message ++
          hobixLink ++
          textarea ++ // display the form
          <br /> ++
          cancelLink ++
          Text(" ") ++
          submitButton
        }</form>
    }

  </span>

  def displayRecord(entry: WikiEntry)(in: NodeSeq): NodeSeq =
  <span>
    <a href={createLink(AllLoc)}>Show All Pages</a><br />
    {TextileParser.toHtml(entry.entry, textileWriter)}

    <br/><a href={createLink(WikiLoc(entry.name, true))}>Edit</a>
  </span>

  import TextileParser._

  val textileWriter = Some((info: WikiURLInfo) =>
    info match {
      case WikiURLInfo(page, _) =>
        (stringUrl(page), Text(page), None)
    })

  def stringUrl(page: String): String =
  url(page).map(_.text) getOrElse ""


}

object CMSLib {

}
