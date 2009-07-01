/*
 * Content.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.liftcode.model

import net.liftweb._
import mapper._
import textile._
import scala.xml.NodeSeq

class Content extends LongKeyedMapper[Content] with IdPK {
  def getSingleton = Content

  object name extends MappedPoliteString(this, 256) {
    override def dbIndexed_? = true
  }

  object content extends MappedText(this)

  def contentHTML: NodeSeq = TextileParser.toHtml(content)
}

object Content extends Content with LongKeyedMetaMapper[Content] {

}
