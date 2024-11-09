package com.codeborne.selenide.impl;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public interface NoOpsList<T> extends List<T> {
  @Override
  default boolean contains(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  default Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  default <T1> T1[] toArray(T1[] a) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean add(T selenideElement) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean addAll(Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean addAll(int index, Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  default void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  default T set(int index, T element) {
    throw new UnsupportedOperationException();
  }

  @Override
  default void add(int index, T element) {
    throw new UnsupportedOperationException();
  }

  @Override
  default T remove(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  default int indexOf(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  default int lastIndexOf(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  default ListIterator<T> listIterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  default ListIterator<T> listIterator(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  default List<T> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }
}
