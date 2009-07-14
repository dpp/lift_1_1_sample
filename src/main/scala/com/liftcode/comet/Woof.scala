/*
 * Woof.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.liftcode.comet

import net.liftweb._
import http._
import util._

import model._

class Woof extends CometActor with CometListener {
  private var dog: Box[Dog] = Empty

  def render = <div>Dog: {dog.map(_.toString) openOr "None"}</div>

  def registerWith = DogBroker
  
  def foo = for {
    i <- List("one", "twi")
  } yield i.length

  override def highPriority = {
    case Full(d: Dog) =>
      dog = Full(d)
      reRender(false)
  }
}
