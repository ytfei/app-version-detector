package seven.elasticsearch

import java.io.ByteArrayInputStream

import cm.antlr4.sql.{SqlParser, SqlLexer}
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.antlr.v4.runtime.{CommonTokenStream, ANTLRInputStream}

/**
 * Created by evans on 7/17/14.
 */
object Test {
  def run() = {
    println("Try to input a SQL statement: ")
    val stat = Console.readLine()
    doRun(stat)
  }

  def doRun(statement: String) {
    val input = new ANTLRInputStream(new ByteArrayInputStream(statement.getBytes))
    val tokens = new CommonTokenStream(new SqlLexer(input))

    val parser = new SqlParser(tokens)
    val tree = parser.parse()

    val walker = new ParseTreeWalker

    val extractor = new SqlInfoExtractor
    walker.walk(extractor, tree)

    println(extractor.info.toString)
    println()
  }
}
