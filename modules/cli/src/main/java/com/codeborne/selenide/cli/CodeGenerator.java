package com.codeborne.selenide.cli;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Turns recorded statements into runnable Selenide source: sorted static imports followed by the
 * statements, optionally wrapped into a JUnit test class.
 */
class CodeGenerator {
  String snippet(List<RecordedStatement> statements) {
    StringBuilder sb = new StringBuilder();
    appendStaticImports(sb, collectImports(statements));
    for (RecordedStatement statement : statements) {
      sb.append(statement.code()).append('\n');
    }
    return sb.toString();
  }

  String testClass(String className, List<RecordedStatement> statements) {
    StringBuilder sb = new StringBuilder();
    sb.append("import org.junit.jupiter.api.Test;\n\n");
    appendStaticImports(sb, collectImports(statements));
    sb.append("public class ").append(className).append(" {\n");
    sb.append("  @Test\n");
    sb.append("  void test() {\n");
    for (RecordedStatement statement : statements) {
      sb.append("    ").append(statement.code()).append('\n');
    }
    sb.append("  }\n}\n");
    return sb.toString();
  }

  private static Set<String> collectImports(List<RecordedStatement> statements) {
    Set<String> imports = new TreeSet<>();
    for (RecordedStatement statement : statements) {
      imports.addAll(statement.staticImports());
    }
    return imports;
  }

  private static void appendStaticImports(StringBuilder sb, Set<String> imports) {
    for (String imp : imports) {
      sb.append("import static ").append(imp).append(";\n");
    }
    if (!imports.isEmpty()) {
      sb.append('\n');
    }
  }
}
