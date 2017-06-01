package com.lightbend.akka.sample

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Keep}
import akka.testkit.TestKit
import akka.util.ByteString
import better.files.File
import unindent._
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class CsvTest extends TestKit(ActorSystem("CsvTest")) with FlatSpecLike
  with Matchers with BeforeAndAfterAll with Eventually with ScalaFutures with IntegrationPatience {

  implicit val mat = ActorMaterializer()

  it should "fail to read all lines" in {
    val data: String =
      i"""
    Date,    Value, Param 1
    2017-01-01,  1,       0
    2017-01-02,  2,      10
    2017-01-03,  3,      10""".replace(" ", "")

    File.usingTemporaryFile() { inputFile =>
      inputFile.write(data)

      eventually {
        File.usingTemporaryFile() { output =>
          FileIO
            .fromPath(inputFile.toJava.toPath)
            .via(CsvParsing.lineScanner())
            .via(CsvToMap.toMap())
            .map(row => "r")
            .map(ByteString.fromString)
            .toMat(FileIO.toPath(output.toJava.toPath))(Keep.right)
            .run()
            .futureValue

          println(output.contentAsString)
          output.contentAsString should be("rr") // one 'r' is missing
        }
      }
    }

  }

  override protected def afterAll(): Unit = {
    mat.shutdown()
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }
}
