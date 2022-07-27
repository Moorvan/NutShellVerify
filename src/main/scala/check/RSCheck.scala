package check

import chisel3._
import chiselFv._
import nutcore.backend.ooo._


class RSCheck extends Module with Formal {

}

object RSCheck extends App {
  Check.generateRTL(() => new RS())
}