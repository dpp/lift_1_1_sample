/*
 * Dog.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.liftcode.model

import net.liftweb._
import mapper._
import http._
import util._

import scala.actors.Actor

class Dog extends LongKeyedMapper[Dog] with IdPK {
  def getSingleton = Dog

  object name extends MappedPoliteString(this, 128)
  object weight extends MappedInt(this)
}

object Dog extends Dog with LongKeyedMetaMapper[Dog] with CRUDify[Long, Dog] {
  override def afterCreate = createdRow _ :: super.afterCreate

  private def createdRow(r: Dog) {
    DogBroker ! r
  }
}

object DogBroker extends Actor with ListenerManager {
  var latestDog: Box[Dog] = Empty

  def createUpdate = latestDog

  override def highPriority = {
    case d: Dog =>
      latestDog = Full(d)
      updateListeners()
  }

  this.start
}