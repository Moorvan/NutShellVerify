package check

import chisel3._
import chiselFv._
import nutcore.NutCoreConfig
import nutcore.backend.fu._
import nutcore.backend.ooo._



class ROBCheck {

}


object ROBCheck extends App {
  Check.generateRTL(() => new CSR()(NutCoreConfig()))
}