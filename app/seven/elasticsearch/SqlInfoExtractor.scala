package seven.elasticsearch

import cm.antlr4.sql.{SqlParser, SqlBaseListener}
import cm.antlr4.sql.SqlParser.{Table_or_subqueryContext, Result_columnContext, Select_coreContext, Table_or_index_nameContext}

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
      expr.K_AND()
      expr.op.getType == SqlParser.STAR

    } else
      println("No where clause!")
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