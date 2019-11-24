package com.github.oxisto.reticulated.ast

import com.github.oxisto.reticulated.ast.expression.Expression
import com.github.oxisto.reticulated.ast.expression.Identifier
import com.github.oxisto.reticulated.ast.simple.SimpleStatement
import com.github.oxisto.reticulated.ast.statement.*
import com.github.oxisto.reticulated.grammar.Python3BaseVisitor
import com.github.oxisto.reticulated.grammar.Python3Parser
import org.antlr.v4.runtime.tree.TerminalNode

class StatementVisitor(val scope: Scope) : Python3BaseVisitor<Statement>() {

  override fun visitStmt(ctx: Python3Parser.StmtContext?): Statement {
    if (ctx == null) {
      throw EmptyContextException()
    }

    return if (ctx.childCount == 1 && ctx.getChild(0) is Python3Parser.Compound_stmtContext) {
      // its a compound statement
      ctx.getChild(0).accept(this)
    } else {
      // create a statement list
      val list = ArrayList<SimpleStatement>()

      // loop through children
      for (tree in ctx.children) {
        list.add(tree.accept(SimpleStatementVisitor(this.scope)))
      }

      StatementList(list)
    }
  }

  override fun visitFuncdef(ctx: Python3Parser.FuncdefContext?): Statement {
    if (ctx == null) {
      throw EmptyContextException()
    }

    // TODO: decorators

    // assume that the first child is 'def'

    // second is the name
    val id = ctx.getChild(1).accept(IdentifierVisitor(this.scope))

    // create a new scope for this function
    val functionScope = Scope(this.scope, ScopeType.FUNCTION);

    // third is the parameter list
    val parameterList = ctx.getChild(2).accept(Visitor(functionScope)) as ParameterList;

    // forth is ':' or '->'
    val op = ctx.getChild(3)
    var expression: Expression? = null
    if (op.text == "->") {
      // fifth is the optional type hint
      expression = ctx.getChild(4).accept(ExpressionVisitor(functionScope))
    }

    // last is the suite
    val suite = ctx.getChild(ctx.childCount - 1).accept(Visitor(functionScope)) as Suite;

    // create a new function definition
    val def = FunctionDefinition(id, parameterList, suite, expression)

    return def;
  }

}