/*
 * SelectTrait.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.liftcode.model

import net.liftweb._
import mapper._
import util._
import http._

trait SelectTrait[U <: LongKeyedMapper[U] with IdPK] {
  self: U =>

  object myDog extends MappedLongForeignKey(this.asInstanceOf[U], Dog) {
    override def _toForm = Full(SHtml.selectObj[Dog](
        Dog.findAll.map(d => (d, d.name.is)),
          obj,
          (d: Dog) => apply(d))
    )
  }
}
