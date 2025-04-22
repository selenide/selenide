package com.codeborne.selenide.logevents;

import com.codeborne.selenide.impl.DurationFormat;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class ArgumentsPrinter {
  private static final DurationFormat df = new DurationFormat();

  @SuppressWarnings("ChainOfInstanceofChecks")
  static String readableArguments(Object @Nullable... args) {
    if (args == null || args.length == 0) {
      return "";
    }

    if (args.length == 1 && args[0] instanceof Object[] objectArray) {
      return arrayToString(objectArray);
    }

    if (args.length == 1 && args[0] instanceof int[] intArray) {
      return arrayToString(intArray);
    }

    return arrayToString(args);
  }

  private static String arrayToString(Object[] args) {
    var argsWithoutEmptyVarargs = argsWithoutEmptyVarargs(args);
    return switch (argsWithoutEmptyVarargs.length) {
      case 0 -> "";
      case 1 -> argToString(argsWithoutEmptyVarargs[0]);
      default -> '[' + Stream.of(argsWithoutEmptyVarargs).map(ArgumentsPrinter::argToString).collect(joining(", ")) + ']';
    };
  }

  private static @Nullable Object[] argsWithoutEmptyVarargs(@Nullable Object[] args) {
    if (args.length < 2) return args;
    Object last = args[args.length - 1];
    if (last == null || !last.getClass().isArray()) return args;

    return mergePreviousArgWithVararg(args, last);
  }

  @SuppressWarnings("ChainOfInstanceofChecks")
  private static @Nullable Object[] mergePreviousArgWithVararg(@Nullable Object[] args, Object last) {
    Object previous = args[args.length - 2];
    if (last instanceof int[] integers && previous instanceof Integer) {
      return mergeArrays(args, integers);
    }
    else if (last instanceof Object[] objects && isSameClass(previous, objects)) {
      return mergeArrays(args, objects);
    }
    else {
      return args;
    }
  }

  private static Object[] mergeArrays(@Nullable Object[] args, int[] integers) {
    Object[] mergedArgs = new Object[args.length - 1 + integers.length];
    System.arraycopy(args, 0, mergedArgs, 0, args.length - 1);
    arrayCopy(integers, mergedArgs, args.length - 1);
    return mergedArgs;
  }

  private static Object[] mergeArrays(@Nullable Object[] args, Object[] objects) {
    Object[] mergedArgs = new Object[args.length - 1 + objects.length];
    System.arraycopy(args, 0, mergedArgs, 0, args.length - 1);
    System.arraycopy(objects, 0, mergedArgs, args.length - 1, objects.length);
    return mergedArgs;
  }

  private static boolean isSameClass(@Nullable Object previous, Object[] objects) {
    return previous != null && previous.getClass() == objects.getClass().getComponentType();
  }

  private static void arrayCopy(int[] source, Object[] destination, int destinationPosition) {
    int sourceLength = source.length;
    for (int i = 0; i < sourceLength; i++) {
      destination[destinationPosition + i] = source[i];
    }
  }

  @SuppressWarnings("ChainOfInstanceofChecks")
  private static String argToString(Object arg) {
    if (arg instanceof Duration duration) {
      return df.format(duration);
    }
    if (arg instanceof Object[] array) {
      return Arrays.toString(array);
    }
    return String.valueOf(arg);
  }

  private static String arrayToString(int[] args) {
    return args.length == 1 ? String.valueOf(args[0]) : Arrays.toString(args);
  }
}
