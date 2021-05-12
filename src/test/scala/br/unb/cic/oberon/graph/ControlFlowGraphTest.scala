package br.unb.cic.oberon.graph

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, IntraProceduralGraphBuilder, SimpleNode, StartNode}
import org.scalatest.funsuite.AnyFunSuite
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc


class ControlFlowGraphTest extends AnyFunSuite {

  /**
   * This is the test case for a control-flow graph for the following Oberon Code:
   *
   * BEGIN
   *   readInt(x);
   *   readInt(max);
   *   IF(x > max) THEN
   *     max := x
   *   END;
   *   write(max)
   * END
   *
   */
  test("Test control flow graph for stmt16.oberon") {
    val s3_1 = AssignmentStmt(1,"max", VarExpression("x"))
    val s1 = ReadIntStmt(2,"x")
    val s2 = ReadIntStmt(3,"max")
    val s3 = IfElseStmt(4,GTExpression(VarExpression("x"), VarExpression("max")), s3_1 , None)
    val s4 = WriteStmt(5,VarExpression("max"))

    // we manually build the "expected" graph, to run the test case.
    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> SimpleNode(s3_1)
    expected += SimpleNode(s3_1) ~> SimpleNode(s4)
    expected += SimpleNode(s3) ~> SimpleNode(s4)
    expected += SimpleNode(s4) ~> EndNode()

    val stmts = List(s1, s2, s3, s4)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(6,stmts))

    assert( g.nodes.size == 7 )
    assert( g.edges.size == 7 )

    assert( g == expected )  // does the resulting control-flow graph match with the expected graph?
  }

test("Test control flow graph for stmt01.oberon") {
    val s1 = ReadIntStmt(1,"x")
    val s2 = ReadIntStmt(2,"y")
    val s3 = WriteStmt(3,AddExpression(VarExpression("x"), VarExpression("y")))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> EndNode()

    val stmts = List (s1, s2, s3)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(4,stmts))

    assert( g.nodes.size == 5 )
    assert( g.edges.size == 4 )

    assert( g == expected )
  }

  test("Test control flow graph for stmt02.oberon") {
    val s1 = ReadIntStmt(1,"x")
    val s2 = ReadIntStmt(2,"y")
    val s3 = AssignmentStmt(3,"z", AddExpression(VarExpression("x"), VarExpression("y")))
    val s4 = WriteStmt(4,VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> SimpleNode(s4)
    expected += SimpleNode(s4) ~> EndNode()

    val stmts = List (s1, s2, s3, s4)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(5,stmts))

    assert( g.nodes.size == 6 )
    assert( g.edges.size == 5 )

    assert( g == expected )
  }
  /** Whilestmt test */
  test("Test control flow graph for stmt04.oberon") {
    val s3_1 = AssignmentStmt(1,"x", MultExpression(VarExpression("x"), VarExpression("x")))
    val s1 = ReadIntStmt(2,"x")
    val s2 = ReadIntStmt(3,"y")
    val s3 = WhileStmt(4,LTExpression(VarExpression("x"), VarExpression("y")), s3_1)
    val s4 = WriteStmt(5,VarExpression("x"))


    // we manually build the "expected" graph, to run the test case.
    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> SimpleNode(s3_1)
    expected += SimpleNode(s3_1) ~> SimpleNode(s4)
    expected += SimpleNode(s3) ~> SimpleNode(s4)
    expected += SimpleNode(s4) ~> EndNode()

    val stmts = List(s1, s2, s3, s4)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(6,stmts))

    assert( g.nodes.size == 7 )
    assert( g.edges.size == 7 )
    assert( expected.nodes.size == 7)
    assert( expected.edges.size == 7)

    assert( g == expected )  // does the resulting control-flow graph match with the expected graph?
  }
  /**Forstmt Test*/
  test("Test control flow graph for stmt11.oberon") {


    /**
     * BEGIN
     * readInt(x);
     *
     * FOR y := 0 TO y < x DO
     * readInt(z);
     * z := z/(y+1);
     * write(z)
     * END;
     *
     */

    val s3_1 = ReadIntStmt(1,"z")
    val s1 = ReadIntStmt(2,"x")
    val s2 = AssignmentStmt(3,"y", IntValue(0))
    val s3 = ForStmt(4,s2, LTExpression(VarExpression("y"), VarExpression("x")), s3_1)
    val s4 = AssignmentStmt(5,"z", DivExpression(VarExpression("z"), AddExpression(VarExpression("y"), IntValue(1))))
    val s5 = WriteStmt(6,VarExpression("z"))


    // we manually build the "expected" graph, to run the test case.
    var expected = Graph[GraphNode, GraphEdge.DiEdge]() //Expected: 7 nodes

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s2) ~> SimpleNode(s3_1)
    expected += SimpleNode(s3) ~> SimpleNode(s4)
    expected += SimpleNode(s3_1) ~> SimpleNode(s4)
    expected += SimpleNode(s4) ~> SimpleNode(s5)
    expected += SimpleNode(s5) ~> EndNode()

    val stmts = List(s1, s2, s3, s4, s5)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(7,stmts))

    assert( g.nodes.size == 8 )
    assert( g.edges.size == 8 )
    assert( expected.nodes.size == 8)
    assert( expected.edges.size == 8)

    assert( g == expected )  // does the resulting control-flow graph match with the expected graph?
  }


  test("Test control flow graph for stmt12.oberon") {


    /**
     * BEGIN
     * readInt(x);
     * v := 0;
     *
     * FOR y := 0 TO y < x DO
     * readInt(w);
     * v := v + (w * (y+1))
     * END;
     * v := v / x;
     *
     * FOR  z:= 0 TO z < x DO
     * readInt(w);
     * u := u + w
     * END;
     * u := u / x;
     *
     * write(v);
     * write(u)
     *
     * END
     *
     */

    val s1 = ReadIntStmt(1,"x")
    val s2 = AssignmentStmt(2,"v", IntValue(0))

    val s3 = AssignmentStmt(3,"y", IntValue(0))
    val s4_1 = ReadIntStmt(4,"w")
    val s4 = ForStmt(5,s3, LTExpression(VarExpression("y"), VarExpression("x")), s4_1)
    val s5 = AssignmentStmt(6,"v", AddExpression(VarExpression("v"), MultExpression(VarExpression("w"), AddExpression(VarExpression("y"), IntValue(1)))))

    val s6 = AssignmentStmt(7,"v", DivExpression(VarExpression("v"), VarExpression("x")))

    val s7 = AssignmentStmt(8,"z", IntValue(0))
    val s8_1 = ReadIntStmt(9,"w")
    val s8 = ForStmt(10,s7, LTExpression(VarExpression("z"), VarExpression("x")), s8_1)
    val s9 = AssignmentStmt(11,"u", AddExpression(VarExpression("u"), VarExpression("w")))

    val s10 = AssignmentStmt(12,"u", DivExpression(VarExpression("u"), VarExpression("x")))
    val s11 = WriteStmt(13,VarExpression("v"))
    val s12 = WriteStmt(14,VarExpression("u"))


    // // we manually build the "expected" graph, to run the test case.
    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    // for 1
    expected += SimpleNode(s3) ~> SimpleNode(s4)
    expected += SimpleNode(s3) ~> SimpleNode(s4_1)
    expected += SimpleNode(s4) ~> SimpleNode(s5)
    expected += SimpleNode(s4_1) ~> SimpleNode(s5)
    
    expected += SimpleNode(s5) ~> SimpleNode(s6)
    expected += SimpleNode(s6) ~> SimpleNode(s7)
    // for 2
    expected += SimpleNode(s7) ~> SimpleNode(s8)
    expected += SimpleNode(s7) ~> SimpleNode(s8_1)
    expected += SimpleNode(s8) ~> SimpleNode(s9)
    expected += SimpleNode(s8_1) ~> SimpleNode(s9)
    
    expected += SimpleNode(s9) ~> SimpleNode(s10)
    expected += SimpleNode(s10) ~> SimpleNode(s11)
    expected += SimpleNode(s11) ~> SimpleNode(s12)

    expected += SimpleNode(s12) ~> EndNode()

    val stmts = List(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(15,stmts))

     assert( g.nodes.size == 16 )
     assert( g.edges.size == 17 )

    assert( g == expected )  // does the resulting control-flow graph match with the expected graph?
  }

  test("Test control flow graph for stmt13.oberon") {


    /**
     * BEGIN
     * readInt(x);
     *
     * FOR y := x TO y < 100 DO
     * y := y * y
     *
     * END;
     *
     * write(y)
     *
     * END
     *
     */

    val s1 = ReadIntStmt(1,"x")

    val s2 = AssignmentStmt(2,"y", VarExpression("x"))
    val s3_1 = AssignmentStmt(3,"y", MultExpression(VarExpression("y"), VarExpression("y")))
    val s3 = ForStmt(4,s2, LTExpression(VarExpression("y"), IntValue(100)), s3_1)

    val s4 = WriteStmt(5,VarExpression("y"))


    // we manually build the "expected" graph, to run the test case.
    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s2) ~> SimpleNode(s3_1)
    expected += SimpleNode(s3) ~> SimpleNode(s4)
    expected += SimpleNode(s3_1) ~> SimpleNode(s4)
    expected += SimpleNode(s4) ~> EndNode()

    val stmts = List(s1, s2, s3, s4)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(6,stmts))

    assert( g.nodes.size == 7 )
    assert( g.edges.size == 7)

    assert( g == expected )  // does the resulting control-flow graph match with the expected graph?
  }




  test("Simple control flow graph with repeated statements") {

    val stmt0 = ReadIntStmt(1,"x")
    val stmt1 = ReadIntStmt(2,"y")
    val stmt2 = ReadIntStmt(3,"z")
    val stmt3 = ReadIntStmt(4,"x")

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> SimpleNode(stmt3)
    expected += SimpleNode(stmt3) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2, stmt3)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(5,statements))

    assert( expected.nodes.size == 6)
    assert( expected.edges.size == 5)
    assert( g.nodes.size == 6)
    assert( g.edges.size == 5)
    assert( g == expected )

  }

  /*  BEGIN
    x: INTEGER;
    z: BOOLEAN;
    readInt(x);
    CASE x OF
      0: z := FALSE
      1: z := TRUE
      ELSE: z:= FALSE
    END
    write(z)
  END*/

  test("Test control flow graph of CaseStatement with 2 regular cases and NO else case") {

    val case0_stmt = AssignmentStmt(1,"z", IntValue(1))
    val case1_stmt = AssignmentStmt(2,"z", IntValue(2))
    val case0 = SimpleCase(IntValue(0), case0_stmt)
    val case1 = SimpleCase(IntValue(1), case1_stmt)
    val cases = List(case0, case1)

    val stmt0 = ReadIntStmt(1,"x")
    val stmt1 = CaseStmt(1,VarExpression("x"), cases, None)
    val stmt2 = WriteStmt(1,VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(case0_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(stmt2)
    expected += SimpleNode(case0_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(3,statements))

    assert( expected.nodes.size == 7)
    assert( expected.edges.size == 8)
    assert( g.nodes.size == 7)
    assert( g.edges.size == 8)
    assert( g == expected )
  }

  test("Test control flow graph of CaseStatement with 2 regular cases and one else case") {

    val case0_stmt = AssignmentStmt(1,"z", IntValue(1))
    val case1_stmt = AssignmentStmt(2,"z", IntValue(2))
    val caseE_stmt = AssignmentStmt(3,"z", IntValue(3))
    val case0 = SimpleCase(IntValue(0), case0_stmt)
    val case1 = SimpleCase(IntValue(1), case1_stmt)
    val cases = List(case0, case1)

    val stmt0 = ReadIntStmt(4,"x")
    val stmt1 = CaseStmt(5,VarExpression("x"), cases, Some(caseE_stmt))
    val stmt2 = WriteStmt(6,VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(case0_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(caseE_stmt)
    expected += SimpleNode(case0_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(caseE_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(7,statements))

    assert( g.nodes.size == 8)
    assert( g.edges.size == 9)
    assert( g == expected )

  }
// Test control flow graph of CaseStatement with 3 regular cases and one else case
  test("Test control flow graph of CaseStatement with 3 regular cases and one else case") {

    val case0_stmt = AssignmentStmt(1,"z", AddExpression(VarExpression("a"), VarExpression("b")))
    val case1_stmt = AssignmentStmt(2,"z", SubExpression(VarExpression("a"), VarExpression("b")))
    val case2_stmt = AssignmentStmt(3,"z", MultExpression(VarExpression("a"), VarExpression("b")))
    val caseE_stmt = AssignmentStmt(4,"z", IntValue(0))

    val case0 = SimpleCase(IntValue(0), case0_stmt)
    val case1 = SimpleCase(IntValue(1), case1_stmt)
    val case2 = SimpleCase(IntValue(2), case2_stmt)
    val cases = List(case0, case1, case2)

    val stmt0 = ReadIntStmt(5,"x")
    val stmt1 = CaseStmt(6,VarExpression("x"), cases, Some(caseE_stmt))
    val stmt2 = WriteStmt(7,VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(case0_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case2_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(caseE_stmt)
    expected += SimpleNode(case0_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case2_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(caseE_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(8,statements))

    assert( 9 == g.nodes.size)
    assert( 11 == g.edges.size)
    assert( expected == g)
  }

  test("Test control flow graph of CaseStatement with 2 regular cases, 1 range case, one else case") {

    val case0_stmt = AssignmentStmt(1,"z", AddExpression(VarExpression("a"), VarExpression("b")))
    val case1_stmt = AssignmentStmt(2,"z", SubExpression(VarExpression("a"), VarExpression("b")))
    val case2_stmt = AssignmentStmt(3,"z", MultExpression(VarExpression("a"), VarExpression("b")))
    val caseE_stmt = AssignmentStmt(4,"z", IntValue(0))

    val case0 = SimpleCase(IntValue(0), case0_stmt)
    val case1 = SimpleCase(IntValue(1), case1_stmt)
    val case2 = RangeCase(IntValue(2), IntValue(10), case2_stmt)
    val cases = List(case0, case1, case2)

    val stmt0 = ReadIntStmt(5,"x")
    val stmt1 = CaseStmt(6,VarExpression("x"), cases, Some(caseE_stmt))
    val stmt2 = WriteStmt(7,VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(case0_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case2_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(caseE_stmt)
    expected += SimpleNode(case0_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case2_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(caseE_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(8,statements))

    assert( 9 == g.nodes.size)
    assert( 11 == g.edges.size)
    assert( expected == g)
  }

  test("Test control flow graph RepeatUntilStmt 03 - 1 Expression and 1 Condition ") {
    val stmt00 = AssignmentStmt(1,"x", IntValue(3))
    val stmt01 = AssignmentStmt(2,"y", IntValue(4))
    val stmt02 = AssignmentStmt(3,"z", MultExpression (VarExpression ("x") , VarExpression("y")))
    val stmt03 = AssignmentStmt(4,"z", AddExpression (VarExpression ("z"), IntValue(5)))
    val stmt04 = RepeatUntilStmt(5,LTExpression (VarExpression("z"), IntValue(50)), stmt03)
    val stmt05 = WriteStmt(6,VarExpression ("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt00)
    expected += SimpleNode(stmt00) ~> SimpleNode(stmt01)
    expected += SimpleNode(stmt01) ~> SimpleNode(stmt02)
    expected += SimpleNode(stmt02) ~> SimpleNode(stmt03)
    expected += SimpleNode(stmt03) ~> SimpleNode(stmt04)
    expected += SimpleNode(stmt03) ~> SimpleNode(stmt05)
    expected += SimpleNode(stmt04) ~> SimpleNode(stmt03)
    expected += SimpleNode(stmt04) ~> SimpleNode(stmt05)
    expected += SimpleNode(stmt05) ~> EndNode()

    val statements = List(stmt00, stmt01, stmt02, stmt03, stmt04, stmt05)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(7,statements))

    assert( 8 == g.nodes.size)
    assert( 9 == g.edges.size)
    assert( expected == g)
  }

  test("Test control flow graph of RepeatUntil 05 with 2 expression and 1 condition ") {
    val stmt2_1 = AssignmentStmt(1,"max", VarExpression("x"))
    val stmt0 = ReadIntStmt(2,"x")
    val stmt1 = ReadIntStmt(3,"max")
    val stmt2 = IfElseStmt(4,GTExpression(VarExpression("x"), VarExpression("max")), stmt2_1 , None)
    val stmt3 = AssignmentStmt(5,"x", SubExpression(VarExpression("x"), IntValue(1)))
    val stmt4 = RepeatUntilStmt(6,LTExpression(VarExpression("x"), IntValue(10)), stmt3)
    val stmt5 = WriteStmt(7,VarExpression ("x"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> SimpleNode(stmt2_1)
    expected += SimpleNode(stmt2_1) ~> SimpleNode(stmt3)
    expected += SimpleNode(stmt2_1) ~> SimpleNode(stmt2_1) //gerado
    //    expected += SimpleNode(stmt2) ~> SimpleNode(stmt3)
    expected += SimpleNode(stmt3) ~> SimpleNode(stmt4)
    expected += SimpleNode(stmt3) ~> SimpleNode(stmt5)
    //    expected += SimpleNode(stmt4) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt4) ~> SimpleNode(stmt3) //gerado
    expected += SimpleNode(stmt4) ~> SimpleNode(stmt5)
    expected += SimpleNode(stmt5) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2, stmt2_1, stmt3, stmt4, stmt5)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(8,statements))

    assert( 9 == g.nodes.size)
    assert( 11 == g.edges.size)
    assert( expected == g)
  }

  /*
  MODULE SimpleModule;

    VAR
      x, y : INTEGER;
    BEGIN
      readInt(x);
      IF(x < 5) THEN
        y := 1
      ELSIF(x < 7) THEN
        y := 2
      ELSIF(x < 9) THEN
        y := 3
      END;
      write(y)
    END

  END SimpleModule.
  */

  test("Test control flow graph of IfElseIfStmt with 3 if/else if and NO else case") {

    val if_stmt = AssignmentStmt(1,"y", IntValue(1))
    val elsif1_stmt = AssignmentStmt(2,"y", IntValue(2))
    val elsif_case_1 = ElseIfStmt(3,LTExpression(VarExpression("x"), IntValue(7)), elsif1_stmt)
    val elsif2_stmt = AssignmentStmt(4,"y", IntValue(3))
    val elsif_case_2 = ElseIfStmt(5,LTExpression(VarExpression("x"), IntValue(9)), elsif2_stmt)

    val elsif = List(elsif_case_1, elsif_case_2)

    val stmt0 = ReadIntStmt(6,"x")
    val stmt1 = IfElseIfStmt(7,LTExpression(VarExpression("x"), IntValue(5)), if_stmt, elsif, None)
    val stmt2 = WriteStmt(8,VarExpression("y"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(if_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(elsif1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(elsif2_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(stmt2)
    expected += SimpleNode(if_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(elsif1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(elsif2_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(9,statements))

    assert( g.nodes.size == 8)
    assert( g.edges.size == 10)
    assert( g == expected )
  }


/*
  MODULE SimpleModule;
    
    VAR
      x, y : INTEGER;
    BEGIN
      readInt(x);
      IF(x < 5) THEN
        y := 1
      ELSIF(x < 7) THEN
        y := 2
      ELSIF(x < 9) THEN
        y := 3
      ELSE
        y := 4
      END;
      write(y)
    END

  END SimpleModule.
*/

  test("Test control flow graph of IfElseIfStmt with 3 if/else if and 1 else case") {

    val if_stmt = AssignmentStmt(1,"y", IntValue(1))
    val elsif1_stmt = AssignmentStmt(2,"y", IntValue(2))
    val elsif_case_1 = ElseIfStmt(3,LTExpression(VarExpression("x"), IntValue(7)), elsif1_stmt)
    val elsif2_stmt = AssignmentStmt(4,"y", IntValue(3))
    val elsif_case_2 = ElseIfStmt(5,LTExpression(VarExpression("x"), IntValue(9)), elsif2_stmt)
    val else_stmt = AssignmentStmt(6,"y", IntValue(4))

    val elsif = List(elsif_case_1, elsif_case_2)

    val stmt0 = ReadIntStmt(7,"x")
    val stmt1 = IfElseIfStmt(8,LTExpression(VarExpression("x"), IntValue(5)), if_stmt, elsif, Some(else_stmt))
    val stmt2 = WriteStmt(9,VarExpression("y"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(if_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(elsif1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(elsif2_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(else_stmt)
    expected += SimpleNode(if_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(elsif1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(elsif2_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(else_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(10,statements))

    assert( g.nodes.size == 9)
    assert( g.edges.size == 11)
    assert( g == expected )
  }


  /*
    MODULE SimpleModule;
      
      VAR
        x, y : INTEGER;
      BEGIN
        readInt(x);
        readInt(y);
        IF(x < 5) THEN
          IF(y < 3) THEN
            y := 0
          ELSIF(y = 3) THEN
            y := 1
          ELSIF(y > 3) THEN
            y := 2
        ELSIF(x = 5) THEN
          y := 10
        ELSE
          y := 90
        END;
        write(y)
      END

    END SimpleModule.
  */


test("Test control flow graph RepeatUntilStmt 02 - 1 Expression and 1 Condition ") {
    val stmt00 = AssignmentStmt(1,"x", IntValue(30))
    val stmt01 = AssignmentStmt(2,"y", IntValue(2))
    val stmt02 = AssignmentStmt(3,"z", DivExpression (VarExpression ("x") , VarExpression("y")))
    val stmt03 = AssignmentStmt(4,"z", AddExpression (VarExpression ("z"), IntValue(2)))
    val stmt04 = RepeatUntilStmt(5,LTExpression (VarExpression("z"), IntValue(20)), stmt03)
    val stmt05 = WriteStmt(6,VarExpression ("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt00)
    expected += SimpleNode(stmt00) ~> SimpleNode(stmt01)
    expected += SimpleNode(stmt01) ~> SimpleNode(stmt02)
    expected += SimpleNode(stmt02) ~> SimpleNode(stmt03)
    expected += SimpleNode(stmt03) ~> SimpleNode(stmt04)
    expected += SimpleNode(stmt03) ~> SimpleNode(stmt05)
    expected += SimpleNode(stmt04) ~> SimpleNode(stmt03)
    expected += SimpleNode(stmt04) ~> SimpleNode(stmt05)
    expected += SimpleNode(stmt05) ~> EndNode()

    val statements = List(stmt00, stmt01, stmt02, stmt03, stmt04, stmt05)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(7,statements))

    assert( 8 == g.nodes.size)
    assert( 9 == g.edges.size)
    assert( expected == g)
  }
}


