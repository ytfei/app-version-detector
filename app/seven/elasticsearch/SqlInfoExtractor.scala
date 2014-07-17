package seven.elasticsearch

import cm.antlr4.sql.{SqlParser, SqlBaseListener}
import SqlParser._

/**
 * Created by evans on 7/17/14.
 *
 * Extract query info from the parsed sql
 */

/**
 * Scenarios for test:
 *
 * select a,b from table
 * select * from table
 * select a,b from table where a='b'
 * select a,b from table where a=1
 * select * from table where a='b'
 * select * from table where a=1 and b=2 or (c='d' and e = 'f')
 */
class SqlInfoExtractor extends SqlBaseListener {
  val info = new SqlInfo

  override def enterTable_or_subquery(ctx: Table_or_subqueryContext): Unit =
    info.tableName = Option(ctx.table_name().any_name().getText)

  // column fields
  override def enterResult_column(ctx: Result_columnContext): Unit =
    if (ctx.expr() == null) // for all column '*'
      info.fields += ctx.getText
    else
      info.fields += ctx.expr().column_name().any_name().getText

  override def enterSelect_core(ctx: Select_coreContext): Unit = {
    val expr = ctx.expr(0) // root expr of where clause
    if (expr != null) {
      println("Name of where expr: " + expr.getText)
    } else
      println("No where clause!")
  }

  var indent = new collection.mutable.ArrayBuffer[String]
  val GAP = "  "

  def prefix = indent.mkString("")

  override def enterExpr(expr: ExprContext): Unit = {
    if (expr.op != null) {
      expr.op.getType match {
        case x@(K_AND | K_OR) =>
          println(prefix + {
            if (x == K_AND) "AND" else "OR"
          })
          indent += GAP
        case x@(EQ | LT_EQ | GT_EQ | LT | GT | ASSIGN) =>
          println(s"${prefix}${left(expr)}, ${right(expr)}")
      }
    }
  }

  override def exitExpr(expr: ExprContext): Unit = {
    if (expr.op != null) {
      expr.op.getType match {
        case x@(K_AND | K_OR) =>
          indent -= GAP
        case x@(EQ | LT_EQ | GT_EQ | LT | GT | ASSIGN) =>

      }
    }
  }

  // Left part of the expr
  private def left(expr: ExprContext): String =
    expr.expr(0).column_name().any_name().getText

  // Right part of the expr
  private def right(expr: ExprContext): String = {
    val real = expr.expr(1)
    if (real.literal_value() != null)
      real.literal_value().getText
    else
      real.collation_name().getText
  }

}

class SqlInfo {
  var tableName: Option[String] = None
  var fields = collection.mutable.ArrayBuffer[String]()

  override def toString: String = {
    val buff = new StringBuffer()

    buff.append("tableName: ").append(tableName.getOrElse("")).append("\n")
    buff.append("fields: ").append(fields.mkString(",")).append("\n")

    buff.toString
  }
}