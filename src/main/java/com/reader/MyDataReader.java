package com.reader;

import java.util.Set;

abstract public class MyDataReader<T> {
	abstract Set<String> read(T dataSource);
}